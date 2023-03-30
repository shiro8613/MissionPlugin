package dev.shiro8613.missionplugin.command;

public class MissionCommandManager {
    private CommandManager commandManager;

    public MissionCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * ミッション毎に実装可能なコマンドを登録します。
     * 登録されたコマンドは`/mission ctl `で呼び出し可能です。
     * タブ補完も自動で実装されます。
     * @param cmd コマンドの名前
     * @param handler コマンドが実行された際に呼ばれる関数
     */
    public void addCmd(String cmd, CommandHandler handler) {
        commandManager.MissionCommandAdd(cmd, handler);
    }

    /**
     * ミッション毎に登録されているコマンドをすべて削除します。
     * ミッション終了時に呼び出さないと
     * 他のミッションの実行時に呼び出されてしまうため
     * 必ず、onDisable()で呼び出してください。
     */
    public void removeAll() {
        commandManager.MissionCommandRemoveAll();
    }
}
