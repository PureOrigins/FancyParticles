package it.pureorigins.fancyparticles

import it.pureorigins.fancyparticles.particles.ParticlePart
import org.bukkit.entity.Player

class ParticleTask(private val particlePart: ParticlePart, private val player: Player) : Runnable {

    var i: Int = 0

    override fun run() {
        if (particlePart.particleActivation.isActive(player)) {
            val pos = particlePart.shape.getPosition(i)
                .add(particlePart.positionReference.getPosition(player))
                .add(particlePart.positionOffset)
            val offset = particlePart.shape.getOffset(i)
            player.world.spawnParticle(
                particlePart.particleComposition.getParticle(player, i),
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