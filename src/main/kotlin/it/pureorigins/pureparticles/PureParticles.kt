package it.pureorigins.pureparticles

import it.pureorigins.common.*
import it.pureorigins.pureparticles.particles.NamedParticleEffect
import it.pureorigins.pureparticles.particles.ParticleEffect
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

internal lateinit var plugin: PureParticles private set

class PureParticles : JavaPlugin() {
    private lateinit var database: Database

    private val playerTasks = HashMap<Player, ParticleTask?>()
    
    fun getParticle(id: Int): NamedParticleEffect? = transaction(database) { ParticlesTable.getById(id) }
    
    fun getParticle(name: String): Pair<Int, NamedParticleEffect>? =
        transaction(database) { ParticlesTable.getByName(name) }

    fun getAllNames(): Set<String> = transaction(database) { ParticlesTable.getAllNames() }
    
    fun addParticle(playerUniqueId: UUID, particleId: Int): Boolean =
        transaction(database) { PlayerParticlesTable.add(playerUniqueId, particleId) }

    fun removeParticle(playerUniqueId: UUID, particleId: Int): Boolean =
        transaction(database) { PlayerParticlesTable.remove(playerUniqueId, particleId) }

    fun getPlayerParticles(playerUniqueId: UUID): Map<Int, NamedParticleEffect> =
        transaction(database) { PlayerParticlesTable.getParticles(playerUniqueId) }

    fun getPlayersCount(): Long = transaction(database) { PlayersTable.count() }
    
    fun getCurrentParticle(playerUniqueId: UUID): Pair<Int, NamedParticleEffect>? =
        transaction(database) { PlayersTable.getCurrentParticle(playerUniqueId) }

    fun setCurrentParticle(playerUniqueId: UUID, particleId: Int?): Boolean =
        transaction(database) { PlayersTable.setCurrentParticle(playerUniqueId, particleId) }

    fun scheduleParticle(player: Player, effect: ParticleEffect?) {
        if (playerTasks[player]?.effect == effect) return
        
        playerTasks[player]?.cancel()
        if (effect == null) {
            playerTasks[player] = null
        } else {
            playerTasks[player] = ParticleTask(effect, player).also { it.run() }
        }
    }
    
    fun clearTasks(player: Player) {
        playerTasks[player]?.cancel()
        playerTasks -= player
    }
    
    override fun onLoad() {
        plugin = this
    }
    
    override fun onEnable() {
        val (db, commands) = json.readFileAs(file("config.json"), Config())
        require(db.url.isNotEmpty()) { "Database url is empty" }
        database = Database.connect(db.url, user = db.username, password = db.password)
        transaction(database) { createMissingTablesAndColumns(PlayersTable, ParticlesTable, PlayerParticlesTable) }
        registerEvents(Events)
        registerCommand(ParticleCommands(commands).command)
        Bukkit.getOnlinePlayers().forEach { scheduleParticle(it, getCurrentParticle(it.uniqueId)?.second?.particleEffect) }
    }

    @Serializable
    data class Config(
        val database: Database = Database(),
        val commands: ParticleCommands.Config = ParticleCommands.Config()
    ) {
        @Serializable
        data class Database(
            val url: String = "",
            val username: String = "",
            val password: String = ""
        )
    }
}

