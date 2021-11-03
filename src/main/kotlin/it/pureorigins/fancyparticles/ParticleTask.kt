package it.pureorigins.fancyparticles

import it.pureorigins.fancyparticles.particles.Particle
import net.minecraft.server.network.ServerPlayerEntity

class ParticleTask(val particle: Particle, val player: ServerPlayerEntity) : Runnable {

    var i: Int = 0

    override fun run() {
        if(particle.isActive(player)) {
            val pos = particle.getPosition(i).add(particle.positionOffset.getPosition(player))
            val offset = particle.getOffset(i)
            for (p in player.serverWorld.players)
                player.serverWorld.spawnParticles(
                    p,
                    particle.getParticle(i),
                    true,
                    pos.x,
                    pos.y,
                    pos.z,
                    particle.getCount(i),
                    offset.x,
                    offset.y,
                    offset.z,
                    particle.getSpeed(i).toDouble()
                )
            i++
        } else i = 0
    }

}