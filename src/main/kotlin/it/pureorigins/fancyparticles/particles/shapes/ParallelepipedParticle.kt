package it.pureorigins.fancyparticles.particles.shapes

import net.minecraft.util.math.Vec3d

class ParallelepipedParticle(
    private val x: Double = 0.2,
    private val y: Double = 0.0,
    private val z: Double = 0.2,
    private val count: Int = 1
) : ParticleShape {

    override fun getPosition(iteration: Int): Vec3d = Vec3d.ZERO
    override fun getOffset(iteration: Int): Vec3d = Vec3d(x, y, z)
    override fun getSpeed(iteration: Int) = 0
    override fun getCount(iteration: Int) = count
}