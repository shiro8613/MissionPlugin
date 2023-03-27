package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.event.EventEnum;
import dev.shiro8613.missionplugin.mission.Mission;
import dev.shiro8613.missionplugin.utils.event.EventUtil;
import org.bukkit.event.player.PlayerInteractEvent;


public class Mission1 extends Mission {

    @Override
    public void Init() {
        getEventManager().registerEventHandler(EventEnum.ClickEvent, (event) -> {
            PlayerInteractEvent ev = EventUtil.convertEvent(EventEnum.ClickEvent, event ); //これでeventが別な型にキャストされるよ！

        });
    }

    @Override
    public void Tick() {

    }

}
