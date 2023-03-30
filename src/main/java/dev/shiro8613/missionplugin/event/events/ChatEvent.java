package dev.shiro8613.missionplugin.event.events;

import dev.shiro8613.missionplugin.event.Event;
import dev.shiro8613.missionplugin.event.EventEnum;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatEvent extends Event implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        onEvent(EventEnum.ChatEvent, event);
    }

}
