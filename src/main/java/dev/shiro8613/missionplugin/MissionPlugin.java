package dev.shiro8613.missionplugin;

import dev.shiro8613.missionplugin.mission.MissionManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MissionPlugin extends JavaPlugin {

    private static MissionPlugin instance;
    private static MissionManager missionManager;

    public static MissionPlugin getInstance() {
        return instance;
    }

    public static MissionManager getMissionManager() {
        return missionManager;
    }

    @Override
    public void onEnable() {
        instance = this;
        missionManager = new MissionManager(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
