package dev.shiro8613.missionplugin.utils.timer;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.jetbrains.annotations.Nullable;

/**
 * カウントダウンの方の逆の動作をするタイマー実装。
 */
public class TaskProgressBar extends Timer {
    public TaskProgressBar(int goal, BarColor barColor, BarStyle barStyle, @Nullable BarFlag... barFlags) {
        super(goal, TimerEnum.CountDown, barColor, barStyle, barFlags);
        this.setPrefix("ミッション完了まであと ");
        this.setSuffix("人");
        this.tickProgress = 0;
        this.updateRemainingTime();
        this.bar.setProgress(0);
    }

    @Override
    public double tickTimer() {
        this.tickProgress++;
        this.updateRemainingTime();
        return super.tickTimer();
    }

    public double setCurrent(int i) {
        if (i > this.getTickGoal() || i < 0) {
            return this.getProgress();
        } else {
            this.tickProgress = i;
            this.updateRemainingTime();
            return super.tickTimer();
        }
    }

    @Override
    public boolean isFinished() {
        return this.getTickGoal() == this.tickProgress;
    }

    @Override
    protected void updateRemainingTime() {
        var text = String.format("%s%d%s", this.getPrefix(), this.getTickGoal() - this.tickProgress, this.getSuffix());
        this.bar.setTitle(text);
    }
}
