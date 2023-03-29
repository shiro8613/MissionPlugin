package dev.shiro8613.missionplugin.mission.missions;

import dev.shiro8613.missionplugin.mission.Mission;
import dev.shiro8613.missionplugin.event.EventEnum;
import dev.shiro8613.missionplugin.mission.Mission;
import dev.shiro8613.missionplugin.utils.timer.Timer;
import dev.shiro8613.missionplugin.utils.timer.TimerEnum;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Mission2 extends Mission{

    private Player player = null;
    private final BossBar bar = Bukkit.createBossBar("残り時間:", BarColor.YELLOW, BarStyle.SOLID);
    private int bossBar;
    private int tickCounter;
    //private int sixMin = 180*2;
    private int sixMin = 500;
    private int nowPhase = 0;

    @Override
    public void Init() {
        player = getPlayers().get(0);
        player.sendTitle("ミッション発動\n迷子のお知らせ","Subtitle",10,70,20);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
        bossBar = 10;
        //480
        // 雑に作ったTimerを使ってみる
        getTimerManager().createTimer("mission.2.find_shiratama", TimerEnum.CountDown, 2*Timer.TICKS_1_MIN, BarColor.BLUE, BarStyle.SOLID);
        // 複数はsetの方をどうぞ
        getTimerManager().getTimerByName("mission.2.find_shiratama").addSubscriber(player);
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
            missionEnd();
        }
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
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1));

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
