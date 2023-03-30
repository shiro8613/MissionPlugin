package dev.shiro8613.missionplugin.event;

import org.bukkit.event.Event;

public class EventContext {
    private Event eventData;

    public static EventContext create(Event event) {
        EventContext eventContext = new EventContext();
        eventContext.eventData = event;
        return eventContext;
    }

    /**
     * 流れてきたイベントデータを取得したい種類の型に変換して取得できます。
     * @param eventEnum 変換したイベントの種類
     * @return <T> 変換されたイベントの型
     */
    public <T> T getEvent(EventEnum eventEnum) {
        Class<? extends T> x = (Class<? extends T>) eventEnum.getEventsClass();
        return x.cast(eventData);
    }
}
