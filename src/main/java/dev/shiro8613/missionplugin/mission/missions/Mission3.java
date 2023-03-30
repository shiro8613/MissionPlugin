package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.mission.Mission;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class Mission3 extends Mission {
    private Player player = null;
    private final BossBar bar = Bukkit.createBossBar("ボタンを押せ！", BarColor.YELLOW, BarStyle.SOLID);
    private int tickCounter;
    private int sixMin;
    private int bossBar;
    @Override
    public void Init() {
        player = getPlayers().get(0);
        tickCounter = 0;
        sixMin = 20;//500; bossBar = 10;
        bossBar = 10;
    }
    @Override
    public void Tick() {
        if (tickCounter == sixMin && bossBar >= 0) {
            bar.setProgress(bossBar--*0.1);
            bar.addPlayer(player);
            bar.setVisible(true);
            tickCounter = 0;
            if (bossBar == 0) {
                bossBar = -1;
                bar.removeAll();
                missionEnd();
            }

        }
        else if (tickCounter < sixMin) {
            tickCounter++;
        }
    }
    @Override
    public void onDisable() {
        //
    }
}
