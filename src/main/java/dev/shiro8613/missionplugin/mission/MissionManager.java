package dev.shiro8613.missionplugin.mission;

import dev.shiro8613.missionplugin.command.CommandManager;
import dev.shiro8613.missionplugin.command.MissionCommandManager;
import dev.shiro8613.missionplugin.event.EventManager;
import dev.shiro8613.missionplugin.utils.timer.TimerManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class MissionManager {

    private Map<String, Mission> missionMap;
    private JavaPlugin plugin;
    private final TimerManager timerManager = new TimerManager();
    private MissionCommandManager commandManager;
    private EventManager eventManager;
    private boolean missionState = false;
    private ProgressingMission progressingMission = null;

    public MissionManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.eventManager = new EventManager(plugin);
        this.missionMap = new HashMap<>();
        this.commandManager = new MissionCommandManager(new CommandManager(plugin, this));
    }

    @SuppressWarnings("unchecked")
    public void register(Class<? extends Mission> missionClass) {
        this.registers(missionClass);
    }

    @SuppressWarnings("unchecked")
    public void registers(Class<? extends Mission>... missionClasses) {

        for(Class<? extends Mission> missionClass : missionClasses) {
            try {
                Mission mission = missionClass.getDeclaredConstructor().newInstance();
                String missionName = missionClass.getSimpleName();
                mission.init(plugin, this, timerManager, eventManager, commandManager);
                missionMap.put(missionName, mission);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public String[] getMissionNames() {
        return missionMap.keySet().toArray(new String[0]);
    }

    public boolean startMission(String name) {
        Mission mission = missionMap.get(name);
        if(missionState) return false;
        if(mission == null) return false;

        missionState = true;
        mission.Init();
        BukkitRunnable tmpTask = new BukkitRunnable() {
            @Override
            public void run() {
                mission.Tick();
            }
        };
        progressingMission = ProgressingMission.create(name, tmpTask.runTaskTimer(plugin,1,0));
        return true;
    }

    public boolean forceMissionStop() {
        if(!missionState) return false;
        if(progressingMission == null) return false;
        missionMap.get(progressingMission.missionName).onDisable();
        progressingMission.task.cancel();
        eventManager.removeAll();
        commandManager.removeAll();
        missionState = false;
        progressingMission = null;
        return true;
    }

    public boolean isMissionState() {
        return missionState;
    }

    public BukkitTask getProgressMission() {
        return progressingMission.task;
    }
}

