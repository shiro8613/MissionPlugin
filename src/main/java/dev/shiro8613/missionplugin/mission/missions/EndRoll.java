package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.event.EventEnum;
import dev.shiro8613.missionplugin.mission.Mission;
import dev.shiro8613.missionplugin.utils.event.EventUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.TitlePart;

public class EndRoll extends Mission {

    @Override
    public void Init() {
        getEventManager().registerEventHandler(EventEnum.ChatEvent, event -> {
            AsyncChatEvent chatEvent = EventUtil.convertEvent(EventEnum.ChatEvent, event);
            chatEvent.setCancelled(true);
            Component text = Component.text("エンドロール中は発言ができません。", NamedTextColor.RED);
            chatEvent.getPlayer().sendActionBar(text);
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
