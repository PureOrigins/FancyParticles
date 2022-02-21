package it.pureorigins.fancyparticles

import it.pureorigins.common.json
import it.pureorigins.common.readFileAs
import it.pureorigins.fancyparticles.particles.NamedParticleEffect
import it.pureorigins.fancyparticles.particles.ParticleEffect
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


object FancyParticles : JavaPlugin() {

    private val playerTasks = HashMap<UUID, ArrayList<ScheduledFuture<*>>>()
    fun getParticle(id: Int): NamedParticleEffect? = transaction(database) { ParticlesTable.getById(id) }
    fun getParticle(name: String): Pair<Int, NamedParticleEffect>? =
        transaction(database) { ParticlesTable.getByName(name) }
    fun getAllNames(): Set<String> = transaction(database) { ParticlesTable.getAllNames() }
    fun addParticle(playerUniqueId: UUID, particleId: Int): Boolean = transaction(database) { PlayerParticlesTable.add(playerUniqueId, particleId) }
    fun removeParticle(playerUniqueId: UUID, particleId: Int): Boolean = transaction(database) { PlayerParticlesTable.remove(playerUniqueId, particleId) }
    fun getPlayerParticles(playerUniqueId: UUID): Map<Int, NamedParticleEffect> =
        transaction(database) { PlayerParticlesTable.getParticles(playerUniqueId) }

    fun getPlayersCount(): Long = transaction(database) { PlayersTable.count() }
    fun getCurrentParticle(playerUniqueId: UUID): Pair<Int, NamedParticleEffect>? =
        transaction(database) { PlayersTable.getCurrentParticle(playerUniqueId) }

    fun setCurrentParticle(playerUniqueId: UUID, particleId: Int?): Boolean =
        transaction(database) { PlayersTable.setCurrentParticle(playerUniqueId, particleId) }


    private lateinit var scheduler: ScheduledExecutorService
    private lateinit var database: Database
    override fun onEnable() {
        val (db, commands) = json.readFileAs(configFile("fancyparticles.json"), Config())
        scheduler = Executors.newScheduledThreadPool(4)
        require(db.url.isNotEmpty()) { "Database url is empty" }
        database = Database.connect(db.url, user = db.username, password = db.password)
        transaction(database) { createMissingTablesAndColumns(PlayersTable, ParticlesTable, PlayerParticlesTable) }

    }

    fun scheduleParticle(particleEffect: ParticleEffect, player: Player) {
        clearTasks(player)
        playerTasks[player.uniqueId] = ArrayList()
        for (part in particleEffect.particleParts) {
            val task = ParticleTask(part, player)
            val t = scheduler.scheduleAtFixedRate(task, part.delay * 50, part.period * 50, TimeUnit.MILLISECONDS)
            playerTasks[player.uniqueId]?.add(t)
        }
    }

    fun clearTasks(player: Player) {
        if (playerTasks.containsKey(player.uniqueId))
            for (t in playerTasks[player.uniqueId]!!) t.cancel(false)
        playerTasks.remove(player.uniqueId)
    }


    @Serializable
    data class Config(
        val database: Database = Database(),
        val commands: ParticleCommands.Config = ParticleCommands.Config()
    ) {
        @Serializable
        data class Database(
            //Default db
            val url: String = "jdbc:sqlite:fancyparticles.db",
            val username: String = "",
            val password: String = ""
        )
    }
}

