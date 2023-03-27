package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.event.EventEnum;
import dev.shiro8613.missionplugin.mission.Mission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Mission1 extends Mission {

    @Override
    public void Init() {
        getJavaPlugin().getServer().broadcast("Hello!", "");
    }

    @Override
    public void Tick() {
        getJavaPlugin().getLogger().info("aaaa");
        missionEnd();
    }

}
