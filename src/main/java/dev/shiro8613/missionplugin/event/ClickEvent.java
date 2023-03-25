package dev.shiro8613.missionplugin.event;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class ClickEvent implements Listener {
    @EventHandler
    public void onClick(PlayerInteractEvent event){
        //ClickAction
        if(!event.hasBlock()) return;

        Block clickedBlock = Objects.requireNonNull(event.getClickedBlock());
        if(clickedBlock.getType() != Material.POLISHED_BLACKSTONE_BUTTON) return;

    }
}
