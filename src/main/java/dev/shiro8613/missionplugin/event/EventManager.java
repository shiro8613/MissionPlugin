package dev.shiro8613.missionplugin.event;

import dev.shiro8613.missionplugin.event.events.ChatEvent;
import dev.shiro8613.missionplugin.event.events.ClickEvent;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private static final Map<EventEnum, EventHandler> EventHandlerMap = new HashMap<>();

    public static Map<EventEnum, EventHandler> getEventMap() {
        return EventHandlerMap;
    }

    public EventManager(JavaPlugin plugin) {
        registerAssist(plugin, new ClickEvent(), new ChatEvent());
    }

    private void registerAssist(JavaPlugin plugin, Listener... listeners) {
        for(Listener listener :listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    /**
     * イベントが発火した際に実行される関数を登録できます。
     * @param eventEnum イベントの種類
     * @param eventHandler 発火した際に呼ばれる関数
     */

    public void registerEventHandler(EventEnum eventEnum, EventHandler eventHandler) {
        EventHandlerMap.put(eventEnum, eventHandler);
    }

    /**
     * ミッション毎に登録されているイベントをすべて削除します。
     * ミッション終了時に呼び出さないと
     * 他のミッションの実行時に呼び出されてしまうため
     * 必ず、onDisable()で呼び出してください。
     */

    public void removeAll() {
        EventHandlerMap.clear();
    }


}
