package dev.shiro8613.missionplugin.event;

import org.bukkit.event.player.PlayerInteractEvent;

public enum EventEnum {
    ClickEvent(PlayerInteractEvent.class);

    private Class<?> eventsClass;

    private EventEnum(Class<?> eventsClass) {
        this.eventsClass = eventsClass;
    }

    public Class<?> getEventsClass() {
        return eventsClass;
    }

}
