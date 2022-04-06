package it.pureorigins.pureparticles

import it.pureorigins.common.runTaskTimer
import it.pureorigins.pureparticles.particles.ParticleEffect
import it.pureorigins.pureparticles.particles.ParticlePart
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

class ParticleTask(val effect: ParticleEffect, val player: Player) : Runnable {
    private lateinit var tasks: List<BukkitTask>
    
    override fun run() {
        tasks = effect.particleParts.map { plugin.runTaskTimer(it.delay, it.period, ParticlePartTask(it, player)::run) }
    }
    
    fun cancel() {
        tasks.forEach { it.cancel() }
    }
}

class ParticlePartTask(private val particle: ParticlePart, private val player: Player) : Runnable {
    private var i = 0
    
    override fun run(): Unit = with(particle) {
        if (particleActivation.isActive(player)) {
            val pos = shape.getPosition(i)
                .add(positionOffset)
                .add(positionReference.getPosition(player))
            val offset = shape.getOffset(i)
            player.world.spawnParticle(
                particleComposition.getParticle(player, i),
                pos.x,
                pos.y,
                pos.z,
                shape.getCount(i),
                offset.x,
                offset.y,
                offset.z,
                shape.getSpeed(i)
            )
            i++
        } else i = 0
    }
}