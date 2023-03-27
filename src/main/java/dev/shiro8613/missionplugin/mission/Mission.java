package dev.shiro8613.missionplugin.mission;

import dev.shiro8613.missionplugin.utils.timer.TimerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Mission {

    private JavaPlugin javaPlugin = null;
    private TimerManager timerManager = null;


    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    final void init(JavaPlugin pl, TimerManager timerManager) {
        this.javaPlugin = pl;
        this.timerManager = timerManager;
    }

    public abstract void Init();

    public abstract void Tick();

}
