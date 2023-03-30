package dev.shiro8613.missionplugin.event;

import org.bukkit.event.Event;

public class EventContext {
    private Event eventData;

    public static EventContext create(Event event) {
        EventContext eventContext = new EventContext();
        eventContext.eventData = event;
        return eventContext;
    }

    public <T> T getEvent(EventEnum eventEnum) {
        Class<? extends T> x = (Class<? extends T>) eventEnum.getEventsClass();
        return x.cast(eventData);
    }
}
