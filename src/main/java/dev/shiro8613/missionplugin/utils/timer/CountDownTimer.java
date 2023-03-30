package dev.shiro8613.missionplugin.utils.timer;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;

import javax.annotation.Nullable;

public class CountDownTimer extends Timer{
    public CountDownTimer(int goal, BarColor barColor, BarStyle barStyle, @Nullable BarFlag... barFlags) {
        super(goal, TimerEnum.CountDown, barColor, barStyle, barFlags);
        this.tickProgress = goal;
        this.updateRemainingTime();
    }

    @Override
    public double tickTimer() {
        this.tickProgress--;
        return super.tickTimer();
    }

    @Override
    public boolean isFinished() {
        return this.tickProgress == 0;
    }

    @Override
    protected void updateRemainingTime()  {
        String timeStr = "";
        int tmpTick = this.tickProgress;
        if (tmpTick >= TICKS_1_MIN) {
            timeStr += (tmpTick / TICKS_1_MIN) + "分";
            tmpTick %= TICKS_1_MIN;
        }
        timeStr += (tmpTick / TICKS_1_SEC) + "秒";
        this.bar.setTitle("残り時間: " + timeStr);
    }
}
