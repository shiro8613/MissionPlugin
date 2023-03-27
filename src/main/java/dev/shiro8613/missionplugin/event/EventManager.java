package dev.shiro8613.missionplugin.event;

import dev.shiro8613.missionplugin.event.events.ClickEvent;

import java.util.HashMap;
import java.util.Map;

public class EventManager {
    private static final EventManager instance = new EventManager();

    public static EventManager getInstance() {
        return instance;
    }

    private final Map<EventEnum,EventInterface> EventMap = new HashMap<>();

    private final ClickEvent clickEvent = new ClickEvent();


    public EventManager() {
        EventMap.put(EventEnum.ClickEvent, clickEvent);
    }




}
