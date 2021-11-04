package it.pureorigins.fancyparticles

import it.pureorigins.fancyparticles.particles.ParticlePart
import net.minecraft.server.network.ServerPlayerEntity

class ParticleTask(private val particlePart: ParticlePart, private val player: ServerPlayerEntity) : Runnable {

    var i: Int = 0

    override fun run() {
        if(particlePart.particleActivation.isActive(player)) {
            val pos = particlePart.shape.getPosition(i).add(particlePart.position.getPosition(player))
            val offset = particlePart.shape.getOffset(i)
            for (p in player.serverWorld.players)
                player.serverWorld.spawnParticles(
                    p,
                    particlePart.particleComposition.getParticle(player, i),
                    true,
                    pos.x,
                    pos.y,
                    pos.z,
                    particlePart.shape.getCount(i),
                    offset.x,
                    offset.y,
                    offset.z,
                    particlePart.shape.getSpeed(i).toDouble()
                )
            i++
        } else i = 0
    }

}