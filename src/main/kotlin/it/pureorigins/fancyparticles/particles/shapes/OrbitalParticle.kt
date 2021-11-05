package it.pureorigins.fancyparticles.particles.shapes

import net.minecraft.util.math.Vec3d
import kotlin.math.cos
import kotlin.math.sin

class OrbitalParticle(
    private val r: Double = 1.0,
    private val initAlpha: Double = 0.0,
    private val angularSpeed: Double = 0.1
) :
    ParticleShape {

    override fun getPosition(iteration: Int) =
        Vec3d(r * cos(angularSpeed * iteration + initAlpha), 0.0, r * sin(angularSpeed * iteration + initAlpha))

    override fun getOffset(iteration: Int): Vec3d = Vec3d.ZERO
    override fun getSpeed(iteration: Int) = 0.0
    override fun getCount(iteration: Int) = 1
}