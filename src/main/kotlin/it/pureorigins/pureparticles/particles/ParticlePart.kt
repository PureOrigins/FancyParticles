package it.pureorigins.pureparticles.particles

import it.pureorigins.pureparticles.PositionReference
import it.pureorigins.pureparticles.particles.shapes.ParticleShape
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.util.Vector

data class ParticlePart(
    val shape: ParticleShape,
    val positionReference: PositionReference = PositionReference.FEET,
    val delay: Long = 0,
    val period: Long = 1,
    val particleComposition: ParticleComposition = ParticleComposition { _: Player, _: Int -> Particle.NOTE },
    val positionOffset: Vector = Vector(0,0,0),
    val particleActivation: ParticleActivation = ParticleActivation.ALWAYS_ACTIVE
)

infix fun ParticleShape.madeOf(particle: Particle) =
    ParticlePart(this, particleComposition = { _, _ -> particle })

infix fun ParticlePart.delay(delay: Long) = copy(delay = delay)
infix fun ParticlePart.period(period: Long) = copy(period = period)
infix fun ParticlePart.at(position: PositionReference) = copy(positionReference = position)
infix fun ParticlePart.at(offset: Vector) = copy(positionOffset = offset)
infix fun ParticlePart.atX(offsetX: Double) = copy(positionOffset = positionOffset.add(Vector(offsetX,0.0,0.0)))
infix fun ParticlePart.atY(offsetY: Double) = copy(positionOffset = positionOffset.add(Vector(0.0, offsetY, 0.0)))
infix fun ParticlePart.atZ(offsetZ: Double) = copy(positionOffset = positionOffset.add(Vector(0.0, 0.0, offsetZ)))
infix fun ParticlePart.activeIf(particleActivation: ParticleActivation) = copy(particleActivation = particleActivation)