package dev.shiro8613.missionplugin.event;

public class Event {

    /**
     * イベントが発火した事をマネージャーに伝えます。
     * @param eventEnum イベントの種類
     * @param event Bukkitのイベント
     */
    public void onEvent(EventEnum eventEnum, org.bukkit.event.Event event) {
        EventHandler eventHandler =  EventManager.getEventMap().get(eventEnum);
        if(eventHandler == null) return;
        eventHandler.Run(EventContext.create(event));
    }
}
