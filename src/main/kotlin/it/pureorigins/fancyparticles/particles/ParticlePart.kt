package it.pureorigins.fancyparticles.particles

import it.pureorigins.fancyparticles.PositionReference
import it.pureorigins.fancyparticles.particles.shapes.ParticleShape
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d

data class ParticlePart(
    val shape: ParticleShape,
    val positionReference: PositionReference = PositionReference.FEET,
    val delay: Long = 0,
    val period: Long = 1,
    val particleComposition: ParticleComposition = ParticleComposition { _: ServerPlayerEntity, _: Int -> ParticleTypes.NOTE },
    val positionOffset: Vec3d = Vec3d.ZERO,
    val particleActivation: ParticleActivation = ParticleActivation.ALWAYS_ACTIVE
)

infix fun ParticleShape.madeOf(particleEffect: ParticleEffect) =
    ParticlePart(this, particleComposition = { _, _ -> particleEffect })

infix fun ParticlePart.delay(delay: Long) = copy(delay = delay)
infix fun ParticlePart.period(period: Long) = copy(period = period)
infix fun ParticlePart.at(position: PositionReference) = copy(positionReference = position)
infix fun ParticlePart.at(offset: Vec3d) = copy(positionOffset = offset)
infix fun ParticlePart.atX(offsetX: Double) = copy(positionOffset = positionOffset.add(offsetX,0.0,0.0))
infix fun ParticlePart.atY(offsetY: Double) = copy(positionOffset = positionOffset.add(0.0, offsetY, 0.0))
infix fun ParticlePart.atZ(offsetZ: Double) = copy(positionOffset = positionOffset.add(0.0, 0.0, offsetZ))
infix fun ParticlePart.activeIf(particleActivation: ParticleActivation) = copy(particleActivation = particleActivation)