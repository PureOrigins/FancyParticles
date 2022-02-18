package it.pureorigins.fancyparticles.particles.shapes

import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

class OrbitalParticle(
    private val r: Double = 1.0,
    private val initAlpha: Double = 0.0,
    private val angularSpeed: Double = 0.1
) :
    ParticleShape {

    override fun getPosition(iteration: Int) =
        Vector(r * cos(angularSpeed * iteration + initAlpha), 0.0, r * sin(angularSpeed * iteration + initAlpha))

    override fun getOffset(iteration: Int): Vector = Vector(0,0,0)
    override fun getSpeed(iteration: Int) = 0.0
    override fun getCount(iteration: Int) = 1
}