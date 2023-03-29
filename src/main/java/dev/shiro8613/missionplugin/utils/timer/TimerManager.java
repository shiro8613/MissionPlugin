package dev.shiro8613.missionplugin.utils.timer;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class TimerManager {

    private final Map<String, Timer> activeTimers;

    public TimerManager() {
        this.activeTimers = new HashMap<>();
    }

    public Timer getTimerByName(String query) {
        return activeTimers.get(query);
    }

    public void discardTimerByName(String query) {
        Timer target = activeTimers.get(query);
        target.setVisibility(false);
        activeTimers.remove(query);
    }

    public Timer createTimer(String name, @NotNull TimerEnum mode, int goal, BarColor barColor, BarStyle barStyle, @Nullable BarFlag... barFlags) {
        try {
            // リフレクションって何ですか？🤔って言ってそうな人が↓のコードを錬成しました。
            // より良いものにできる人お願いします。🙏
            Timer timer = mode.getImplClass().getConstructor(Integer.TYPE, BarColor.class, BarStyle.class, BarFlag[].class).newInstance(goal, barColor, barStyle, barFlags);
            this.activeTimers.put(name, timer);
            return timer;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
