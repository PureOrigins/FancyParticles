package it.pureorigins.fancyparticles.particles

import net.minecraft.particle.ParticleEffect
import net.minecraft.server.network.ServerPlayerEntity

fun interface ParticleComposition {
    fun getParticle(player: ServerPlayerEntity, iteration: Int): ParticleEffect
}