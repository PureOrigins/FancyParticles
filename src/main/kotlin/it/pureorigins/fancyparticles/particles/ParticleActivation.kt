package it.pureorigins.fancyparticles.particles

import net.minecraft.server.network.ServerPlayerEntity

fun interface ParticleActivation {
    fun isActive(player: ServerPlayerEntity): Boolean

    companion object {
        val ALWAYS_ACTIVE = ParticleActivation { true }
        val WHEN_MOVING = ParticleActivation { TODO() }
        val WHEN_STANDING = ParticleActivation { !WHEN_MOVING.isActive(it) }
    }
}