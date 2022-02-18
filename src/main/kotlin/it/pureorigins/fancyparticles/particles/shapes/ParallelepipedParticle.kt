package it.pureorigins.fancyparticles.particles.shapes

import org.bukkit.util.Vector

class ParallelepipedParticle(
    private val x: Double = 0.0,
    private val y: Double = 0.0,
    private val z: Double = 0.0,
    private val count: Int = 1,
    private val speed: Double = 0.0
) : ParticleShape {

    override fun getPosition(iteration: Int): Vector = Vector(0,0,0)
    override fun getOffset(iteration: Int): Vector = Vector(x, y, z)
    override fun getSpeed(iteration: Int) = speed
    override fun getCount(iteration: Int) = count
}