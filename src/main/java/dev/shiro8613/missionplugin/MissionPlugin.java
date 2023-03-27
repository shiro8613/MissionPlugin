package dev.shiro8613.missionplugin;

import dev.shiro8613.missionplugin.mission.MissionManager;
import dev.shiro8613.missionplugin.mission.missions.Mission1;
import dev.shiro8613.missionplugin.mission.missions.Mission2;
import dev.shiro8613.missionplugin.mission.missions.Mission3;
import dev.shiro8613.missionplugin.mission.missions.Mission4;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

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
        missionManager.registers(Mission1.class);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        /*
            /mission start (Number) //ミッション開始
            /mission list //ミッション一覧表示
            /mission forcestop //ミッション強制停止
            /mission state //ミッション実行中かどうか
        */

        sender.sendMessage("実行するコマンドは〜〜〜〜？！？！？！？！ " + args[0]);
        String className = missionManager.getMissionNames()[Integer.parseInt(args[0])-1];
        sender.sendMessage("実行できるクラス名は〜〜〜〜？！？！？！？！ " + className);

        switch (args[1]) {
            case "start": {
                // ミッションを実行!
                missionManager.startMission(className);
                break;
            }

            case "list": {
                // ミッション一覧
                break;
            }

            case "forcestop": {
                // ミッション強制停止
                break;
            }

            case "state": {
                // ミッション実行中かどうか
                break;
            }
        }

        return true;
    }
}
