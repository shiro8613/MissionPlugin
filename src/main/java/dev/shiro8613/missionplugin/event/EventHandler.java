package dev.shiro8613.missionplugin.event;

import org.bukkit.event.Event;

public interface EventHandler {

     /**
      * イベントが発火した際に呼ばれる関数
      * @param eventContext この中にイベントの引数等が入っています。
      */
     void Run(EventContext eventContext);
}
