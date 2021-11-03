package it.pureorigins.fancyparticles.particles

data class ParticleEffect(val particleParts: List<ParticlePart>) {
    constructor(vararg parts: ParticlePart) : this(listOf(*parts))
}
