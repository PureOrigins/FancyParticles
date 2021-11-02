package it.pureorigins.fancyparticles

import com.mojang.brigadier.arguments.StringArgumentType.greedyString
import it.pureorigins.framework.configuration.*
import kotlinx.serialization.Serializable
import net.minecraft.command.argument.EntityArgumentType.players

class ParticleCommands(private val config: Config) {

    val command
        get() = literal(config.commandName) {
            requiresPermission("particles.particles")
            success { source.sendFeedback(config.commandUsage?.templateText()) }
            then(setCommand)
            then(infoCommand)
            then(addCommand)
            then(removeCommand)
        }

    val setCommand
        get() = literal(config.set.commandName) {
            requiresPermission("particles.particles.set")
            success { source.sendFeedback(config.set.commandUsage?.templateText()) }
            then(argument("particle", greedyString()) {
                suggestions {
                    TODO()
                }
                success {
                    TODO()
                }
            })
        }

    val infoCommand
        get() = literal(config.info.commandName) {
            requiresPermission("particles.particles.info")
            success {
                TODO()
            }
        }

    val addCommand
        get() = literal(config.add.commandName) {
            requiresPermission("particles.particles.add")
            success { source.sendFeedback(config.add.commandUsage?.templateText()) }
            then(argument("targets", players()) {
                then(argument("particle", greedyString()) {
                    suggestions {
                        TODO()
                    }
                    success {
                        TODO()
                    }
                })
            })
        }

    val removeCommand
        get() = literal(config.remove.commandName) {
            requiresPermission("particles.particles.remove")
            success { source.sendFeedback(config.remove.commandUsage?.templateText()) }
            then(argument("targets", players()) {
                then(argument("particle", greedyString()) {
                    suggestions {
                        TODO()
                    }
                    success {
                        TODO()
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