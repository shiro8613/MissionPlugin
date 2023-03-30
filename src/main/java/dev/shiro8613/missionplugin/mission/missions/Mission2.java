package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.mission.Mission;
import dev.shiro8613.missionplugin.event.EventEnum;
import dev.shiro8613.missionplugin.mission.Mission;
import dev.shiro8613.missionplugin.utils.timer.Timer;
import dev.shiro8613.missionplugin.utils.timer.TimerEnum;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import net.kyori.adventure.sound.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;

public class Mission2 extends Mission{

    private Player player = null;
    private List<Player> challengers = null;
    private final ItemStack reward = new ItemStack(Material.AIR);
    private final BossBar bar = Bukkit.createBossBar("残り時間:", BarColor.YELLOW, BarStyle.SOLID);
    private int bossBar;
    private int tickCounter;
    //private int sixMin = 180*2;
    private int sixMin = 500;
    private int nowPhase = 0;

    @Override
    public void Init() {
        player = getPlayers().get(0);
        challengers = getPlayers().stream().filter(p -> p.getScoreboard().getTeam("challenger") != null).toList();
        player.sendTitle("ミッション発動\n迷子のお知らせ","Subtitle",10,70,20);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP,1,1);
        bossBar = 10;
        //480

        greet();
        // 雑に作ったTimerを使ってみる
        getTimerManager().createTimer("mission.2.find_shiratama", TimerEnum.CountDown, 2*Timer.TICKS_1_MIN, BarColor.BLUE, BarStyle.SOLID);
        // 複数はsetSubscribersが便利です
        getTimerManager().getTimerByName("mission.2.find_shiratama").setSubscribers(challengers);
        getTimerManager().getTimerByName("mission.2.find_shiratama").setVisibility(true);

        tickCounter = sixMin;
        player.sendMessage(
                    "迷子のお知らせです。\n" +
                        "ピンクのスカートを着て、サングラス を掛けた、白玉のような女の子を探しています。" +
                        "もし見つけましたらその子と一緒 に写真を撮り、写真をdiscordの『#逃走中ミッション』チャンネルにお貼りください。\n" +
                        "5人の逃走者が写真を貼ることができればミッション達成です。\n" +
                        "全員に俊足の ポーションをお渡しします。\n" +
                        "もし制限時間内に達成できなければ、逃走者全員が10 秒間発光します。");
    }

    @Override
    public void Tick() {

        getTimerManager().getTimerByName("mission.2.find_shiratama").tickTimer();
        if (tickCounter == sixMin && bossBar >= 0) {
            //getJavaPlugin().getServer().sendMessage(Component.text(bossBar*0.1));
            bar.setProgress(bossBar--*0.1);
            bar.addPlayer(player);
            bar.setVisible(true);
            tickCounter = 0;
            if (bossBar == 0) {
                givePotion();
                bossBar = -1;
            }

        }
        else if (tickCounter < sixMin) {
            tickCounter++;
        }

        if (getTimerManager().getTimerByName("mission.2.find_shiratama").isFinished()) {
            givePotion();
            bossBar = -1;
        }

        if (bossBar == -1) {
            bar.removeAll();
            player.sendMessage("ミッションを終了します");
            // ここのメソッド動かない！！
            // そんな時代も414fdac919775820ce5dc2582d04c2b39dd34be1より前にはありました...
            missionEnd();
        }
    }

    private void greet() {
        // 報酬アイテム情報の設定
        reward.setType(Material.POTION);
        var pm = (PotionMeta) reward.getItemMeta();
        pm.setBasePotionData(new PotionData(PotionType.SPEED));
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
            p.sendMessage(Component.text("もし見つけましたらその子と一緒に写真を撮り、写真を",NamedTextColor.YELLOW).append(Component.text("Discordの『#逃走中ミッション』チャンネル", NamedTextColor.GOLD)).clickEvent(ClickEvent.openUrl("https://discord.com/channels/1046066805552189440/1080796750303997973")).append(Component.text("にお貼りください。", NamedTextColor.YELLOW)));
            p.sendMessage(Component.text("5人の逃走者が写真を貼ることができれば",NamedTextColor.YELLOW).append(Component.text("ミッション成功", NamedTextColor.GREEN)).append(Component.text("です。", NamedTextColor.YELLOW)));
            p.sendMessage(Component.text("全員に",NamedTextColor.YELLOW).append(reward.displayName().color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, true)).append(Component.text("をお渡しします。")));
            p.sendMessage(Component.text("もし制限時間内に達成できなければ、",NamedTextColor.YELLOW).append(Component.text("逃走者全員が10秒間発光します", NamedTextColor.RED, TextDecoration.UNDERLINED)).append(Component.text("。", NamedTextColor.YELLOW)));
        });
    }

    public void givePotion() {
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
        var deBuff = new PotionEffect(PotionEffectType.GLOWING, Timer.TICKS_1_SEC*10, 1);
        challengers.forEach(p -> p.addPotionEffect(deBuff));

        /*
        PlayerInventory inventory = player.getInventory(); // プレイヤーのインベントリ
        ItemStack potion = new ItemStack(Material.POTION, 1); // ポーション
        player.addPotionEffect ( new PotionEffect ( PotionEffectType.HEAL, 200, 1 ) );
         */
    }

    @Override
    public void onDisable() {
        bar.removeAll();
        getTimerManager().discardTimerByName("mission.2.find_shiratama");
    }
}
