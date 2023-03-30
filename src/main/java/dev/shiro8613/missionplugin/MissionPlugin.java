package dev.shiro8613.missionplugin;

import dev.shiro8613.missionplugin.mission.MissionManager;
import dev.shiro8613.missionplugin.mission.missions.*;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public final class MissionPlugin extends JavaPlugin {

    private static MissionPlugin instance;
    private static MissionManager missionManager;

    private String SaidByDangomushi = "(|||| ); < ";

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
        missionManager.registers(Mission1.class, Mission2.class, EndRoll.class);

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

        if(args.length < 1 ) return false;

        sender.sendMessage(SaidByDangomushi + "実行したコマンドは: " + args[0]);

        switch (args[0]) {
            case "start": {
                String className = "";

                try {
                    className = missionManager.getMissionNames()[Integer.parseInt(args[1])-1];
                } catch (Exception ex) {
                    sender.sendMessage(SaidByDangomushi + "ミッションの指定が間違っています！");
                    break;
                }

                sender.sendMessage(SaidByDangomushi + className + "を実行〜〜〜〜！！！！！ ");
                // ミッションを実行!
                missionManager.startMission(className);
                break;
            }

            case "list": {
                sender.sendMessage(SaidByDangomushi + "実行できるミッション一覧〜〜〜〜！！！！！ ");
                // ミッション一覧
                String[] missionNames = missionManager.getMissionNames();
                sender.sendMessage(ChatColor.AQUA + "------------List------------");
                for (int i=0; i < missionNames.length; i++) {
                    sender.sendMessage(ChatColor.AQUA + "[" + (i+1) + "] " + ChatColor.YELLOW + missionNames[i]);
                }
                sender.sendMessage(ChatColor.AQUA + "---------------------------" + ChatColor.WHITE);
                break;
            }

            case "forcestop": {
                // ミッション強制停止
                if (missionManager.forceMissionStop())
                    sender.sendMessage(SaidByDangomushi+"ミッションを停止しました");
                break;
            }

            case "state": {
                // ミッション実行中かどうか
                if (missionManager.isMissionState()) {
                    sender.sendMessage((SaidByDangomushi + "ミッションを実行中です!"));
                }
                break;
            }
        }

        return true;
    }
}
