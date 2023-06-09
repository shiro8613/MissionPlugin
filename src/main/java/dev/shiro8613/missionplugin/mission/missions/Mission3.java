package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.event.EventEnum;
import dev.shiro8613.missionplugin.mission.Mission;
import dev.shiro8613.missionplugin.utils.timer.TaskProgressBar;
import dev.shiro8613.missionplugin.utils.timer.Timer;
import dev.shiro8613.missionplugin.utils.timer.TimerEnum;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

import static dev.shiro8613.missionplugin.mission.missions.Mission1.testTeam;
import static org.bukkit.Bukkit.getServer;


public class Mission3 extends Mission {
    private final ItemStack reward = new ItemStack(Material.AIR);
    private final Map<Player, Integer> pressedPlayers = new HashMap<>();// List<Player>に変えても良いくらいだけど取り敢えず残しておこうかな
    private final List<Location> buttonLocationList = new ArrayList<>();
    private List<Player> nonHunters = null;
    private int timeLimit;
    private int buttonNum;
    private List<Player> challengers = null;
    private MissionState state = MissionState.Start;
    private Location buttonLocation;

    private void registerButton(BlockFace face) {
        spawnStoneButton(buttonLocation.add(0, -1, 0), face);
        buttonLocation.add(0, 1, 0);
        buttonLocationList.add(buttonLocation);
    }

    @Override
    public void Init() {
        challengers = getPlayers().stream().filter(p -> testTeam(p, "nige")).toList();
        nonHunters = getPlayers().stream().filter(p -> !testTeam(p, "oni")).toList();
        state = MissionState.OnGoing;
        timeLimit = Timer.TICKS_1_MIN * 5;
        buttonNum = 10;


        // 石のボタンを登録する処理　ここで座標を登録
        buttonLocation = new Location(getServer().getWorld("world"), 40, -60, 31);//N
        registerButton(BlockFace.NORTH);
        buttonLocation = new Location(getServer().getWorld("world"), 2, -43, 25);//N
        registerButton(BlockFace.NORTH);
        buttonLocation = new Location(getServer().getWorld("world"), -40, -60, 28);//W
        registerButton(BlockFace.WEST);
        buttonLocation = new Location(getServer().getWorld("world"), 10, -52, 62);//N
        registerButton(BlockFace.NORTH);
        buttonLocation = new Location(getServer().getWorld("world"), -36, -44, -8);//E
        registerButton(BlockFace.EAST);
        buttonLocation = new Location(getServer().getWorld("world"), -33, -50, 3);//E
        registerButton(BlockFace.EAST);
        buttonLocation = new Location(getServer().getWorld("world"), -4, -60, -19);//N
        registerButton(BlockFace.NORTH);
        buttonLocation = new Location(getServer().getWorld("world"), -95, -24, 24);//W
        registerButton(BlockFace.WEST);
        buttonLocation = new Location(getServer().getWorld("world"), 29, -35, 34);//N
        registerButton(BlockFace.NORTH);
        buttonLocation = new Location(getServer().getWorld("world"), -18, -60, 7);//S
        registerButton(BlockFace.SOUTH);

        getEventManager().registerEventHandler(EventEnum.ClickEvent, eventContext -> {
            PlayerInteractEvent playerInteractEvent = eventContext.getEvent(EventEnum.ClickEvent);
            Block block = playerInteractEvent.getClickedBlock();
            if (Objects.nonNull(block) && block.getType().equals(Material.STONE_BUTTON)) {
                buttonLocationList.removeIf(buttonLocation -> {
                    if (block.getLocation().equals(buttonLocation)) {
                        playerInteractEvent.setCancelled(true);
                        if (!challengers.contains(playerInteractEvent.getPlayer()) || playerInteractEvent.getPlayer().getScoreboardTags().contains("ded")) return false;
                        challengers.forEach(p -> p.sendMessage(Component.text(playerInteractEvent.getPlayer().getName() + "にボタンが押されました！", NamedTextColor.GREEN)));

                        pressedPlayers.merge(playerInteractEvent.getPlayer(), 0, (c, _unused) -> c + 1);

                        removeStoneButton(buttonLocation);
                        ((TaskProgressBar) getTimerManager().getTimerByName("mission.3.count_button")).setCurrent(10 - buttonLocationList.size());
                        return true;
                    }
                    return false;
                });
            }
        });

        greet();

        getTimerManager().createTimer("mission.3.push_button", TimerEnum.CountDown, timeLimit, BarColor.BLUE, BarStyle.SOLID);
        getTimerManager().createTimer("mission.3.count_button", TimerEnum.TaskProgress, buttonLocationList.size(), BarColor.YELLOW, BarStyle.SOLID);

        getTimerManager().getTimerByName("mission.3.push_button").setSubscribers(nonHunters);
        getTimerManager().getTimerByName("mission.3.count_button").setSubscribers(nonHunters);
        getTimerManager().getTimerByName("mission.3.push_button").setVisibility(true);
        getTimerManager().getTimerByName("mission.3.count_button").setVisibility(true);

        TaskProgressBar timerByName = (TaskProgressBar) getTimerManager().getTimerByName("mission.3.count_button");
        timerByName.setCurrent(0);

        getTimerManager().getTimerByName("mission.3.count_button").setPrefix("残りのボタン：");
        getTimerManager().getTimerByName("mission.3.count_button").setSuffix("個");
    }

    @Override
    public void Tick() {

        getTimerManager().getTimerByName("mission.3.push_button").tickTimer();

        if (buttonLocationList.isEmpty()) {
            // ミッション成功！
            onSucceeded();
        }
        if (getTimerManager().getTimerByName("mission.3.push_button").isFinished()) {
            // ミッション失敗！
            onFailed();
        }
        if (state == MissionState.End) {
            nonHunters.forEach(p -> p.sendMessage(Component.text("ミッションを終了します", NamedTextColor.YELLOW, TextDecoration.UNDERLINED, TextDecoration.BOLD)));
            missionEnd();
        }
    }

    @Override
    public void onDisable() {
        getTimerManager().discardTimerByName("mission.3.push_button");
        getTimerManager().discardTimerByName("mission.3.count_button");
        getCommandManager().removeAll();
        buttonLocationList.forEach(this::removeStoneButton);
        pressedPlayers.clear();
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
    public void spawnStoneButton(Location location, BlockFace face) {
        Block buttonBlock = location.getBlock().getRelative(BlockFace.UP); // 上向きの石のボタンを作成するためにブロックの上の位置を取得する
        buttonBlock.setType(Material.STONE_BUTTON); // ブロックを石のボタンに設定
        rotateButtonUp(buttonBlock, face);
    }

    // 石のボタンを座標指定で消す関数
    public void removeStoneButton(Location location) {
        Block block = location.getBlock();
        block.setType(Material.AIR);
    }

    // 石のボタンを上方向に回転させるメソッド
    public void rotateButtonUp(Block buttonBlock, BlockFace face) {
        if (buttonBlock.getType() != Material.STONE_BUTTON) {
            return;
        }

        BlockData buttonData = buttonBlock.getBlockData();
        if (!(buttonData instanceof Directional directional)) {
            return;
        }

        directional.setFacing(face);
        buttonBlock.setBlockData(directional);
    }

    public void onSucceeded() {
        var title = Component.text("ミッションに成功", NamedTextColor.YELLOW);
        var subTitle = Component.text("ボタンを押した人には俊足のポーションを与えました。", NamedTextColor.GRAY, TextDecoration.ITALIC);

        var deBuff = new PotionEffect(PotionEffectType.GLOWING, Timer.TICKS_1_SEC * 10, 1);
        challengers.forEach(player -> {
            if (pressedPlayers.containsKey(player))
                // 押したプレイヤー全員にポーション
                player.getInventory().addItem(reward);
            else if (!player.getScoreboardTags().contains("ded")) {
                // 押せなかった人に発光付与
                player.addPotionEffect(deBuff);
            }
        });
        nonHunters.forEach(p -> {
            p.showTitle(Title.title(title, subTitle));
            p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Sound.Source.PLAYER, 1.0f, 1.0f));
        });
        getCommandManager().removeAll();
        missionEnd();
    }

    public void onFailed() {
        final var failTitle = Component.text("ミッション失敗", NamedTextColor.RED);
        final var failSubTitle = Component.text("ミッションに失敗したため、逃走者全員に発光エフェクトが付与されました。", NamedTextColor.GOLD, TextDecoration.ITALIC);

        nonHunters.forEach(p -> {
            p.showTitle(Title.title(failTitle, failSubTitle));
            p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ELDER_GUARDIAN_CURSE, Sound.Source.HOSTILE, 1f, 1.1f));
        });
        var deBuff = new PotionEffect(PotionEffectType.GLOWING, Timer.TICKS_1_SEC * 10, 1);
        challengers.stream().filter(p -> !p.getScoreboardTags().contains("ded")).forEach(p -> p.addPotionEffect(deBuff));
        state = MissionState.End;
    }

    private enum MissionState {Start, OnGoing, End}

}