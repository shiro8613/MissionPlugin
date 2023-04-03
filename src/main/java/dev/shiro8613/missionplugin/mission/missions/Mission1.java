package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.command.CommandContext;
import dev.shiro8613.missionplugin.event.EventContext;
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
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Powerable;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;


public class Mission1 extends Mission {

    private final int timeLimit = Timer.TICKS_1_MIN * 3;
    private final List<Block> triggers = new ArrayList<>();
    private final int triggerCount = 4;
    private final ItemStack reward = new ItemStack(Material.SPLASH_POTION);
    private final Map<Block, Player> checking = new HashMap<>();
    private List<Player> challengers;
    private List<Player> nonHunters;
    private MissionState state;
    private Location rewardPos = null;

    public static boolean testTeam(Player p, String team) {
        var t = p.getScoreboard().getTeam(team);
        return t != null && t.hasPlayer(p);
    }

    @Override
    public void Init() {
        state = MissionState.Setup;
        onStateChange();
        getEventManager().registerEventHandler(EventEnum.ClickEvent, this::onPressed);

        challengers = getPlayers().stream().filter(p -> testTeam(p, "nige")).toList();
        nonHunters = getPlayers().stream().filter(p -> !testTeam(p, "oni")).toList();

        getTimerManager().createTimer("mission.1.come_on", TimerEnum.CountDown, timeLimit, BarColor.BLUE, BarStyle.SOLID);
        getTimerManager().createTimer("mission.1.reached", TimerEnum.TaskProgress, triggerCount, BarColor.GREEN, BarStyle.SEGMENTED_12);
        getTimerManager().getTimerByName("mission.1.come_on").setSubscribers(nonHunters);
        getTimerManager().getTimerByName("mission.1.reached").setSubscribers(nonHunters);
        getTimerManager().getTimerByName("mission.1.come_on").setVisibility(false);
        getTimerManager().getTimerByName("mission.1.reached").setVisibility(false);

        // 報酬アイテム情報の設定
        reward.setType(Material.SPLASH_POTION);
        var pm = (PotionMeta) reward.getItemMeta();
        pm.setBasePotionData(new PotionData(PotionType.WATER));
        pm.displayName(Component.translatable("item.minecraft.potion.effect.slowness").color(NamedTextColor.AQUA));
        pm.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, 5 * Timer.TICKS_1_SEC, 6, false, true, true), true);
        reward.setItemMeta(pm);
        reward.setAmount(1);
    }

    public void addTriggerCmd(CommandContext ctx) {
        if (ctx.getCommandSender() instanceof ConsoleCommandSender) {
            ctx.getCommandSender().sendMessage(Component.text("このコマンドはプレイヤーで実行してください", NamedTextColor.RED));
            return;
        }

        if (ctx.getCommandSender() instanceof Player) {
            var targetRes = ((Player) ctx.getCommandSender()).rayTraceBlocks(5);
            @org.jetbrains.annotations.NotNull Block tmpPresPlate;
            try {
                tmpPresPlate = targetRes.getHitBlock().getRelative(BlockFace.UP);
            } catch (NullPointerException _e) {
                ctx.getCommandSender().sendMessage("上に感圧版を設置可能なブロックに焦点を合わせてコマンドを打ってください");
                return;
            }
            if (tmpPresPlate.isEmpty() && tmpPresPlate.canPlace(Material.STONE_PRESSURE_PLATE.createBlockData()) && triggers.stream().noneMatch(b -> b.getLocation().equals(tmpPresPlate.getLocation())) && !tmpPresPlate.getLocation().equals(rewardPos)) {
                triggers.add(tmpPresPlate);
                ((Player) ctx.getCommandSender()).spawnParticle(Particle.REDSTONE, tmpPresPlate.getLocation().add(0.5d, 0.2d, 0.5), 15, new Particle.DustOptions(Color.LIME, 1.0f));

                ctx.getCommandSender().sendMessage(Component.text(String.format("%d箇所目のトリガを設置しました。", triggers.size()), NamedTextColor.GREEN));
                checkReady(ctx);
            } else {
                ctx.getCommandSender().sendMessage(Component.text("このブロックの上は感圧板を設置できないか、既にトリガや報酬チェストの地点として登録済みです。", NamedTextColor.RED));
            }
        }
    }

    public void setRewardPosCmd(CommandContext ctx) {
        if (ctx.getCommandSender() instanceof ConsoleCommandSender) {
            ctx.getCommandSender().sendMessage(Component.text("このコマンドはプレイヤーで実行してください", NamedTextColor.RED));
            return;
        }

        if (ctx.getCommandSender() instanceof Player) {
            var targetRes = ((Player) ctx.getCommandSender()).rayTraceBlocks(5);
            @org.jetbrains.annotations.NotNull Block tmpPresPlate;
            try {
                tmpPresPlate = targetRes.getHitBlock().getRelative(BlockFace.UP);
            } catch (NullPointerException _e) {
                ctx.getCommandSender().sendMessage("上にチェストを設置可能なブロックに焦点を合わせてコマンドを打ってください");
                return;
            }
            if (tmpPresPlate.isEmpty() && triggers.stream().noneMatch(b -> b.getLocation().equals(tmpPresPlate.getLocation()))) {
                rewardPos = tmpPresPlate.getLocation();
                ((Player) ctx.getCommandSender()).spawnParticle(Particle.REDSTONE, tmpPresPlate.getLocation().add(0.5d, 0.2d, 0.5), 15, new Particle.DustOptions(Color.LIME, 1.0f));
                ctx.getCommandSender().sendMessage(Component.text(String.format("報酬チェストの設置場所を%sに設定しました。", rewardPos.toString()), NamedTextColor.GREEN));
                checkReady(ctx);
            } else {
                ctx.getCommandSender().sendMessage(Component.text("このブロックの上は何かがあるか、トリガの地点として設定されているため設定できません。", NamedTextColor.RED));
            }
        }
    }

    public void resetTriggerCmd(CommandContext ctx) {
        ctx.getCommandSender().sendMessage(Component.text("すべてのトリガを削除しました", NamedTextColor.GOLD));
        triggers.clear();
        state = MissionState.Setup;
        onStateChange();
        checkReady(ctx);
    }

    public void showTriggerCmd(CommandContext ctx) {
        if (ctx.getCommandSender() instanceof ConsoleCommandSender) {
            ctx.getCommandSender().sendMessage(Component.text("このコマンドはプレイヤーで実行してください", NamedTextColor.RED));
            return;
        }

        if (ctx.getCommandSender() instanceof Player) {
            triggers.forEach(b -> ((Player) ctx.getCommandSender()).spawnParticle(Particle.REDSTONE, b.getLocation().add(0.5d, 0.2d, 0.5), 5, new Particle.DustOptions(Color.LIME, 1.0f)));
            if (rewardPos != null) {
                ((Player) ctx.getCommandSender()).spawnParticle(Particle.REDSTONE, rewardPos.clone().add(0.5d, 0.2d, 0.5), 5, new Particle.DustOptions(Color.AQUA, 1.0f));
            }
            checkReady(ctx);
        }
    }

    public void startCmd(CommandContext ctx) {
        state = MissionState.OnGoing;
        onStateChange();
    }

    private void onStateChange() {
        getCommandManager().removeAll();
        switch (state) {
            case Setup -> {
                if (triggers.size() < triggerCount) getCommandManager().addCmd("addTrigger", this::addTriggerCmd);
                getCommandManager().addCmd("resetTrigger", this::resetTriggerCmd);
                getCommandManager().addCmd("setRewardPos", this::setRewardPosCmd);
                getCommandManager().addCmd("showTriggers", this::showTriggerCmd);
            }
            case Ready -> {
                getCommandManager().addCmd("resetTrigger", this::resetTriggerCmd);
                getCommandManager().addCmd("setRewardPos", this::setRewardPosCmd);
                getCommandManager().addCmd("showTriggers", this::showTriggerCmd);
                getCommandManager().addCmd("start", this::startCmd);
            }
            case OnGoing -> {
                getTimerManager().getTimerByName("mission.1.come_on").setVisibility(true);
                getTimerManager().getTimerByName("mission.1.reached").setVisibility(true);
                triggers.forEach(b -> b.setType(Material.STONE_PRESSURE_PLATE));
                greet();
            }
            case End -> {
                if (getTimerManager().getTimerByName("mission.1.come_on").isFinished()) {
                    // ミッション失敗
                    final var failTitle = Component.text("ミッション失敗", NamedTextColor.RED);
                    final var failSubTitle = Component.text("ミッションに失敗したため、逃走者全員に発光エフェクトが付与されました。", NamedTextColor.GOLD, TextDecoration.ITALIC);
                    var deBuff = new PotionEffect(PotionEffectType.GLOWING, Timer.TICKS_1_SEC * 10, 1);
                    challengers.stream().filter(p -> !p.getScoreboardTags().contains("ded")).forEach(p -> p.addPotionEffect(deBuff));
                    nonHunters.forEach(p -> {
                        p.showTitle(Title.title(failTitle, failSubTitle));
                        p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ELDER_GUARDIAN_CURSE, Sound.Source.HOSTILE, 1f, 1.1f));
                    });
                } else if (getTimerManager().getTimerByName("mission.1.reached").isFinished()) {
                    final var successTitle = Component.text("ミッション達成", NamedTextColor.GREEN);
                    final var successSubTitle = Component.text("ミッションを達成したため、報酬チェストが出現します。", NamedTextColor.GOLD, TextDecoration.ITALIC);

                    // 報酬チェスト召喚！
                    rewardPos.getBlock().setType(Material.CHEST);
                    Chest rewardBlock = (Chest) rewardPos.getBlock().getState();
                    rewardBlock.getBlockInventory().setItem(3, reward);
                    rewardBlock.getBlockInventory().setItem(5, reward);
                    rewardBlock.getBlockInventory().setItem(21, reward);
                    rewardBlock.getBlockInventory().setItem(23, reward);

                    nonHunters.forEach(p -> {
                        p.showTitle(Title.title(successTitle, successSubTitle));
                        p.playSound(Sound.sound(org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, Sound.Source.HOSTILE, 1f, 1.1f));
                    });
                }
                missionEnd();
            }
        }
    }

    public void onPressed(EventContext ctx) {
        var event = (PlayerInteractEvent) ctx.getEvent(EventEnum.ClickEvent);
        if (challengers.contains(event.getPlayer())) {
            if (event.getAction() == Action.PHYSICAL && Objects.nonNull(event.getClickedBlock()) && event.getClickedBlock().getType() == Material.STONE_PRESSURE_PLATE && triggers.stream().anyMatch(b -> b.getLocation().equals(event.getClickedBlock().getLocation()))) {
                checking.put(event.getClickedBlock(), event.getPlayer());
            }
        }
    }

    private void greet() {
        // ミッション開始通知
        final var startTitle = Component.text("ミッション開始: 時計台に集合", NamedTextColor.YELLOW);
        final var startSubTitle = Component.text("詳細はチャットを確認してください", NamedTextColor.GRAY, TextDecoration.ITALIC);

        nonHunters.forEach(p -> {
            p.showTitle(Title.title(startTitle, startSubTitle));
            p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Sound.Source.PLAYER, 1.0f, 1.0f));

            // チャットに詳細を表示
            p.sendMessage(Component.text("キッザニアにある時計台に四人で集合せよ！", NamedTextColor.YELLOW, TextDecoration.UNDERLINED, TextDecoration.BOLD));
            p.sendMessage(Component.text("時計台に設置された", NamedTextColor.YELLOW).append(Component.text("感圧版", NamedTextColor.GRAY)).append(Component.text("4つを", NamedTextColor.YELLOW)).append(Component.text("同時に", NamedTextColor.GOLD, TextDecoration.UNDERLINED)).append(Component.text("踏み、アイテムをゲットしよう。", NamedTextColor.YELLOW)));
            p.sendMessage(Component.text("制限時間は3分。", NamedTextColor.YELLOW));
            p.sendMessage(Component.text("ミッションが達成できなければ、", NamedTextColor.YELLOW).append(Component.text("逃走者全員が10秒間発光する", NamedTextColor.RED, TextDecoration.UNDERLINED)).append(Component.text("！", NamedTextColor.YELLOW)));
        });
    }

    public void checkReady(CommandContext ctx) {
        if (rewardPos != null && triggers.size() == triggerCount) {
            ctx.getCommandSender().sendMessage(Component.text("ミッションの設定が完了しました。ミッションを開始可能です。", NamedTextColor.GREEN));
            state = MissionState.Ready;
            onStateChange();
        }
        if (rewardPos == null) {
            ctx.getCommandSender().sendMessage(Component.text("ミッションを実行するには報酬チェストの出現地点を設定してください", NamedTextColor.RED));
        }
        if (triggers.size() != triggerCount) {
            ctx.getCommandSender().sendMessage(Component.text(String.format("ミッションを開始するにはトリガをあと%d箇所指定する必要があります。", triggerCount - triggers.size()), NamedTextColor.RED));
        } else onStateChange();
    }

    @Override
    public void Tick() {
        if (state != MissionState.OnGoing) {
            // 開始されていないか終了状態
            return;
        }

        // 感圧版の監視
        var released = new ArrayList<Block>();
        checking.forEach((b, p) -> {
            var delta = p.getLocation().subtract(b.getLocation().add(0.5d, 0, 0.5d));
            if (Math.abs(delta.x()) >= 0.8d - (1d / 16) || Math.abs(delta.z()) >= 0.8d - (1d / 16) || delta.y() >= 1d / 4 || delta.y() < 0) {
                var bd = (Powerable) b.getBlockData();
                bd.setPowered(false);
                b.setBlockData(bd);
                released.add(b);
            }
        });
        released.forEach(checking::remove);
        var t = (TaskProgressBar) getTimerManager().getTimerByName("mission.1.reached");
        t.setCurrent(checking.size());

        getTimerManager().getTimerByName("mission.1.come_on").tickTimer();

        if (getTimerManager().getTimerByName("mission.1.come_on").isFinished() || getTimerManager().getTimerByName("mission.1.reached").isFinished()) {
            state = MissionState.End;
            onStateChange();
        }
    }

    @Override
    public void onDisable() {
        // タイマー破棄
        getTimerManager().discardTimerByName("mission.1.come_on");
        getTimerManager().discardTimerByName("mission.1.reached");

        getEventManager().removeAll();

        // お掃除
        triggers.forEach(b -> b.setType(Material.AIR));
        triggers.clear();
        rewardPos = null;
        checking.clear();
    }

    public enum MissionState {Setup, Ready, OnGoing, End}

}
