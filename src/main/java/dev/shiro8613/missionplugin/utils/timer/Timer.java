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

/**
 * タイマーです。このままでは使えないので継承・実装されたものを使ってください。
 */
public abstract class Timer {

    /**
     * 1秒に相当するTick数です
     */
    public static final int TICKS_1_SEC = 20;
    /**
     * 1分に相当するTick数です
     */
    public static final int TICKS_1_MIN = TICKS_1_SEC * 60;
    /**
     * タイマーのボスバー
     */
    protected BossBar bar;
    /**
     * タイマーの現在の進捗(整数)
     */
    protected int tickProgress;
    private final int tickGoal;
    private final TimerEnum mode;
    public Timer(int goal, TimerEnum mode, BarColor barColor, BarStyle barStyle, @Nullable BarFlag... barFlags) {
        this.bar = Bukkit.createBossBar("", barColor, barStyle, barFlags);
        this.tickGoal = goal;
        this.mode = mode;
    }

    /**
     * タイマーを1回進めます。
     * どの方向にどれだけ進むかはタイマーの種類により変化することがあります。
     * 派生クラスで実装する際は {@code super.tickTimer()} を呼び出してください。
     * ボスバーの進捗更新を行い、割合表記の進捗を返却します。
     * @return タイマーの進捗(割合)
     */
    public double tickTimer() {
        bar.setProgress((double) tickProgress / tickGoal);
        return this.getProgress();
    }

    /**
     * タイマーが最後まで進んだかどうかを取得します。
     * 条件はタイマーの種類により違うため、派生クラスで実装してください。
     * @return 終了条件に合致したかどうか
     */
    public abstract boolean isFinished();

    /**
     * タイマー(ボスバー)の進捗を割合で取得します。
     * @return タイマーの進捗(0.0 - 1.0)
     */
    public double getProgress() {
        return (double) this.tickProgress / this.tickGoal;
    }

    /**
     * タイマーの総ステップ数を取得します。
     * タイマーはこの値と現在のステップ数を基に進捗を計算・更新を行います。
     * @return タイマーの総ステップ数
     */
    protected int getTickGoal() {
        return this.tickGoal;
    }

    /**
     * タイマーのボスバーのタイトルを更新する関数です。
     * どの様な内容に更新するかは派生クラスで実装してください。
     */
    protected abstract void updateRemainingTime();

    /**
     * 指定したプレイヤーをタイマーの表示対象に追加します。
     * @param player タイマーを表示するプレイヤー
     */
    public void addSubscriber(Player player) {
        this.bar.addPlayer(player);
    }

    /**
     * 指定したプレイヤーをタイマーの表示対象から外します
     * @param player タイマーを非表示にするプレイヤー
     */
    public void removeSubscriber(Player player) {
        this.bar.removePlayer(player);
    }

    /**
     * 指定したプレイヤー群のみをタイマーの表示対象にします
     * @param playerList タイマーの表示対象にするプレイヤーの{@link List}
     */
    public void setSubscribers(@NotNull List<Player> playerList) {
        this.bar.removeAll();
        playerList.forEach(this::addSubscriber);
    }

    /**
     * タイマーの表示状態を変更します。
     * @param state 表示するか否か
     */
    public void setVisibility(boolean state) {
        this.bar.setVisible(state);
    }

    /**
     * タイマーの種類を取得します。
     * @return タイマーの種類
     */
    public TimerEnum getMode() {
        return mode;
    }
}
