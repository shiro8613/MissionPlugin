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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dev.shiro8613.missionplugin.mission.missions.Mission1.testTeam;
import static org.bukkit.Bukkit.getServer;


public class Mission3 extends Mission {
    private final BossBar bar = Bukkit.createBossBar("残り時間:", BarColor.YELLOW, BarStyle.SOLID);
    private final ItemStack reward = new ItemStack(Material.AIR);
    private final int requiredChecks = 5;
    private Player player = null;
    private List<Player> nonHunters = null;
    private int timeLimit;
    private List<Player> challengers = null;
    private List<Player> pressedPlayers = new ArrayList<Player>();
    private MissionState state = MissionState.Start;
    private Location buttonLocation;
    private List<Location> buttonLocationList = new ArrayList<Location>();
    
    private boolean allComplete = false;

    @Override
    public void Init() {
        challengers = getPlayers().stream().filter(p -> testTeam(p,"nige")).toList();
        nonHunters = getPlayers().stream().filter(p -> !testTeam(p,"oni")).toList();
        state = MissionState.OnGoing;
        player = getPlayers().get(0);
        timeLimit = 5 * Timer.TICKS_1_MIN;

        for (int i = 0; i <= 9; i++) {
            buttonLocation = new Location(getServer().getWorld("world"), 0, 63, 11-(i+i));
            spawnStoneButton(buttonLocation);
            buttonLocation.setY(buttonLocation.getY() + 1);
            buttonLocationList.add(buttonLocation);
        }
//        buttonLocationList.remove(buttonLocation);

        getEventManager().registerEventHandler(EventEnum.ClickEvent, eventContext -> {
            PlayerInteractEvent playerInteractEvent = eventContext.getEvent(EventEnum.ClickEvent);
            Block block = playerInteractEvent.getClickedBlock();
            if (Objects.requireNonNull(block).getType().equals(Material.STONE_BUTTON)) {
                player.sendMessage(String.valueOf(buttonLocationList.size()));
                for (int i = 0; i < buttonLocationList.size(); i++) {
                    buttonLocation = buttonLocationList.get(i);

                    if (block.getLocation().equals(buttonLocation)) {
                        playerInteractEvent.setCancelled(true);
                        player.sendMessage(playerInteractEvent.getPlayer().getName() + "にボタンが押されました！");
                        removeStoneButton(buttonLocation);
                        buttonLocationList.remove(buttonLocation);
                    }
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
        if (buttonLocationList.isEmpty()) {
            // ミッション成功！
            player.sendMessage(Component.text("ミッション3に成功!", NamedTextColor.YELLOW, TextDecoration.UNDERLINED, TextDecoration.BOLD));
            getCommandManager().removeAll();
            missionEnd();
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
        rotateButtonUp(buttonBlock);
    }

    // 石のボタンを座標指定で消す関数
    public void removeStoneButton(Location location) {
        Block block = location.getBlock();
        block.setType(Material.AIR);
        player.sendMessage(String.valueOf(block.getType()));
        player.sendMessage("ボタンを消去!");
    }

    // 石のボタンを上方向に回転させるメソッド
    public void rotateButtonUp(Block buttonBlock) {
        if (buttonBlock.getType() != Material.STONE_BUTTON) {
            return;
        }

        BlockData buttonData = buttonBlock.getBlockData();
        if (!(buttonData instanceof Directional)) {
            return;
        }

        Directional directional = (Directional) buttonData;
        BlockFace currentFacing = directional.getFacing();
        BlockFace newFacing = currentFacing;

        directional.setFacing(newFacing);
        buttonBlock.setBlockData(directional);
    }

    private enum MissionState {Start, OnGoing, End}

}