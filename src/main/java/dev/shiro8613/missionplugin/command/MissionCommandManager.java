package dev.shiro8613.missionplugin.command;

public class MissionCommandManager {
    private CommandManager commandManager;

    public MissionCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public void addCmd(String cmd, CommandHandler handler) {
        commandManager.MissionCommandAdd(cmd, handler);
    }

    public void removeAll() {
        commandManager.MissionCommandRemoveAll();
    }
}
