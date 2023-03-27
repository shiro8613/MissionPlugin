package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.mission.Mission;
import org.bukkit.Bukkit;

public class Mission1 extends Mission {

    @Override
    public void Init() {}

    @Override
    public void Tick() {
        getJavaPlugin().getServer().broadcast("aaa", "");
    }

}
