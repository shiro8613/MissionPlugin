package dev.shiro8613.missionplugin.command;

import dev.shiro8613.missionplugin.mission.MissionManager;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommandManager {

    private final Map<String, CommandHandler> tmpMissionCommandMap = new HashMap<>();
    private Map<String, CommandHandler> firstCommandList;

    private MissionManager missionManager;

    public CommandManager(JavaPlugin plugin, MissionManager missionManager) {

        this.missionManager = missionManager;

        FirstCommand firstCommand = new FirstCommand(missionManager);
        this.firstCommandList = firstCommand.build();
        this.firstCommandList.put("ctl", context -> {
            String[] args = context.getArgs();
            CommandSender commandSender = context.getCommandSender();
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            CommandHandler commandHandler = tmpMissionCommandMap.get(args[0]);
            if(commandHandler != null) {
                commandHandler.execute(CommandContext.create(newArgs, commandSender));
            } else commandSender.sendMessage("コマンドが無いよ....");
        });

        Objects.requireNonNull(plugin.getCommand("mission")).setExecutor(this::onCommand);
        Objects.requireNonNull(plugin.getCommand("mission")).setTabCompleter(this::onTabComplete);
    }

    public void MissionCommandAdd(String cmd, CommandHandler handler) {
        tmpMissionCommandMap.put(cmd, handler);
    }

    public void MissionCommandRemoveAll() {
        tmpMissionCommandMap.clear();
    }


    private boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        CommandHandler commandHandler = firstCommandList.get(args[0]);
        if(commandHandler == null) {
            sender.sendMessage("コマンドがありません");
            return false;
        }

        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        commandHandler.execute(CommandContext.create(newArgs, sender));

        return true;
    }

    private @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("mission")) {
            final List<String> argResponse = new ArrayList<>();
            final List<String> availableSubCmd = firstCommandList.keySet().stream().toList();
            final List<String> missionCmd = tmpMissionCommandMap.keySet().stream().toList();

            switch (args.length) {
                case 0 -> {
                    return availableSubCmd;

                }

                case 1 -> {
                    StringUtil.copyPartialMatches(args[0], availableSubCmd, argResponse);
                    return argResponse;

                }

                case 2 -> {
                    if(args[0].equalsIgnoreCase("start")) {
                        for (int i = 1; i <= missionManager.getMissionNames().length; i++) {
                            argResponse.add(Integer.toString(i));
                        }
                        return argResponse;

                    } else if (args[0].equalsIgnoreCase("ctl")) {
                        if(missionCmd.size() == 0) argResponse.add("NO-MISSION-COMMAND");
                        StringUtil.copyPartialMatches(args[1], missionCmd, argResponse);
                        return argResponse;

                    }
                }
            }
        }
        return null;
    }
}
