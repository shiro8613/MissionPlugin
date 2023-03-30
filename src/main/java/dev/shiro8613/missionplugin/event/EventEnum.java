package dev.shiro8613.missionplugin.event;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public enum EventEnum {
    ClickEvent(PlayerInteractEvent.class),
    ChatEvent(AsyncChatEvent.class);
    private Class<?> eventsClass;

    EventEnum(Class<?> eventsClass) {
        this.eventsClass = eventsClass;
    }

    public Class<?> getEventsClass() {
        return eventsClass;
    }

}
