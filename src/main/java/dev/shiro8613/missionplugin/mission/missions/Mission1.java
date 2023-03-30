package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.mission.Mission;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;



public class Mission1 extends Mission {

    private final BossBar bossBar = Bukkit.createBossBar("残り時間:", BarColor.YELLOW, BarStyle.SOLID);
    private int tick = 0;
    private int sec;
    private int tmpSec;
    private double view;
    @Override
    public void Init() {
        bossBar.setVisible(true);
        sec = 30;
        view = 1;
        tmpSec = sec % 100;

    }

    @Override
    public void Tick() {
        getPlayers().forEach(bossBar::addPlayer);
        if(sec <= 0) { bossBar.removeAll(); missionEnd(); return; }
        if((tick % 20 == 0)) { sec --; tick = 0;}
        if((tick % 4) == 0) view -= tmpSec;
        bossBar.setTitle("残り時間: " + sec + "秒");
        bossBar.setProgress(view);
        tick++;

    }

    @Override
    public void onDisable() {
        bossBar.removeAll();
    }

}
