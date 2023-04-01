package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.command.CommandContext;
import dev.shiro8613.missionplugin.event.EventContext;
import dev.shiro8613.missionplugin.event.EventEnum;
import dev.shiro8613.missionplugin.mission.Mission;
import dev.shiro8613.missionplugin.utils.timer.Timer;
import dev.shiro8613.missionplugin.utils.timer.TimerEnum;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.ShulkerBox;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static dev.shiro8613.missionplugin.mission.missions.Mission1.testTeam;

public class Mission4 extends Mission {

    private final int timeLimit = 3 * Timer.TICKS_1_MIN;
    private final String timerName = "mission.4.otukai";
    private final ItemStack pickedItem = new ItemStack(Material.APPLE);
    private final ItemStack giveItem = new ItemStack(Material.LEATHER_BOOTS);
    private final List<InventoryAction> placeActions = Arrays.asList(InventoryAction.PLACE_SOME, InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE);
    private List<Player> nonHunters = null;
    private List<Player> challenger = null;
    private Map<Player, Boolean> markingPlayer = null;
    private Location rewardPos = null;
    private Location momPos = null;
    private ShulkerBox box = null;
    private ArmorStand momArmor = null;
    private MissionState state = null;
    private Player successPlayer = null;

    @Override
    public void Init() {
        state = MissionState.Setup;
        onStateChange();

        nonHunters = getPlayers().stream().filter(p -> !testTeam(p,"oni")).toList();
        challenger = getPlayers().stream().filter(p -> testTeam(p,"nige")).toList();

        getTimerManager().createTimer(timerName, TimerEnum.CountDown, timeLimit, BarColor.BLUE, BarStyle.SOLID);
        getTimerManager().getTimerByName(timerName).setSubscribers(nonHunters);
        getTimerManager().getTimerByName(timerName).setVisibility(false);

        getEventManager().registerEventHandler(EventEnum.ClickEvent, this::openBox);
        getEventManager().registerEventHandler(EventEnum.InventoryClickEvent, this::updateBox);
    }

    @Override
    public void Tick() {
        if (state != MissionState.OnGoing) {
            return;
        }

        getTimerManager().getTimerByName(timerName).tickTimer();

        markingPlayer.forEach(((player, aBoolean) -> {
            if (aBoolean) {
                if (!box.getInventory().contains(giveItem)) {
                    if (player.getInventory().contains(giveItem)) {
                        successPlayer = player;
                    } else {
                        challenger.forEach(p -> {
                            if (p.getInventory().contains(giveItem)) {
                                p.sendMessage(Component.text("盗んじゃダメでしょ！", NamedTextColor.YELLOW));
                                p.sendMessage(Component.text("ぼっしゅ～", NamedTextColor.YELLOW));
                                p.getInventory().remove(giveItem);
                                box.getInventory().setItem(1, giveItem);
                            }
                        });
                    }
                }
            }
        }));

        if (getTimerManager().getTimerByName(timerName).isFinished() || successPlayer != null) {
            state = MissionState.End;
            onStateChange();
        }
    }

    private void openBox(EventContext eventContext) {
        PlayerInteractEvent event = eventContext.getEvent(EventEnum.ClickEvent);
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (Objects.requireNonNull(block).getType().equals(Material.SHULKER_BOX) && block.getLocation().equals(box.getLocation())) {
            if (player.getInventory().contains(pickedItem)) {
                markingPlayer.putIfAbsent(player, false);
            }
        }
    }

    private void updateBox(EventContext eventContext) {
        InventoryClickEvent event = eventContext.getEvent(EventEnum.InventoryClickEvent);
        Player player = (Player) event.getWhoClicked();
        InventoryAction action = event.getAction();
        if (placeActions.contains(action)) {
            if (Objects.requireNonNull(event.getCursor()).equals(pickedItem)) {
                if (markingPlayer.containsKey(player)) {
                    box.getInventory().clear();
                    box.getInventory().setItem(1, giveItem);
                    markingPlayer.replace(player, true);
                }
            }
        }
    }

    private void setRewardPosCmd(CommandContext ctx) {
        if (ctx.getCommandSender() instanceof ConsoleCommandSender) {
            ctx.getCommandSender().sendMessage(Component.text("このコマンドはプレイヤーで実行してください", NamedTextColor.RED));
            return;
        }

        if (ctx.getCommandSender() instanceof Player) {
            var targetRes = ((Player) ctx.getCommandSender()).rayTraceBlocks(5);
            @org.jetbrains.annotations.NotNull Block tmpPresPlate;
            try {
                assert targetRes != null;
                tmpPresPlate = Objects.requireNonNull(targetRes.getHitBlock()).getRelative(BlockFace.UP);
            } catch (NullPointerException _e) {
                ctx.getCommandSender().sendMessage("上にチェストを設置可能なブロックに焦点を合わせてコマンドを打ってください");
                return;
            }
            if (tmpPresPlate.isEmpty()) {
                rewardPos = tmpPresPlate.getLocation();
                ((Player) ctx.getCommandSender()).spawnParticle(Particle.REDSTONE, tmpPresPlate.getLocation().add(0.5d, 0.2d, 0.5), 15, new Particle.DustOptions(Color.LIME, 1.0f));

                BlockFace face = ((Player) ctx.getCommandSender()).getFacing();
                if (face.equals(BlockFace.NORTH) || face.equals(BlockFace.SOUTH)) {
                    momPos = new Location(rewardPos.getWorld(), rewardPos.getX() + 1.5, rewardPos.getY() + 1, rewardPos.getZ());
                } else if (face.equals(BlockFace.WEST) || face.equals(BlockFace.EAST)) {
                    momPos = new Location(rewardPos.getWorld(), rewardPos.getX(), rewardPos.getY() + 1, rewardPos.getZ() + 1.5);
                }

                ctx.getCommandSender().sendMessage(Component.text(String.format("報酬チェストの設置場所を%sに設定しました。", rewardPos.toString()), NamedTextColor.GREEN));
                checkReady(ctx);
            } else {
                ctx.getCommandSender().sendMessage(Component.text("このブロックの上には何かがあります。", NamedTextColor.RED));
            }
        }
    }

    private void startCmd(CommandContext ctx) {
        state = MissionState.OnGoing;
        onStateChange();
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

    private void checkReady(CommandContext ctx) {
        if (rewardPos != null) {
            ctx.getCommandSender().sendMessage(Component.text("ミッションの設定が完了しました。ミッションを開始可能です。", NamedTextColor.GREEN));
            state = MissionState.Ready;
            onStateChange();
        }
        if (rewardPos == null) {
            ctx.getCommandSender().sendMessage(Component.text("ミッションを実行するには物資交換用チェストの出現地点を設定してください", NamedTextColor.RED));
        }
    }

    private void onStateChange() {
        getCommandManager().removeAll();
        switch (state) {
            case Setup -> {
                getCommandManager().addCmd("setRewardPos", this::setRewardPosCmd);
            }
            case Ready -> {
                getCommandManager().addCmd("setRewardPos", this::setRewardPosCmd);
                getCommandManager().addCmd("start", this::startCmd);
            }
            case OnGoing -> {
                getTimerManager().getTimerByName(timerName).setVisibility(true);

                rewardPos.getBlock().setType(Material.SHULKER_BOX);
                box = (ShulkerBox) rewardPos.getBlock().getState();
                momArmor = momPos.getWorld().spawn(momPos, ArmorStand.class);
                //何かするならここ

                markingPlayer = new HashMap<>();

                greet();
            }
            case End -> {
                if (getTimerManager().getTimerByName(timerName).isFinished() && successPlayer == null) {
                    Component failTitle = Component.text("ミッション失敗", NamedTextColor.YELLOW);
                    Component failSubTitle = Component.text("ミッションに失敗したため、逃走者全員に盲目エフェクトが付与されました", NamedTextColor.YELLOW);
                    nonHunters.forEach(player -> {
                        player.showTitle(Title.title(failTitle, failSubTitle));
                        player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ELDER_GUARDIAN_CURSE, Sound.Source.HOSTILE, 1f, 1.1f));
                    });
                    challenger.forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * Timer.TICKS_1_SEC, 1)));
                } else {
                    Component successTitle = Component.text("ミッションがクリアされました", NamedTextColor.YELLOW);
                    Component successSubTitle = Component.text("");

                    nonHunters.forEach(player -> {
                        player.sendMessage(Component.text(successPlayer.getName(), NamedTextColor.AQUA)
                                .append(Component.text("さんがミッションにクリアしました")));
                        player.showTitle(Title.title(successTitle, successSubTitle));
                        player.playSound(Sound.sound(org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, Sound.Source.HOSTILE, 1f, 1.1f));
                    });
                    successPlayer.sendMessage(Component.text("あなたはミッションにクリアしました", NamedTextColor.YELLOW));
                    successPlayer.sendMessage(Component.text("報酬のブーツにより、", NamedTextColor.YELLOW)
                            .append(Component.text("-51 -59 30", NamedTextColor.AQUA))
                            .append(Component.text("地点にある", NamedTextColor.YELLOW)));
                    successPlayer.sendMessage(Component.text("冷房室に入ることができるようになりました", NamedTextColor.YELLOW));
                    successPlayer.sendMessage(Component.text("そこには何か", NamedTextColor.YELLOW)
                            .append(Component.text("特別なアイテム", NamedTextColor.GOLD))
                            .append(Component.text("があるとか、ないとか...", NamedTextColor.YELLOW)));
                }

                box.getBlock().setType(Material.AIR);
                momArmor.remove();
                missionEnd();
            }
        }
    }


    @Override
    public void onDisable() {
        getEventManager().removeAll();
        getCommandManager().removeAll();
        getTimerManager().discardTimerByName(timerName);
        nonHunters = null;
        challenger = null;
        markingPlayer = null;
        rewardPos = null;
        momPos = null;
        box = null;
        momArmor = null;
        state = null;
        successPlayer = null;

    }
    private enum MissionState {Setup, Ready, OnGoing, End}
}