package it.pureorigins.fancyparticles;

import it.pureorigins.fancyparticles.FancyParticles.Companion.clearTasks
import it.pureorigins.fancyparticles.FancyParticles.Companion.getCurrentParticle
import it.pureorigins.fancyparticles.FancyParticles.Companion.scheduleParticle
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent;

class Events {
    @EventHandler
    fun onLeave(event: PlayerJoinEvent) {
        clearTasks(event.player)
    }

    @EventHandler
    fun onJoin(event: PlayerQuitEvent) {
        getCurrentParticle(event.player.uniqueId)?.second?.let {
            scheduleParticle(it.particleEffect, event.player)
        }
    }
}
