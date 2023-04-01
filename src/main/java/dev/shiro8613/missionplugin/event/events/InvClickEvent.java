package dev.shiro8613.missionplugin.event.events;

import dev.shiro8613.missionplugin.event.Event;
import dev.shiro8613.missionplugin.event.EventEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InvClickEvent extends Event implements Listener {
    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        onEvent(EventEnum.InventoryClickEvent, event);
    }
}
