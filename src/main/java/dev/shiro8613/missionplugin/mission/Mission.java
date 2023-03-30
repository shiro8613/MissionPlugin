package dev.shiro8613.missionplugin.mission;

import dev.shiro8613.missionplugin.command.MissionCommandManager;
import dev.shiro8613.missionplugin.event.EventManager;
import dev.shiro8613.missionplugin.utils.timer.TimerManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public abstract class Mission {

    private JavaPlugin javaPlugin = null;
    private TimerManager timerManager = null;
    private EventManager eventManager = null;
    private MissionManager missionManager = null;
    private MissionCommandManager commandManager = null;

    /**
     * プラグイン本体のインスタンスを取得できます。
     * @return JavaPlugin
     */
    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }

    /**
     * ボスバーなどの機能が実装されたタイマーを管理するマネージャーを取得できます。
     * @return TimerManager
     */
    public TimerManager getTimerManager() {
        return timerManager;
    }

    /**
     * ミッション毎に使用可能なコマンドを管理するマネージャーを取得できます。
     * @return MissionCommandManager
     */
    public MissionCommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * イベント処理を管理するマネージャーを取得できます。
     * @return EventManager
     */
    public EventManager getEventManager() {
        return eventManager;
    }


    /**
     * サーバー内にいるすべてのプレーヤーを取得することができます。
     * @return List
     */

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        List<World> worlds = getJavaPlugin().getServer().getWorlds();
        worlds.forEach(world -> players.addAll(world.getPlayers()));

        return players;
    }

    /**
     * ミッションが終了したことを伝え、ミッションのスレッドを破棄します。
     * <h1>このメソッドを呼び出すまで、ミッションが終了したことにならず次のミッションが実行できないので、</h1>
     * <h1>必ずがミッションの終了する時点で呼び出してください。</h1>
     */
    public void missionEnd() {
        missionManager.forceMissionStop();
    }


    final void init(JavaPlugin pl, MissionManager missionManager , TimerManager timerManager, EventManager eventManager, MissionCommandManager commandManager) {
        this.javaPlugin = pl;
        this.missionManager = missionManager;
        this.timerManager = timerManager;
        this.eventManager = eventManager;
        this.commandManager = commandManager;
    }

    /**
     * ミッションが開始する際に１度だけ呼ばれる関数です。
     */
    public abstract void Init();

    /**
     * ミッションが終了（missionEnd()が呼ばれるまで）毎ティック呼び出される関数です。
     */
    public abstract void Tick();

    /**
     * ミッションが終了する際に１度だけ呼ばれる関数です。
     * ステータスやリストがある場合はここで廃棄しないとメモリリークが起こる可能性があります。
     */
    public abstract void onDisable();

}
