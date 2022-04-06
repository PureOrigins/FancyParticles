package it.pureorigins.pureparticles

import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.greedyString
import it.pureorigins.common.*
import kotlinx.serialization.Serializable
import net.minecraft.commands.arguments.EntityArgument.getPlayers
import net.minecraft.commands.arguments.EntityArgument.players
import org.bukkit.entity.Player


class ParticleCommands(private val config: Config) {

    val command
        get() = literal(config.commandName) {
            requiresPermission("pureparticles.particles")
            success { source.sendNullableMessage(config.commandUsage?.templateText()) }
            then(setCommand)
            then(infoCommand)
            then(addCommand)
            then(removeCommand)
        }

    val setCommand
        get() = literal(config.set.commandName) {
            requiresPermission("pureparticles.particles.set")
            success { source.sendNullableMessage(config.set.commandUsage?.templateText()) }
            then(argument("particle", greedyString()) {
                suggestions {
                    plugin.getPlayerParticles(source.player.uniqueId)
                        .map { (_, particle) -> particle.name } + config.nullParticleName
                }
                success {
                    val particleName = getString(this, "particle")
                    if (particleName == config.nullParticleName) {
                        plugin.setCurrentParticle(source.player.uniqueId, null)
                        plugin.clearTasks(source.bukkitSender as Player)
                        return@success source.sendNullableMessage(config.set.success?.templateText("particle" to null))
                    }
                    val (id, particle) = plugin.getParticle(particleName)
                        ?: return@success source.sendNullableMessage(config.set.particleNotFound?.templateText())
                    val particles = plugin.getPlayerParticles(source.player.uniqueId)
                    if (id !in particles) return@success source.sendNullableMessage(
                        config.set.particleNotOwned?.templateText(
                            "particle" to particle
                        )
                    )
                    plugin.setCurrentParticle(source.player.uniqueId, id)
                    plugin.scheduleParticle(source.player, particle.particleEffect)
                    source.sendNullableMessage(config.set.success?.templateText("particle" to particle))
                }
            })
        }

    val infoCommand
        get() = literal(config.info.commandName) {
            requiresPermission("pureparticles.particles.info")
            success {
                val particles = plugin.getPlayerParticles(source.player.uniqueId).values
                val currentParticles = plugin.getCurrentParticle(source.player.uniqueId)?.second
                source.sendNullableMessage(
                    config.info.message?.templateText(
                        "particles" to particles,
                        "current" to currentParticles
                    )
                )
            }
        }

    val addCommand
        get() = literal(config.add.commandName) {
            requiresPermission("pureparticles.particles.add")
            success { source.sendNullableMessage(config.add.commandUsage?.templateText()) }
            then(argument("targets", players()) {
                then(argument("particle", greedyString()) {
                    suggestions {
                        plugin.getAllNames()
                    }
                    success {
                        val players = getPlayers(this, "targets")
                        val particleName = getString(this, "particle")
                        val (id, particle) = plugin.getParticle(particleName) ?: return@success source.sendNullableMessage(config.add.particleNotFound?.templateText())
                        val success = players.filter { plugin.addParticle(it.uuid, id) }
                        if (success.isEmpty()) source.sendNullableMessage(config.add.particleAlreadyOwned?.templateText("particle" to particle))
                        else source.sendNullableMessage(config.add.success?.templateText("particle" to particle, "players" to players))
                    }
                })
            })
        }

    val removeCommand
        get() = literal(config.remove.commandName) {
            requiresPermission("pureparticles.particles.remove")
            success { source.sendNullableMessage(config.remove.commandUsage?.templateText()) }
            then(argument("targets", players()) {
                then(argument("particle", greedyString()) {
                    suggestions {
                        plugin.getAllNames()
                    }
                    success {
                        val players = getPlayers(this, "targets")
                        val particleName = getString(this, "particle")
                        val (id, particle) = plugin.getParticle(particleName) ?: return@success source.sendNullableMessage(config.remove.particleNotFound?.templateText())
                        val success = players.filter {
                            plugin.removeParticle(it.uuid, id)
                        }
                        if (success.isEmpty()) source.sendNullableMessage(config.remove.particleNotOwned?.templateText("particle" to particle))
                        else source.sendNullableMessage(config.remove.success?.templateText("particle" to particle, "players" to players))
                    }
                })
            })
        }

    @Serializable
    data class Config(
        val commandName: String = "particles",
        val commandUsage: String? = "[{\"text\": \"Usage: \", \"color\": \"dark_gray\"}, {\"text\": \"/$commandName <set | info>\", \"color\": \"gray\"}]",
        val nullParticleName: String = "none",
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
            val message: String? = "[{\"text\": \"Current particle: \", \"color\": \"gray\"}, <#if current??>{\"particle\": \"\${current.name}\"}<#else>{\"text\": \"none\", \"color\": \"gray\"}</#if>, {\"text\": \"\\n\"}, {\"text\": \"\${particles?size} Owned particle<#if particles?size != 0>s</#if>: \", \"color\": \"gray\"}, <#list particles as particle>{\"particle\": \"\${particle.name}\"}<#sep>,{\"text\": \", \", \"color\": \"gray\"},</#list>]"
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