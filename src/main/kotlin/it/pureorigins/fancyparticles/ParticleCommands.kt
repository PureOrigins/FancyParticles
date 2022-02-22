package it.pureorigins.fancyparticles

import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.greedyString
import it.pureorigins.common.*
import kotlinx.serialization.Serializable
import net.minecraft.commands.arguments.EntityArgument.getPlayers
import net.minecraft.commands.arguments.EntityArgument.players
import org.apache.logging.log4j.LogManager
import org.bukkit.entity.Player


class ParticleCommands(private val config: Config) {

    val command
        get() = literal(config.commandName) {
            requiresPermission("fancyparticles.particles")
            success { source.bukkitSender.sendMessage(config.commandUsage?.templateText()) }
            then(setCommand)
            then(infoCommand)
            then(addCommand)
            then(removeCommand)
        }

    val setCommand
        get() = literal(config.set.commandName) {
            requiresPermission("fancyparticles.particles.set")
            success { source.bukkitSender.sendMessage(config.set.commandUsage?.templateText()) }
            then(argument("particle", greedyString()) {
                suggestions {
                    FancyParticles.getPlayerParticles((source.bukkitSender as Player).uniqueId)
                        .map { (_, particle) -> particle.name } + config.nullparticleName
                }
                success {
                    val particleName = getString(this, "particle")
                    if (particleName == config.nullparticleName) {
                        FancyParticles.setCurrentParticle((source.bukkitSender as Player).uniqueId, null)
                        FancyParticles.clearTasks(source.bukkitSender as Player)
                        return@success source.bukkitSender.sendMessage(config.set.success?.templateText("particle" to null))
                    }
                    val (id, particle) = FancyParticles.getParticle(particleName)
                        ?: return@success source.bukkitSender.sendMessage(config.set.particleNotFound?.templateText())
                    val playerparticles = FancyParticles.getPlayerParticles((source.bukkitSender as Player).uniqueId)
                    if (id !in playerparticles) return@success source.bukkitSender.sendMessage(
                        config.set.particleNotOwned?.templateText(
                            "particle" to particle
                        )
                    )
                    FancyParticles.setCurrentParticle((source.bukkitSender as Player).uniqueId, id)
                    LogManager.getLogger().info("${source.bukkitSender.name} set particle to $particle")
                    FancyParticles.scheduleParticle(particle.particleEffect, source.bukkitSender as Player)
                    source.bukkitSender.sendMessage(config.set.success?.templateText("particle" to particle))
                }
            })
        }

    val infoCommand
        get() = literal(config.info.commandName) {
            requiresPermission("fancyparticles.particles.info")
            success {
                val particles = FancyParticles.getPlayerParticles((source.bukkitSender as Player).uniqueId).values
                val currentparticle = FancyParticles.getCurrentParticle((source.bukkitSender as Player).uniqueId)?.second
                source.bukkitSender.sendMessage(
                    config.info.message?.templateText(
                        "particles" to particles,
                        "currentparticle" to currentparticle
                    )
                )
            }
        }

    val addCommand
        get() = literal(config.add.commandName) {
            requiresPermission("fancyparticles.particles.add")
            success { source.bukkitSender.sendMessage(config.add.commandUsage?.templateText()) }
            then(argument("targets", players()) {
                then(argument("particle", greedyString()) {
                    suggestions {
                        FancyParticles.getAllNames()
                    }
                    success {
                        val players = getPlayers(this, "targets")
                        val particleName = getString(this, "particle")
                        val (id, particle) = FancyParticles.getParticle(particleName) ?: return@success source.bukkitSender.sendMessage(config.add.particleNotFound?.templateText())
                        val success = players.filter { FancyParticles.addParticle(it.uuid, id) }
                        if (success.isEmpty()) source.bukkitSender.sendMessage(config.add.particleAlreadyOwned?.templateText("particle" to particle))
                        else source.bukkitSender.sendMessage(config.add.success?.templateText("particle" to particle, "players" to players))
                    }
                })
            })
        }

    val removeCommand
        get() = literal(config.remove.commandName) {
            requiresPermission("fancyparticles.particles.remove")
            success { source.bukkitSender.sendMessage(config.remove.commandUsage?.templateText()) }
            then(argument("targets", players()) {
                then(argument("particle", greedyString()) {
                    suggestions {
                        FancyParticles.getAllNames()
                    }
                    success {
                        val players = getPlayers(this, "targets")
                        val particleName = getString(this, "particle")
                        val (id, particle) = FancyParticles.getParticle(particleName) ?: return@success source.bukkitSender.sendMessage(config.remove.particleNotFound?.templateText())
                        val success = players.filter {
                            FancyParticles.removeParticle(it.uuid, id)
                        }
                        if (success.isEmpty()) source.bukkitSender.sendMessage(config.remove.particleNotOwned?.templateText("particle" to particle))
                        else source.bukkitSender.sendMessage(config.remove.success?.templateText("particle" to particle, "players" to players))
                    }
                })
            })
        }

    @Serializable
    data class Config(
        val commandName: String = "particles",
        val commandUsage: String? = "[{\"text\": \"Usage: \", \"color\": \"dark_gray\"}, {\"text\": \"/$commandName <set | info>\", \"color\": \"gray\"}]",
        val nullparticleName: String = "none",
        val set: Set = Set(),
        val info: Info = Info(),
        val add: Add = Add(),
        val remove: Remove = Remove()
    ) {
        @Serializable
        data class Set(
            val commandName: String = "set",
            val commandUsage: String? = "[{\"text\": \"Usage: \", \"color\": \"dark_gray\"}, {\"text\": \"/particles set <particle | none>\", \"color\": \"gray\"}]",
            val particleNotFound: String? = "{\"text\": \"Particle not found.\", \"color\": \"dark_gray\"}",
            val particleNotOwned: String? = "{\"text\": \"particle not owned.\", \"color\": \"dark_gray\"}",
            val success: String? = "{\"text\": \"particle set.\", \"color\": \"gray\"}"
        )

        @Serializable
        data class Info(
            val commandName: String = "info",
            val message: String? = "[{\"text\": \"Current particle: \", \"color\": \"gray\"}, <#if currentparticle??>{\"particle\": \"\${currentparticle.name}\"}<#else>{\"text\": \"none\", \"color\": \"gray\"}</#if>, {\"text\": \"\\n\"}, {\"text\": \"\${particles?size} Owned particle<#if particles?size != 0>s</#if>: \", \"color\": \"gray\"}, <#list particles as particle>{\"particle\": \"\${particle.name}\"}<#sep>,{\"text\": \", \", \"color\": \"gray\"},</#list>]"
        )

        @Serializable
        data class Add(
            val commandName: String = "add",
            val commandUsage: String? = "[{\"text\": \"Usage: \", \"color\": \"dark_gray\"}, {\"text\": \"/particles add <targets> <particle>\", \"color\": \"gray\"}]",
            val particleNotFound: String? = "{\"text\": \"particle not found.\", \"color\": \"dark_gray\"}",
            val particleAlreadyOwned: String? = "{\"text\": \"particle already owned.\", \"color\": \"dark_gray\"}",
            val success: String? = "{\"text\": \"\${players?size} particle<#if players?size != 0>s</#if> added.\", \"color\": \"gray\"}"
        )

        @Serializable
        data class Remove(
            val commandName: String = "remove",
            val commandUsage: String? = "[{\"text\": \"Usage: \", \"color\": \"dark_gray\"}, {\"text\": \"/particles remove <targets> <particle>\", \"color\": \"gray\"}]",
            val particleNotFound: String? = "{\"text\": \"particle not found.\", \"color\": \"dark_gray\"}",
            val particleNotOwned: String? = "{\"text\": \"particle not owned.\", \"color\": \"dark_gray\"}",
            val success: String? = "{\"text\": \"\${players?size} particle<#if players?size != 0>s</#if> removed.\", \"color\": \"gray\"}"
        )
    }
}