package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.mission.Mission;
import dev.shiro8613.missionplugin.utils.timer.Timer;
import dev.shiro8613.missionplugin.utils.timer.TimerEnum;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.List;

public class Mission3 extends Mission {
    private Player player = null;
    private List<Player> nonHunters = null;
    private enum MissionState {Start, OnGoing, End}
    private final BossBar bar = Bukkit.createBossBar("残り時間:", BarColor.YELLOW, BarStyle.SOLID);
    private int timeLimit;
    private List<Player> challengers = null;
    private MissionState state = MissionState.Start;
    private final int requiredChecks = 5;
    @Override
    public void Init() {
        challengers = getPlayers().stream().filter(p -> p.getScoreboard().getTeam("challenger") != null).toList();
        nonHunters = getPlayers().stream().filter(p -> p.getScoreboard().getTeam("hunter") == null).toList();
        state = MissionState.OnGoing;

        getTimerManager().createTimer("mission.3.push_button", TimerEnum.CountDown, timeLimit, BarColor.BLUE, BarStyle.SOLID);

        getTimerManager().getTimerByName("mission.3.push_button").setSubscribers(nonHunters);
        getTimerManager().getTimerByName("mission.3.push_button").setVisibility(true);

        player = getPlayers().get(0);
        timeLimit = 5*Timer.TICKS_1_MIN;

        player.sendMessage("ミッション3を開始！");
    }
    @Override
    public void Tick() {

        getTimerManager().getTimerByName("mission.3.push_button").tickTimer();

        if (getTimerManager().getTimerByName("mission.3.push_button").isFinished()) {
            // ミッション失敗！
            state = MissionState.End;
        }
        if (state == MissionState.End) {
            player.sendMessage(Component.text("ミッション3を終了します", NamedTextColor.YELLOW, TextDecoration.UNDERLINED, TextDecoration.BOLD));
            missionEnd();
        }
    }
    @Override
    public void onDisable() {
        getTimerManager().discardTimerByName("mission.3.push_button");
        getCommandManager().removeAll();
    }
}
