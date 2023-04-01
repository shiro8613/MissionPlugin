package dev.shiro8613.missionplugin.event;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public enum EventEnum {
    ClickEvent(PlayerInteractEvent.class),
    ChatEvent(AsyncChatEvent.class),
    InventoryCloseEvent(InventoryCloseEvent.class),
    InventoryClickEvent(InventoryClickEvent.class);
    private Class<?> eventsClass;

    EventEnum(Class<?> eventsClass) {
        this.eventsClass = eventsClass;
    }

    public Class<?> getEventsClass() {
        return eventsClass;
    }

}
