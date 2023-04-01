package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.event.EventEnum;
import dev.shiro8613.missionplugin.mission.Mission;
import dev.shiro8613.missionplugin.utils.timer.Timer;
import dev.shiro8613.missionplugin.utils.timer.TimerEnum;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.Button;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Objects;

import static org.bukkit.Bukkit.getServer;


public class Mission3 extends Mission {
    private Player player = null;
    private List<Player> nonHunters = null;

    private enum MissionState {Start, OnGoing, End}

    private final BossBar bar = Bukkit.createBossBar("残り時間:", BarColor.YELLOW, BarStyle.SOLID);
    private final ItemStack reward = new ItemStack(Material.AIR);
    private int timeLimit;
    private List<Player> challengers = null;
    private MissionState state = MissionState.Start;
    private final int requiredChecks = 5;
    private Location buttonLocation;

    @Override
    public void Init() {
        challengers = getPlayers().stream().filter(p -> p.getScoreboard().getTeam("challenger") != null).toList();
        nonHunters = getPlayers().stream().filter(p -> p.getScoreboard().getTeam("hunter") == null).toList();
        state = MissionState.OnGoing;
        player = getPlayers().get(0);
        timeLimit = 5 * Timer.TICKS_1_MIN;
        buttonLocation = new Location(getServer().getWorld("world"), 0, 63, 11);
        spawnStoneButton(buttonLocation);
        buttonLocation.setY(buttonLocation.getY() + 1);

        getEventManager().registerEventHandler(EventEnum.ClickEvent, eventContext -> {
            PlayerInteractEvent playerInteractEvent = eventContext.getEvent(EventEnum.ClickEvent);
            Block block = playerInteractEvent.getClickedBlock();
            if (Objects.requireNonNull(block).getType().equals(Material.STONE_BUTTON)) {
                //1,1,1は座標
                if (block.getLocation().equals(buttonLocation)) {
                    //処理
                    // 石のボタンを消す処置：ボタンが凹んでる時にもう一回押さないと反映しない
                    removeStoneButton(buttonLocation);
                }
            }
        });

        greet();

        getTimerManager().createTimer("mission.3.push_button", TimerEnum.CountDown, timeLimit, BarColor.BLUE, BarStyle.SOLID);

        getTimerManager().getTimerByName("mission.3.push_button").setSubscribers(nonHunters);
        getTimerManager().getTimerByName("mission.3.push_button").setVisibility(true);
    }

    @Override
    public void Tick() {

        getTimerManager().getTimerByName("mission.3.push_button").tickTimer();

        if (getTimerManager().getTimerByName("mission.3.push_button").isFinished()) {
            // ミッション失敗！
            state = MissionState.End;
            final var failTitle = Component.text("ミッション失敗", NamedTextColor.RED);
            final var failSubTitle = Component.text("ミッションに失敗したため、逃走者全員に発光エフェクトが付与されました。", NamedTextColor.GOLD, TextDecoration.ITALIC);
            var deBuff = new PotionEffect(PotionEffectType.GLOWING, Timer.TICKS_1_SEC * 10, 1);
            challengers.forEach(p -> {
                p.addPotionEffect(deBuff);
                p.showTitle(Title.title(failTitle, failSubTitle));
                p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ELDER_GUARDIAN_CURSE, Sound.Source.HOSTILE, 1f, 1.1f));
            });
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
        removeStoneButton(buttonLocation);
    }

    public void greet() {

        // 報酬アイテム情報の設定
        reward.setType(Material.POTION);
        var pm = (PotionMeta) reward.getItemMeta();
        pm.setBasePotionData(new PotionData(PotionType.WATER));
        pm.displayName(Component.translatable("item.minecraft.potion.effect.swiftness").color(NamedTextColor.AQUA));
        pm.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 10 * Timer.TICKS_1_SEC, 3, false, true, true), true);
        reward.setItemMeta(pm);

        var title = Component.text("ミッション発動: ボタンを押せ！", NamedTextColor.YELLOW);
        var subTitle = Component.text("詳細はチャットを確認してください", NamedTextColor.GRAY, TextDecoration.ITALIC);

        nonHunters.forEach(p -> {
            p.showTitle(Title.title(title, subTitle));
            p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Sound.Source.PLAYER, 1.0f, 1.0f));
            p.sendMessage(Component.text("おいおい、さっきからずっと隠れてる人がいるんじゃないか？逃走中なのにけしからん！ショッピングモール内に石のボタン（ダイヤモンド ブロックに設置されている）を10個置いた。制限時間は5分。全部おせなきゃ全員発光！ミッションを達成してもボタンを押さなかった人は発光するぞ！ボタンを押して、ミッションも達成できたら特殊アイテムゲットだ！", NamedTextColor.YELLOW));
        });
    }

    // 石のボタンを座標指定で出現させる関数
    public void spawnStoneButton(Location location) {
        Block buttonBlock = location.getBlock().getRelative(BlockFace.UP); // 上向きの石のボタンを作成するためにブロックの上の位置を取得する
        buttonBlock.setType(Material.STONE_BUTTON); // ブロックを石のボタンに設定
    }

    // 石のボタンを座標指定で消す関数
    public void removeStoneButton(Location location) {
        Block block = location.getWorld().getBlockAt(location);
        block.setType(Material.AIR);
        player.sendMessage("ボタンを消去!");
    }

}