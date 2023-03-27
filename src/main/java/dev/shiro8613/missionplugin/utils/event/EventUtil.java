package dev.shiro8613.missionplugin.utils.event;

import dev.shiro8613.missionplugin.event.EventEnum;
import org.bukkit.event.Event;
public class EventUtil {
    public static <T> T convertEvent(EventEnum eventEnum, Event event) {
        Class<? extends T> x = (Class<? extends T>) eventEnum.getEventsClass();
        return x.cast(event);
    }
}
