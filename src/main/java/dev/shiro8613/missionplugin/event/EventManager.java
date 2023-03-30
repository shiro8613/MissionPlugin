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

    public void registerEventHandler(EventEnum eventEnum, EventHandler eventHandler) {
        EventHandlerMap.put(eventEnum, eventHandler);
    }

    public void removeAll() {
        EventHandlerMap.clear();
    }


}
