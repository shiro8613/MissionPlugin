package dev.shiro8613.missionplugin.mission;

import org.bukkit.scheduler.BukkitTask;

public class ProgressingMission {
    String missionName;
    BukkitTask task;

    public static ProgressingMission create(String missionName, BukkitTask task) {
        ProgressingMission progressingMission = new ProgressingMission();
        progressingMission.task = task;
        progressingMission.missionName = missionName;
        return progressingMission;
    }
}
