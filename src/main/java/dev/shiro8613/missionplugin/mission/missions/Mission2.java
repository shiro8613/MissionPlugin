package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.command.CommandContext;
import dev.shiro8613.missionplugin.mission.Mission;
import dev.shiro8613.missionplugin.utils.timer.Timer;
import dev.shiro8613.missionplugin.utils.timer.TimerEnum;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import net.kyori.adventure.sound.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;

public class Mission2 extends Mission{

    private enum MissionState {Start, OnGoing, End}
    private List<Player> challengers = null;
    private final ItemStack reward = new ItemStack(Material.AIR);
    private MissionState state = MissionState.Start;
    private int timeLimit = 6*Timer.TICKS_1_MIN;
    private final int requiredChecks = 5;

    @Override
    public void Init() {
        challengers = getPlayers().stream().filter(p -> p.getScoreboard().getTeam("challenger") != null).toList();

        greet();
        // 雑に作ったTimerを使ってみる
        getTimerManager().createTimer("mission.2.find_shiratama", TimerEnum.CountDown, timeLimit, BarColor.BLUE, BarStyle.SOLID);
        getTimerManager().createTimer("mission.2.approved_player", TimerEnum.TaskProgress, requiredChecks, BarColor.GREEN, BarStyle.SEGMENTED_10);
        // 複数はsetSubscribersが便利です
        getTimerManager().getTimerByName("mission.2.find_shiratama").setSubscribers(challengers);
        getTimerManager().getTimerByName("mission.2.approved_player").setSubscribers(challengers);
        getTimerManager().getTimerByName("mission.2.find_shiratama").setVisibility(true);
        getTimerManager().getTimerByName("mission.2.approved_player").setVisibility(true);

        state = MissionState.OnGoing;
        getCommandManager().addCmd("chkMsg", this::checkMessageCmd);
    }

    @Override
    public void Tick() {

        getTimerManager().getTimerByName("mission.2.find_shiratama").tickTimer();

        if (getTimerManager().getTimerByName("mission.2.find_shiratama").isFinished()) {
            state = MissionState.End;
            onFail();
        }

        if (state == MissionState.End) {
            challengers.forEach(p ->p.sendMessage("ミッションを終了します"));
            // ここのメソッド動かない！！
            // そんな時代も414fdac919775820ce5dc2582d04c2b39dd34be1より前にはありました...
            missionEnd();
        }
    }

    private void checkMessageCmd(CommandContext ctx) {
        ctx.getCommandSender().sendMessage(Component.text("1プレイヤーの画像の確認が済んだものとしてマークします。", NamedTextColor.AQUA));
        getTimerManager().getTimerByName("mission.2.approved_player").tickTimer();
        if (getTimerManager().getTimerByName("mission.2.approved_player").isFinished()) {
            onSuccess();
        }
    }

    private void greet() {
        // 報酬アイテム情報の設定
        reward.setType(Material.POTION);
        var pm = (PotionMeta) reward.getItemMeta();
        pm.setBasePotionData(new PotionData(PotionType.WATER));
        pm.displayName(Component.translatable("item.minecraft.potion.effect.swiftness").color(NamedTextColor.AQUA));
        pm.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 10*Timer.TICKS_1_SEC,3, false, true, true), true);
        reward.setItemMeta(pm);

        // ミッション開始通知
        var title = Component.text("ミッション発動: 迷子の、、お知らせです。。。", NamedTextColor.YELLOW);
        var subtitle = Component.text("詳細はチャットを確認してください", NamedTextColor.GRAY, TextDecoration.ITALIC);
        challengers.forEach(p -> {
            p.showTitle(Title.title(title, subtitle));
            p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Sound.Source.PLAYER, 1.0f, 1.0f));

            // チャットに詳細を表示
            p.sendMessage(Component.text("迷子のお知らせです。",NamedTextColor.YELLOW, TextDecoration.UNDERLINED, TextDecoration.BOLD));
            p.sendMessage(Component.text("ピンクのスカート",NamedTextColor.LIGHT_PURPLE).append(Component.text("を着て、サングラスを掛けた、", NamedTextColor.YELLOW)).append(Component.text("白玉のような女の子", NamedTextColor.WHITE)).append(Component.text("を探しています。", NamedTextColor.YELLOW)));
            // TODO: 使用時はリンク先を正しいものに変更するかリンクを開く機能を削除してください。
            p.sendMessage(Component.text("もし見つけましたらその子と一緒に写真を撮り、写真を",NamedTextColor.YELLOW).append(Component.text("Discordの『#逃走中ミッション』チャンネル", NamedTextColor.GOLD)).clickEvent(ClickEvent.openUrl("https://discord.com/channels/1046066805552189440/1080796750303997973")).hoverEvent(Component.text("クリックするとWebブラウザで開きます")).append(Component.text("にお貼りください。", NamedTextColor.YELLOW)));
            p.sendMessage(Component.text("5人の逃走者が写真を貼ることができれば",NamedTextColor.YELLOW).append(Component.text("ミッション成功", NamedTextColor.GREEN)).append(Component.text("です。", NamedTextColor.YELLOW)));
            p.sendMessage(Component.text("全員に",NamedTextColor.YELLOW).append(reward.displayName().color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, true)).append(Component.text("をお渡しします。")));
            p.sendMessage(Component.text("もし制限時間内に達成できなければ、",NamedTextColor.YELLOW).append(Component.text("逃走者全員が10秒間発光します", NamedTextColor.RED, TextDecoration.UNDERLINED)).append(Component.text("。", NamedTextColor.YELLOW)));
        });
    }

    public void onSuccess() {
        state = MissionState.End;
        final var successTitle = Component.text("ミッション成功", NamedTextColor.GREEN);
        final var successSubTitle = Component.text("ミッションに成功したため、逃走者全員に報酬が配布されました。", NamedTextColor.GOLD, TextDecoration.ITALIC);
        challengers.forEach(p -> {
            p.getInventory().addItem(reward);
            p.showTitle(Title.title(successTitle, successSubTitle));
            p.playSound(Sound.sound(org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, Sound.Source.HOSTILE, 1f, 1.1f));
        });
    }
    public void onFail() {
/*
        // ゲームクリアの場合
        ItemStack potion = new ItemStack(Material.POTION, 1);

        PotionMeta potionmeta = (PotionMeta) potion.getItemMeta();
        potionmeta.setMainEffect(PotionEffectType.HEAL);

        PotionEffect heal = new PotionEffect(PotionEffectType.HEAL, 1000, 1);
        potionmeta.addCustomEffect(heal, true);
        potionmeta.setDisplayName("治癒のポーション");
        potion.setItemMeta(potionmeta);

        player.getInventory().addItem(potion);

*/
        // ゲームオーバーの場合
        final var failTitle = Component.text("ミッション失敗", NamedTextColor.RED);
        final var failSubTitle = Component.text("ミッションに失敗したため、逃走者全員に発光エフェクトが付与されました。", NamedTextColor.GOLD, TextDecoration.ITALIC);
        var deBuff = new PotionEffect(PotionEffectType.GLOWING, Timer.TICKS_1_SEC*10, 1);
        challengers.forEach(p -> {
            p.addPotionEffect(deBuff);
            p.showTitle(Title.title(failTitle, failSubTitle));
            p.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ELDER_GUARDIAN_CURSE, Sound.Source.HOSTILE, 1f, 1.1f));
        });

        /*
        PlayerInventory inventory = player.getInventory(); // プレイヤーのインベントリ
        ItemStack potion = new ItemStack(Material.POTION, 1); // ポーション
        player.addPotionEffect ( new PotionEffect ( PotionEffectType.HEAL, 200, 1 ) );
         */
    }

    @Override
    public void onDisable() {
        getTimerManager().discardTimerByName("mission.2.find_shiratama");
        getTimerManager().discardTimerByName("mission.2.approved_player");
        getCommandManager().removeAll();
    }
}
