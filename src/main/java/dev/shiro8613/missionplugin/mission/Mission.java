package dev.shiro8613.missionplugin.mission;

import dev.shiro8613.missionplugin.event.EventManager;
import dev.shiro8613.missionplugin.utils.timer.TimerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Mission {

    private JavaPlugin javaPlugin = null;
    private TimerManager timerManager = null;
    private EventManager eventManager = null;

    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    final void init(JavaPlugin pl, TimerManager timerManager, EventManager eventManager) {
        this.javaPlugin = pl;
        this.timerManager = timerManager;
        this.eventManager = eventManager;
    }

    public abstract void Init();

    public abstract void Tick();

}
