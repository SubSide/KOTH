package subside.plugins.koth.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.events.KothCapEvent;
import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.events.KothLeftEvent;
import subside.plugins.koth.utils.MessageBuilder;

/**
 * @author Thomas "SubSide" van den Bulk
 */
public class KothClassic implements RunningKoth {
    private @Getter Koth koth;
    private int captureTime;

    private @Getter String cappingPlayer;
    private @Getter String lootChest;
    private int timeCapped;
    private int timeNotCapped;
    private int lootAmount;
    private int timeKnocked;
    private boolean knocked;

    private @Getter int maxRunTime;
    private int timeRunning;
    
    @Override
    public void init(StartParams params){
        this.koth = params.getKoth();
        this.captureTime = params.getCaptureTime();
        this.lootChest = params.getLootChest();
        this.lootAmount = params.getLootAmount();
        
        this.timeCapped = 0;
        this.timeNotCapped = 0;
        this.cappingPlayer = null;
        this.maxRunTime = maxRunTime * 60;
        koth.removeLootChest();
        koth.setLastWinner(null);
        new MessageBuilder(Lang.KOTH_PLAYING_STARTING).maxTime(maxRunTime).time(getTimeObject()).koth(koth).buildAndBroadcast();
    }

    /**
     * Get the TimeObject for the running KoTH
     * @return The TimeObject
     */
    public TimeObject getTimeObject() {
        return new TimeObject(captureTime, timeCapped);
    }

    @Deprecated
    public void checkPlayerCapping(Player player) {
        if (cappingPlayer == null) return;
        if (cappingPlayer.equalsIgnoreCase(player.getName())) {
            checkPlayerCapping();
        }
    }

    @Deprecated
    public void checkPlayerCapping() {
        if (cappingPlayer == null) {
            return;
        }

        boolean shouldClear = true;
        try {
            shouldClear = !koth.isInArea(Bukkit.getOfflinePlayer(cappingPlayer));
            if (!shouldClear) {
                if (Bukkit.getOfflinePlayer(cappingPlayer).isOnline()) {
                    if (((Player) Bukkit.getOfflinePlayer(cappingPlayer)).isDead()) {
                        shouldClear = true;
                    }
                }
            }
        }
        catch (Exception e) {}

        if (!shouldClear) {
            return;
        }

        KothLeftEvent event = new KothLeftEvent(koth, cappingPlayer, timeCapped);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        if (event.getNextCapper() == null) {
            new MessageBuilder(Lang.KOTH_PLAYING_LEFT).maxTime(maxRunTime).time(getTimeObject()).player(cappingPlayer).koth(koth).shouldExcludePlayer().buildAndBroadcast();
            if (Bukkit.getPlayer(cappingPlayer) != null) {
                new MessageBuilder(Lang.KOTH_PLAYING_LEFT_CAPPER).maxTime(maxRunTime).time(getTimeObject()).player(cappingPlayer).koth(koth).buildAndSend(Bukkit.getPlayer(cappingPlayer));
            }

            cappingPlayer = null;
            timeCapped = 0;
            if (ConfigHandler.getCfgHandler().getKoth().getKnockTime() > 0) {
                timeKnocked = 0;
                knocked = true;
            }
        } else {
            cappingPlayer = event.getNextCapper();
        }

    }

    public void endKoth(EndReason reason) {
        if (reason == EndReason.WON || reason == EndReason.GRACEFUL) {
            if (cappingPlayer != null) {
                new MessageBuilder(Lang.KOTH_PLAYING_WON).maxTime(maxRunTime).player(cappingPlayer).koth(koth).shouldExcludePlayer().buildAndBroadcast();
                if (Bukkit.getPlayer(cappingPlayer) != null) {
                    new MessageBuilder(Lang.KOTH_PLAYING_WON_CAPPER).maxTime(maxRunTime).player(cappingPlayer).koth(koth).buildAndSend(Bukkit.getPlayer(cappingPlayer));
                }

                KothEndEvent event = new KothEndEvent(koth, cappingPlayer);
                Bukkit.getServer().getPluginManager().callEvent(event);

                koth.setLastWinner(cappingPlayer);
                if (event.isCreatingChest()) {
                    Bukkit.getScheduler().runTask(KothPlugin.getPlugin(), new Runnable() {
                        public void run() {
                            koth.createLootChest(lootAmount, lootChest);
                        }
                    });
                }
            }
        } else if (reason == EndReason.TIMEUP) {
            new MessageBuilder(Lang.KOTH_PLAYING_TIME_UP).maxTime(maxRunTime).koth(koth).buildAndBroadcast();
        }

        
        
        final KothClassic thisObj = this;
        Bukkit.getScheduler().runTask(KothPlugin.getPlugin(), new Runnable() {
            @SuppressWarnings("deprecation")
            public void run() {
                KothHandler.getInstance().remove(thisObj);
            }
        });
    }

    @Deprecated
    public void update() {
        timeRunning++;
        timeNotCapped++;
        if (!ConfigHandler.getCfgHandler().getGlobal().isUsePlayerMoveEvent()) {
            checkPlayerCapping();
        }
        if (knocked && timeKnocked < ConfigHandler.getCfgHandler().getKoth().getKnockTime()) {
            timeKnocked++;
            return;
        } else if (knocked) {
            knocked = false;
        }

        if (cappingPlayer != null) {
            timeNotCapped = 0;
            if (++timeCapped < captureTime) {
                if (timeCapped % 30 == 0) {
                    new MessageBuilder(Lang.KOTH_PLAYING_CAPTIME).maxTime(maxRunTime).time(getTimeObject()).player(cappingPlayer).koth(koth).shouldExcludePlayer().buildAndBroadcast();
                    if (Bukkit.getPlayer(cappingPlayer) != null) {
                        new MessageBuilder(Lang.KOTH_PLAYING_CAPTIME_CAPPER).maxTime(maxRunTime).time(getTimeObject()).player(cappingPlayer).koth(koth).buildAndSend(Bukkit.getPlayer(cappingPlayer));
                    }
                }
            } else {
                endKoth(EndReason.WON);
            }
            return;
        }

        if (timeNotCapped % 30 == 0) {
            new MessageBuilder(Lang.KOTH_PLAYING_NOT_CAPPING).maxTime(maxRunTime).time(getTimeObject()).koth(koth).buildAndBroadcast();
        }

        if (maxRunTime > 0 && timeRunning > maxRunTime) {
            endKoth(EndReason.TIMEUP);
        }

        List<Player> insideArea = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (koth.isInArea(player)) {
                insideArea.add(player);
            }
        }
        if (insideArea.size() < 1) {
            return;
        }

        String nextCappingPlayer = insideArea.get(new Random().nextInt(insideArea.size())).getName();

        KothCapEvent event = new KothCapEvent(koth, insideArea, nextCappingPlayer);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        cappingPlayer = event.getNextPlayerCapping();
        new MessageBuilder(Lang.KOTH_PLAYING_PLAYERCAP).maxTime(maxRunTime).player(cappingPlayer).koth(koth).time(getTimeObject()).shouldExcludePlayer().buildAndBroadcast();
        if (Bukkit.getPlayer(cappingPlayer) != null) {
            new MessageBuilder(Lang.KOTH_PLAYING_PLAYERCAP_CAPPER).maxTime(maxRunTime).player(cappingPlayer).koth(koth).time(getTimeObject()).buildAndSend(Bukkit.getPlayer(cappingPlayer));
        }

    }
}
