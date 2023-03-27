package dev.shiro8613.missionplugin.event;

public class Event {
    public void onEvent(EventEnum eventEnum, org.bukkit.event.Event event) {
        EventHandler eventHandler =  EventManager.getEventMap().get(eventEnum);
        if(eventHandler == null) return;
        eventHandler.Run(event);
    }
}
