package it.pureorigins.fancyparticles

import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.greedyString
import it.pureorigins.framework.configuration.*
import kotlinx.serialization.Serializable
import net.minecraft.command.argument.EntityArgumentType.getPlayers
import net.minecraft.command.argument.EntityArgumentType.players

class ParticleCommands(private val config: Config) {

    val command
        get() = literal(config.commandName) {
            requiresPermission("fancyparticles.particles")
            success { source.sendFeedback(config.commandUsage?.templateText()) }
            then(setCommand)
            then(infoCommand)
            then(addCommand)
            then(removeCommand)
        }

    val setCommand
        get() = literal(config.set.commandName) {
            requiresPermission("fancyparticles.particles.set")
            success { source.sendFeedback(config.set.commandUsage?.templateText()) }
            then(argument("particle", greedyString()) {
                suggestions {
                    FancyParticles.getPlayerParticles(source.player.uuid)
                        .map { (_, particle) -> particle.name } + config.nullparticleName
                }
                success {
                    val titleName = getString(this, "particle")
                    if (titleName == config.nullparticleName) {
                        FancyParticles.setCurrentParticle(source.player.uuid, null)
                        return@success source.sendFeedback(config.set.success?.templateText("particle" to null))
                    }
                    val (id, title) = FancyParticles.getParticle(titleName)
                        ?: return@success source.sendFeedback(config.set.particleNotFound?.templateText())
                    val playerTitles = FancyParticles.getPlayerParticles(source.player.uuid)
                    if (id !in playerTitles) return@success source.sendFeedback(
                        config.set.particleNotOwned?.templateText(
                            "title" to title
                        )
                    )
                    FancyParticles.setCurrentParticle(source.player.uuid, id)
                    source.sendFeedback(config.set.success?.templateText("title" to title))
                }
            })
        }

    val infoCommand
        get() = literal(config.info.commandName) {
            requiresPermission("fancyparticles.particles.info")
            success {
                val titles = FancyParticles.getPlayerParticles(source.player.uuid).values
                val currentTitle = FancyParticles.getCurrentParticle(source.player.uuid)?.second
                source.sendFeedback(
                    config.info.message?.templateText(
                        "titles" to titles,
                        "currentTitle" to currentTitle
                    )
                )
            }
        }

    val addCommand
        get() = literal(config.add.commandName) {
            requiresPermission("fancyparticles.particles.add")
            success { source.sendFeedback(config.add.commandUsage?.templateText()) }
            then(argument("targets", players()) {
                then(argument("particle", greedyString()) {
                    suggestions {
                        FancyParticles.getAllNames()
                    }
                    success {
                        val players = getPlayers(this, "targets")
                        val titleName = getString(this, "title")
                        val (id, title) = FancyParticles.getParticle(titleName) ?: return@success source.sendFeedback(config.add.particleNotFound?.templateText())
                        val success = players.filter { FancyParticles.addParticle(it.uuid, id) }
                        if (success.isEmpty()) source.sendFeedback(config.add.particleAlreadyOwned?.templateText("title" to title))
                        else source.sendFeedback(config.add.success?.templateText("title" to title, "players" to players))
                    }
                })
            })
        }

    val removeCommand
        get() = literal(config.remove.commandName) {
            requiresPermission("fancyparticles.particles.remove")
            success { source.sendFeedback(config.remove.commandUsage?.templateText()) }
            then(argument("targets", players()) {
                then(argument("particle", greedyString()) {
                    suggestions {
                        FancyParticles.getAllNames()
                    }
                    success {
                        val players = getPlayers(this, "targets")
                        val titleName = getString(this, "title")
                        val (id, title) = FancyParticles.getParticle(titleName) ?: return@success source.sendFeedback(config.remove.particleNotFound?.templateText())
                        val success = players.filter {
                            FancyParticles.removeParticle(it.uuid, id)
                        }
                        if (success.isEmpty()) source.sendFeedback(config.remove.particleNotOwned?.templateText("title" to title))
                        else source.sendFeedback(config.remove.success?.templateText("title" to title, "players" to players))
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