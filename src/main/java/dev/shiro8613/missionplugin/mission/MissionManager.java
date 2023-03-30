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

    /**
     * ミッションを実装したクラスを１つ追加できます。
     * @param  missionClass ミッションを実装したクラス.class
     */
    @SuppressWarnings("unchecked")
    public void register(Class<? extends Mission> missionClass) {
        this.registers(missionClass);
    }

    /**
     * ミッションを実装したクラスを複数追加できます。
     * @param missionClasses ミッションを実装したクラス名1.class , ミッションを実装したクラス2.class ...
     */
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

    /**
     * 登録されたすべてのミッション名を取得できます。
     * @return string[] すべてのミッション名
     */
    public String[] getMissionNames() {
        return missionMap.keySet().toArray(new String[0]);
    }

    /**
     *
     * @param name　ミッション名 - getMissionNamesを利用して確認。
     * @return boolean 実行が成功したかどうか。
     */
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

    /**
     * 実行中のミッションを強制的に停止します。
     * @return boolean 停止に成功したかどうか。
     */
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

    /**
     * ミッションが実行中かどうかを取得できます。
     * @return boolean 実行中かどうか。
     */
    public boolean isMissionState() {
        return missionState;
    }

    public BukkitTask getProgressMission() {
        return progressingMission.task;
    }
}

