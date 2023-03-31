package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.command.CommandContext;
import dev.shiro8613.missionplugin.event.EventEnum;
import dev.shiro8613.missionplugin.mission.Mission;
import dev.shiro8613.missionplugin.utils.timer.Timer;
import dev.shiro8613.missionplugin.utils.timer.TimerEnum;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Mission4 extends Mission {

    private List<Player> nonHunters = null;
    private Location boxLocation;
    private final int timeLimit = 3 * Timer.TICKS_1_MIN;
    private final String timerName = "mission.4.otukai";

    @Override
    public void Init() {
        nonHunters = getPlayers().stream().filter(p -> p.getScoreboard().getTeam("hunter") == null).toList();

        greet();
        getTimerManager().createTimer(timerName, TimerEnum.CountDown, timeLimit, BarColor.BLUE, BarStyle.SOLID);
        getTimerManager().getTimerByName(timerName).setSubscribers(nonHunters);
        getTimerManager().getTimerByName(timerName).setVisibility(true);

        getCommandManager().addCmd("setLocation", context -> {
            Player player = (Player) context.getCommandSender();
            Location location = player.getLocation();
            boxLocation = new Location(player.getWorld(), location.getX(), location.getBlockY() -1, location.getBlockZ());
        });
    }

    @Override
    public void Tick() {
        getTimerManager().getTimerByName(timerName).tickTimer();

        getJavaPlugin().getServer().getWorlds().forEach(world -> {

        });


        if(getTimerManager().getTimerByName(timerName).isFinished()) {
            //終処理
            missionEnd();
        }
    }

    private void greet() {
        Component title = Component.text("ミッション発動: はじめてのおつかい");
        Component subTitle = Component.text("詳細はチャットを確認してください");

        this.nonHunters.forEach(player -> {
            player.showTitle(Title.title(title, subTitle));
            player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Sound.Source.PLAYER, 1.0f, 1.0f));

            player.sendMessage(Component.text("あら、コーヒーを買ってくるのを忘れちゃったわ。", NamedTextColor.YELLOW));
            player.sendMessage(Component.text("ちょっと そこのあなた、フードコートのカフェで", NamedTextColor.YELLOW));
            player.sendMessage(Component.text("コーヒーを買ってきてくれないかしら。", NamedTextColor.YELLOW));
            player.sendMessage(Component.text("持ってきてくれたら素敵なプレゼントをあげる。", NamedTextColor.YELLOW));
            player.sendMessage(Component.text("プレゼントは", NamedTextColor.YELLOW)
                            .append(Component.text("一人", NamedTextColor.AQUA)
                            .append(Component.text("だけよ～。", NamedTextColor.YELLOW))));

        });
    }


    @Override
    public void onDisable() {

    }
}
