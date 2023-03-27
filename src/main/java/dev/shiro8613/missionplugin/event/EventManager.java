package dev.shiro8613.missionplugin.event;

import dev.shiro8613.missionplugin.event.events.ClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class EventManager {
    private static final Map<EventEnum, EventHandler> EventHandlerMap = new HashMap<>();

    public static Map<EventEnum, EventHandler> getEventMap() {
        return EventHandlerMap;
    }

    public EventManager(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new ClickEvent(), plugin);
    }

    public void registerEventHandler(EventEnum eventEnum, EventHandler eventHandler) {
        EventHandlerMap.put(eventEnum, eventHandler);
    }

    public void removeAll() {
        EventHandlerMap.clear();
    }


}
