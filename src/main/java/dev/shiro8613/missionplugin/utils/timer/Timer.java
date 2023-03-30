package dev.shiro8613.missionplugin.utils.timer;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Timer {

    public static final int TICKS_1_SEC = 20;
    public static final int TICKS_1_MIN = TICKS_1_SEC * 60;
    protected BossBar bar;
    protected int tickProgress;
    private final int tickGoal;
    private final TimerEnum mode;
    public Timer(int goal, TimerEnum mode, BarColor barColor, BarStyle barStyle, @Nullable BarFlag... barFlags) {
        this.bar = Bukkit.createBossBar("", barColor, barStyle, barFlags);
        this.tickGoal = goal;
        this.mode = mode;
    }

    public double tickTimer() {
        bar.setProgress((double) tickProgress / tickGoal);
        if (this.tickProgress % TICKS_1_SEC == 0) {this.updateRemainingTime();}
        return this.getProgress();
    }

    public abstract boolean isFinished();
    public double getProgress() {
        return (double) this.tickProgress / this.tickGoal;
    }

    protected int getTickGoal() {
        return this.tickGoal;
    }

    protected abstract void updateRemainingTime();

    public void addSubscriber(Player player) {
        this.bar.addPlayer(player);
    }

    public void removeSubscriber(Player player) {
        this.bar.removePlayer(player);
    }

    public void setSubscribers(@NotNull List<Player> playerList) {
        this.bar.removeAll();
        playerList.forEach(this::addSubscriber);
    }

    public void setVisibility(boolean state) {
        this.bar.setVisible(state);
    }

    public TimerEnum getMode() {
        return mode;
    }
}
