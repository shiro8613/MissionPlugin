package dev.shiro8613.missionplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class MissionPlugin extends JavaPlugin {

    private static MissionPlugin instance;

    public static MissionPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
