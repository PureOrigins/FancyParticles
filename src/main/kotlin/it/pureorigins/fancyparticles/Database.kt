package it.pureorigins.fancyparticles

import it.pureorigins.fancyparticles.particles.NamedParticleEffect
import it.pureorigins.fancyparticles.particles.ParticleEffects
import net.minecraft.text.Text
import org.jetbrains.exposed.sql.*
import java.util.*

object PlayersTable : Table("players") {
    val uniqueId = uuid("uuid")
    val currentParticleId = (integer("current_particle_id") references ParticlesTable.id).nullable()

    override val primaryKey = PrimaryKey(uniqueId)

    fun count(): Long = selectAll().count()
    fun getCurrentParticle(uniqueId: UUID): Pair<Int, NamedParticleEffect>? =
        innerJoin(ParticlesTable).select { PlayersTable.uniqueId eq uniqueId }.singleOrNull()?.toNamedParticleEffect()

    fun setCurrentParticle(uniqueId: UUID, particleId: Int?): Boolean {
        insertIgnore {
            it[PlayersTable.uniqueId] = uniqueId
            it[currentParticleId] = null
        }
        return update({ PlayersTable.uniqueId eq uniqueId }) {
            it[currentParticleId] = particleId
        } > 0
    }
}

private fun ResultRow.toNamedParticleEffect() = get(ParticlesTable.id) to NamedParticleEffect(
    0,
    get(ParticlesTable.name),
    Text.of(get(ParticlesTable.title)),
    ParticleEffects.fromString(ParticlesTable.string_id.toString())
)

object PlayerParticlesTable : Table("player_particles_table") {
    val uniqueId = uuid("player_id") references PlayersTable.uniqueId
    val particleId = integer("particle_id") references ParticlesTable.id
    override val primaryKey = PrimaryKey(uniqueId, particleId)

    fun getParticles(uniqueId: UUID): Map<Int, NamedParticleEffect> =
        innerJoin(ParticlesTable).select { PlayerParticlesTable.uniqueId eq uniqueId }
            .associate { it.toNamedParticleEffect() }

    fun add(playerUniqueId: UUID, particleId: Int): Boolean = insertIgnore {
        it[PlayerParticlesTable.uniqueId] = playerUniqueId
        it[PlayerParticlesTable.particleId] = particleId
    }.insertedCount > 0

    fun remove(playerUniqueId: UUID, particleId: Int): Boolean = deleteWhere {
        (PlayerParticlesTable.uniqueId eq playerUniqueId) and (PlayerParticlesTable.particleId eq particleId)
    } > 0
}

object ParticlesTable : Table("particles_table") {
    val id = integer("id")
    val name = varchar("name", length = 50)
    val title = varchar("title", length = 50)
    val string_id = varchar("string_id", length = 50)
    override val primaryKey = PrimaryKey(id)

    fun getAllNames(): Set<String> = selectAll().mapTo(HashSet()) { it[name] }

    fun getById(id: Int): NamedParticleEffect? =
        select { ParticlesTable.id eq id }.singleOrNull()?.toNamedParticleEffect()?.second

    fun getByName(name: String): Pair<Int, NamedParticleEffect>? =
        select { ParticlesTable.name eq name }.singleOrNull()?.toNamedParticleEffect()

    fun add(particle: NamedParticleEffect): Int = insert {
        it[name] = particle.name
        it[string_id] = particle.particleEffect.stringId
        it[title] = Text.Serializer.toJson(particle.title)
    } get id

    fun remove(id: Int): Boolean = deleteWhere { ParticlesTable.id eq id } > 0
    fun update(id: Int, particle: NamedParticleEffect): Boolean = update({ ParticlesTable.id eq id }) {
        it[name] = particle.name
        it[string_id] = particle.particleEffect.stringId
        it[title] = Text.Serializer.toJson(particle.title)
    } > 0
}