package dev.shiro8613.missionplugin.mission;

import dev.shiro8613.missionplugin.event.EventManager;
import dev.shiro8613.missionplugin.utils.timer.TimerManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public abstract class Mission {

    private JavaPlugin javaPlugin = null;
    private TimerManager timerManager = null;
    private EventManager eventManager = null;
    private MissionManager missionManager = null;

    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        List<World> worlds = getJavaPlugin().getServer().getWorlds();
        worlds.forEach(world -> {
            players.addAll(world.getPlayers());
        });

        return players;
    }

    public void missionEnd() {
        missionManager.getProgressMission().cancel();
        eventManager.removeAll();
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    final void init(JavaPlugin pl,MissionManager missionManager , TimerManager timerManager, EventManager eventManager) {
        this.javaPlugin = pl;
        this.missionManager = missionManager;
        this.timerManager = timerManager;
        this.eventManager = eventManager;
    }

    public abstract void Init();

    public abstract void Tick();

}
