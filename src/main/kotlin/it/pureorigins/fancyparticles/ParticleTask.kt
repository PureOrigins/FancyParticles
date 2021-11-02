package it.pureorigins.fancyparticles

import it.pureorigins.fancyparticles.particles.Particle
import net.minecraft.server.network.ServerPlayerEntity

class ParticleTask(var particle: Particle, var player: ServerPlayerEntity) : Runnable {

    var i: Int = 0

    override fun run() {
        //TODO add if(particle.isActive())

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

    }

}