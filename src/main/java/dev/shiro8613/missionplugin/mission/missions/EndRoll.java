package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.event.EventEnum;
import dev.shiro8613.missionplugin.mission.Mission;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.player.PlayerInteractEvent;

public class EndRoll extends Mission {

    @Override
    public void Init() {
        getEventManager().registerEventHandler(EventEnum.ChatEvent, eventContext -> {
            AsyncChatEvent chatEvent = eventContext.getEvent(EventEnum.ChatEvent);
            chatEvent.setCancelled(true);
            Component text = Component.text("エンドロール中は発言ができません。", NamedTextColor.RED);
            chatEvent.getPlayer().sendActionBar(text);
        });

        getCommandManager().addCmd("stop", context -> {
            context.getCommandSender().sendMessage("aaa");
        });
    }

    @Override
    public void Tick() {
        //あとで実装
    }

    @Override
    public void onDisable() {

    }
}
