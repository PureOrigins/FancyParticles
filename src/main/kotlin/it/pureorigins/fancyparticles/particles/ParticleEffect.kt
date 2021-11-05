package it.pureorigins.fancyparticles.particles

data class ParticleEffect(val stringId:String = "", val particleParts: List<ParticlePart>) {
    constructor(stringId:String = "", vararg parts: ParticlePart) : this(stringId, listOf(*parts))
}
