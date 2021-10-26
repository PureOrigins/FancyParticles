package it.pureorigins.fancyparticles

import it.pureorigins.fancyparticles.particles.Particle
import it.pureorigins.fancyparticles.particles.SandParticle
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class FancyParticles : ModInitializer {

    lateinit var scheduler: ScheduledExecutorService
    override fun onInitialize() {
        scheduler = Executors.newScheduledThreadPool(4)

        ServerPlayConnectionEvents.JOIN.register { handler: ServerPlayNetworkHandler, _: PacketSender, _: MinecraftServer ->
            scheduleParticle(SandParticle,handler.player)
        }
    }

    private fun scheduleParticle(particle:Particle, player:ServerPlayerEntity) {
        scheduler.scheduleAtFixedRate(ParticleTask(particle, player), particle.delay/20, particle.period/20, TimeUnit.SECONDS)
    }
}
