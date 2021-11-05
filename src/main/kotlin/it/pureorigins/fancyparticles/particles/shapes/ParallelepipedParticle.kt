package it.pureorigins.fancyparticles.particles.shapes

import net.minecraft.util.math.Vec3d

class ParallelepipedParticle(
    private val x: Double = 0.0,
    private val y: Double = 0.0,
    private val z: Double = 0.0,
    private val count: Int = 1,
    private val speed: Double = 0.0
) : ParticleShape {

    override fun getPosition(iteration: Int): Vec3d = Vec3d.ZERO
    override fun getOffset(iteration: Int): Vec3d = Vec3d(x, y, z)
    override fun getSpeed(iteration: Int) = speed
    override fun getCount(iteration: Int) = count
}