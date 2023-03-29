package dev.shiro8613.missionplugin.mission;

import org.bukkit.scheduler.BukkitTask;

public class ProgressMission {
    String missionName;
    BukkitTask task;

    public static ProgressMission create(String missionName, BukkitTask task) {
        ProgressMission progressMission = new ProgressMission();
        progressMission.task = task;
        progressMission.missionName = missionName;
        return progressMission;
    }
}
