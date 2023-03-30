package dev.shiro8613.missionplugin.utils.timer;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link Timer}を内部の{@link Map}に内梱します。
 * {@link Timer}の{@linkplain TimerManager#createTimer(String, TimerEnum, int, BarColor, BarStyle, BarFlag...) 作成}・{@linkplain TimerManager#activeTimers 保管}・{@linkplain TimerManager#getTimerByName(String) 取得}・{@linkplain TimerManager#discardTimerByName(String) 削除}を担います。
 */
public class TimerManager {

    private final Map<String, Timer> activeTimers;

    public TimerManager() {
        this.activeTimers = new HashMap<>();
    }

    /**
     * {@link Timer}を取得します。
     * @param query 作成時に指定した識別子
     * @return 識別子に合致する {@link Timer}、合致するものがない場合は{@code null}
     */
    public Timer getTimerByName(String query) {
        return activeTimers.get(query);
    }

    /**
     * {@link Timer}を{@link TimerManager}から削除します
     * @param query 作成時に指定した識別子
     */
    public void discardTimerByName(String query) {
        Timer target = activeTimers.get(query);
        if (target == null) {
            return;
        }
        target.setVisibility(false);
        activeTimers.remove(query);
    }

    /**
     * {@link Timer}を作成して{@link TimerManager}に登録します。
     * 登録後は {@link TimerManager#getTimerByName(String 識別子)}や{@link TimerManager#discardTimerByName(String 識別子)}を用いて{@link Timer}の取得・破棄が可能です。
     * @param name 上述の関数で{@link Timer}を特定するためにつける識別子
     * @param mode 作るタイマーの種類
     * @param goal 0からこの数値までのタイマーを作ります
     * @param barColor タイマーのボスバーの色 {@link org.bukkit.Bukkit#createBossBar(String, BarColor, BarStyle, BarFlag...)}
     * @param barStyle タイマーのボスバーのスタイル
     * @param barFlags タイマーのボスバーのフラグ
     * @return 作成した {@link Timer}
     */
    public Timer createTimer(String name, @NotNull TimerEnum mode, int goal, BarColor barColor, BarStyle barStyle, @Nullable BarFlag... barFlags) {
        try {
            // リフレクションって何ですか？🤔って言ってそうな人が↓のコードを錬成しました。
            // より良いものにできる人お願いします。🙏
            var timer = mode.getImplClass().getConstructor(Integer.TYPE, BarColor.class, BarStyle.class, BarFlag[].class).newInstance(goal, barColor, barStyle, barFlags);
            this.activeTimers.put(name, timer);
            return timer;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
