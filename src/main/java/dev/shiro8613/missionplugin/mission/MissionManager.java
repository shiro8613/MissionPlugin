package dev.shiro8613.missionplugin.mission;

import dev.shiro8613.missionplugin.event.EventManager;
import dev.shiro8613.missionplugin.utils.timer.TimerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class MissionManager {

    private Map<String, Mission> missionMap;
    private JavaPlugin plugin;
    private final TimerManager timerManager = new TimerManager();
    private final EventManager eventManager = new EventManager();
    private boolean missionState = false;
    private BukkitRunnable progressMission = null;

    public MissionManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.missionMap = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public void register(Class<? extends Mission> missionClass)
            throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {

        this.registers(missionClass);
    }

    @SuppressWarnings("unchecked")
    public void registers(Class<? extends Mission>... missionClasses)
            throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {

        for(Class<? extends Mission> missionClass : missionClasses) {
            Mission mission = missionClass.getDeclaredConstructor().newInstance();
            String missionName = mission.getClass().getEnclosingClass().getName();
            mission.init(plugin, timerManager);
            mission.Init();
            missionMap.put(missionName, mission);

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
        progressMission = new BukkitRunnable() {
            @Override
            public void run() {
                mission.Tick();
            }
        };
        progressMission.runTaskTimer(plugin,1,0);
        return true;
    }

    public boolean forceMissionStop() {
        if(missionState) return false;
        if(progressMission == null) return false;
        progressMission.cancel();
        missionState = false;
        progressMission = null;
        return true;
    }

    public boolean isMissionState() {
        return missionState;
    }

}

