package dev.shiro8613.missionplugin.command;

import dev.shiro8613.missionplugin.mission.Mission;
import dev.shiro8613.missionplugin.mission.MissionManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class FirstCommand {

    private MissionManager missionManager;

    public FirstCommand(MissionManager missionManager) {
        this.missionManager = missionManager;
    }

    public Map<String, CommandHandler> build() {
        Map<String, CommandHandler> map = new HashMap<>();
        map.put("start", startCommand());
        map.put("list", listCommand());
        map.put("forcestop", forceStopCommand());
        map.put("state", stateCommand());

        return map;
    }

    private CommandHandler startCommand() {
        return context -> {
            String[] args = context.getArgs();
            CommandSender commandSender = context.getCommandSender();

            if(args.length < 1) commandSender.sendMessage("コマンドミスってるよ");
            int index = Integer.parseInt(args[0]);
            try {
                String missionName = missionManager.getMissionNames()[index];
                missionManager.startMission(missionName);
            } catch (Exception e) {
                commandSender.sendMessage("その番号のミッション無いって");
            }
        };
    }

    private CommandHandler listCommand() {
        return context -> {
            CommandSender commandSender = context.getCommandSender();
            String[] missionNames = missionManager.getMissionNames();
            commandSender.sendMessage(ChatColor.AQUA + "------------List------------");
            for (int i = 0; i < missionNames.length; i++) {
                commandSender.sendMessage(ChatColor.AQUA + "[" + i + "] " + ChatColor.YELLOW + missionNames[i]);
            }
            commandSender.sendMessage(ChatColor.AQUA + "---------------------------" + ChatColor.WHITE);
        };
    }

    private CommandHandler forceStopCommand() {
        return context -> {
            CommandSender commandSender = context.getCommandSender();
            if(missionManager.forceMissionStop()) {
                commandSender.sendMessage("ミッションを停止したよ");
            } else commandSender.sendMessage("実行中のミッションが無いぜ");
        };
    }

    private CommandHandler stateCommand() {
        return context -> {
            CommandSender commandSender = context.getCommandSender();
            if(missionManager.isMissionState()) {
                commandSender.sendMessage("ミッションが進行中です");
            } else commandSender.sendMessage("進行中のミッションはありません");
        };
    }

}
