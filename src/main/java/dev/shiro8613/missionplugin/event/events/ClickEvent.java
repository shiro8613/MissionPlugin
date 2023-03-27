package dev.shiro8613.missionplugin.event.events;

import dev.shiro8613.missionplugin.event.Event;
import dev.shiro8613.missionplugin.event.EventEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ClickEvent extends Event implements Listener {
    @EventHandler
    public void onClick(PlayerInteractEvent event){ //ボタンをクリックした時の処理
        //ClickAction
        onEvent(EventEnum.ClickEvent, event);
    }
}
