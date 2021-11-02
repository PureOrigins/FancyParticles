package it.pureorigins.fancyparticles.particles

data class ParticleEffect(val particles: List<Particle>) {
    constructor(vararg particles: Particle) : this(listOf(*particles))
}
