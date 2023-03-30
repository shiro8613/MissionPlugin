package dev.shiro8613.missionplugin;

import dev.shiro8613.missionplugin.mission.MissionManager;
import dev.shiro8613.missionplugin.mission.missions.Mission1;
import dev.shiro8613.missionplugin.mission.missions.Mission2;
import dev.shiro8613.missionplugin.mission.missions.Mission3;
import dev.shiro8613.missionplugin.mission.missions.Mission4;
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
        missionManager.registers(Mission1.class, Mission2.class);

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
            case "start" -> {
                String className = "";

                try {
                    className = missionManager.getMissionNames()[Integer.parseInt(args[1]) - 1];
                } catch (Exception ex) {
                    sender.sendMessage(SaidByDangomushi + "ミッションの指定が間違っています！");
                    break;
                }

                sender.sendMessage(SaidByDangomushi + className + "を実行〜〜〜〜！！！！！ ");
                // ミッションを実行!
                missionManager.startMission(className);
            }
            case "list" -> {
                sender.sendMessage(SaidByDangomushi + "実行できるミッション一覧〜〜〜〜！！！！！ ");
                // ミッション一覧
                String[] missionNames = missionManager.getMissionNames();
                sender.sendMessage(ChatColor.AQUA + "------------List------------");
                for (int i = 1; i <= missionNames.length; i++) {
                    sender.sendMessage(ChatColor.AQUA + "[" + i + "] " + ChatColor.YELLOW + missionNames[i - 1]);
                }
                sender.sendMessage(ChatColor.AQUA + "---------------------------" + ChatColor.WHITE);
            }
            case "forcestop" -> {
                // ミッション強制停止
                if (missionManager.forceMissionStop())
                    sender.sendMessage(SaidByDangomushi + "ミッションを停止しました");
            }
            case "state" -> {
                // ミッション実行中かどうか
                if (missionManager.isMissionState()) {
                    sender.sendMessage((SaidByDangomushi + "ミッションを実行中です!"));
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (cmd.getName().equalsIgnoreCase("mission")) {
            final List<String> argResponse = new ArrayList<>();
            final List<String> availableSubCmd = new ArrayList<>();
            availableSubCmd.add("start");
            availableSubCmd.add("list");
            availableSubCmd.add("forcestop");
            availableSubCmd.add("state");

            if (args.length == 0) {
                return availableSubCmd;
            }
            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], availableSubCmd, argResponse);
                return argResponse;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
                for (int i = 1; i <= missionManager.getMissionNames().length; i++) {
                    argResponse.add(Integer.toString(i));
                }
                return argResponse;
            }
            return argResponse;
        }
        return null;
    }
}
