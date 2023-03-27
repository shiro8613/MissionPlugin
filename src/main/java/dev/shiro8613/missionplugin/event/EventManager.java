package dev.shiro8613.missionplugin.event;

import dev.shiro8613.missionplugin.event.events.ClickEvent;

import java.util.HashMap;
import java.util.Map;

public class EventManager {
    private static final Map<EventEnum, EventHandler> EventHandlerMap = new HashMap<>();

    public static Map<EventEnum, EventHandler> getEventMap() {
        return EventHandlerMap;
    }

    public EventManager() {
        new ClickEvent();
    }

    public void registerEvent(EventEnum eventEnum, EventHandler eventHandler) {
        EventHandlerMap.put(eventEnum, eventHandler);
    }


}
