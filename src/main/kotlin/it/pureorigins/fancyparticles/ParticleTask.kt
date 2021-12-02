package it.pureorigins.fancyparticles

import it.pureorigins.fancyparticles.particles.ParticlePart
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld

class ParticleTask(private val particlePart: ParticlePart, private val player: ServerPlayerEntity) : Runnable {

    var i: Int = 0

    override fun run() {
        if (particlePart.particleActivation.isActive(player)) {
            val pos = particlePart.shape.getPosition(i)
                .add(particlePart.positionReference.getPosition(player))
                .add(particlePart.positionOffset)
            val offset = particlePart.shape.getOffset(i)
            for (p in player.world.players)
                (player.world as ServerWorld).spawnParticles(
                    p as ServerPlayerEntity,
                    particlePart.particleComposition.getParticle(player, i),
                    true,
                    pos.x,
                    pos.y,
                    pos.z,
                    particlePart.shape.getCount(i),
                    offset.x,
                    offset.y,
                    offset.z,
                    particlePart.shape.getSpeed(i)
                )
            i++
        } else i = 0
    }
}