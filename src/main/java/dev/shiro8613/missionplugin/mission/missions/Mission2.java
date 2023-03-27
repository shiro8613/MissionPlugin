package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.mission.Mission;
import dev.shiro8613.missionplugin.event.EventEnum;
import dev.shiro8613.missionplugin.mission.Mission;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Mission2 extends Mission{

    private Player player = null;
    private BossBar bar;
    private int bossBar;
    private int tickCounter;
    private int sixMin = 180*2;

    @Override
    public void Init() {
        player = getPlayers().get(0);
        getJavaPlugin().getServer().broadcast("Hello!", "");
        player.sendTitle("ミッション発動\n迷子のお知らせ","Subtitle",10,70,20);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
        bar = Bukkit.createBossBar("残り時間:", BarColor.YELLOW, BarStyle.SOLID);
        bossBar = 10;
        tickCounter = sixMin;
    }

    @Override
    public void Tick() {

        if (tickCounter == sixMin && bossBar*0.1 >= 0) {
            bar.removeAll();
            bar = Bukkit.createBossBar("残り時間:", BarColor.YELLOW, BarStyle.SOLID);
            bar.setProgress(0.1*bossBar--);
            getJavaPlugin().getServer().sendMessage(Component.text("->"));
            getJavaPlugin().getServer().sendMessage( Component.text(bossBar*0.1));
            bar.addPlayer(player);
            bar.setVisible(true);
            tickCounter = 0;
        }
        else if (tickCounter < sixMin) {
            tickCounter++;
        }

        getJavaPlugin().getServer().sendMessage(Component.text(tickCounter));
        //missionEnd();
    }
}
