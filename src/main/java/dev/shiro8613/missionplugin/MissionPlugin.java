package dev.shiro8613.missionplugin;

import dev.shiro8613.missionplugin.command.CommandManager;
import dev.shiro8613.missionplugin.mission.MissionManager;
import dev.shiro8613.missionplugin.mission.missions.*;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

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
        missionManager.registers(Mission1.class, Mission2.class, Mission3.class, EndRoll.class);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
