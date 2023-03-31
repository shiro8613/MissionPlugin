package dev.shiro8613.missionplugin.utils.timer;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;

import javax.annotation.Nullable;

/**
 * カウントダウンタイプのタイマー実装
 */
public class CountDownTimer extends Timer{
    public CountDownTimer(int goal, BarColor barColor, BarStyle barStyle, @Nullable BarFlag... barFlags) {
        super(goal, TimerEnum.CountDown, barColor, barStyle, barFlags);
        this.tickProgress = goal;
        this.updateRemainingTime();
        this.setPrefix("残り時間: ");
    }

    @Override
    public double tickTimer() {
        this.tickProgress--;
        if (this.tickProgress % TICKS_1_SEC == 0) {this.updateRemainingTime();}
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
        this.bar.setTitle(this.getPrefix() + timeStr + this.getSuffix());
    }
}
