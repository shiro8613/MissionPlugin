package dev.shiro8613.missionplugin.command;

import org.bukkit.command.CommandSender;

public class CommandContext {
    private CommandSender commandSender;
    private String[] args;

    public String[] getArgs() {
        return args;
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }

    public static CommandContext create(String[] args, CommandSender sender) {
        CommandContext context = new CommandContext();
        context.args = args;
        context.commandSender = sender;
        return context;
    }
}
