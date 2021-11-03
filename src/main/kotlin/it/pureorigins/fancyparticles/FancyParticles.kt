package it.pureorigins.fancyparticles

import com.mojang.brigadier.CommandDispatcher
import it.pureorigins.fancyparticles.particles.ParticleEffect
import it.pureorigins.fancyparticles.particles.ParticleEffects
import it.pureorigins.framework.configuration.literal
import it.pureorigins.framework.configuration.success
import kotlinx.serialization.Serializable
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import org.jetbrains.exposed.sql.Database
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

object FancyParticles : ModInitializer {

    fun getParticles(): List<ParticleEffect> =
        TODO()

    private val playerTasks = HashMap<UUID, ArrayList<ScheduledFuture<*>>>()


    lateinit var scheduler: ScheduledExecutorService
    internal lateinit var database: Database private set
    override fun onInitialize() {
        scheduler = Executors.newScheduledThreadPool(4)

        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { d: CommandDispatcher<ServerCommandSource?>, _: Boolean ->
            d.register(literal("fancy") { success { scheduleParticle(ParticleEffects.CLOUD, source.player) } })
            d.register(literal("fancy1") { success { scheduleParticle(ParticleEffects.MUSIC, source.player) } })
            d.register(literal("fancy2") { success { scheduleParticle(ParticleEffects.LOVE, source.player) } })
        })

        ServerPlayConnectionEvents.DISCONNECT.register { handler: ServerPlayNetworkHandler, _: MinecraftServer ->
            clearTasks(handler.player)
        }

    }

    private fun scheduleParticle(particleEffect: ParticleEffect, player: ServerPlayerEntity) {
        clearTasks(player)
        playerTasks[player.uuid] = ArrayList()
        for (part in particleEffect.particleParts) {
            val task = ParticleTask(part, player)
            val t = scheduler.scheduleAtFixedRate(task, part.delay * 50, part.period * 50, TimeUnit.MILLISECONDS)
            playerTasks[player.uuid]?.add(t)
        }
    }

    private fun clearTasks(player: ServerPlayerEntity) {
        if (playerTasks.containsKey(player.uuid))
            for (t in playerTasks[player.uuid]!!) t.cancel(false)
        playerTasks.remove(player.uuid)
    }

    @Serializable
    data class Config(
        val database: Database = Database(),
        val commands: ParticleCommands.Config = ParticleCommands.Config()
    ) {
        @Serializable
        data class Database(
            val url: String = "",
            val username: String = "",
            val password: String = ""
        )
    }
}

