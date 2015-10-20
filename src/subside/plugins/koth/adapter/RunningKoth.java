package subside.plugins.koth.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.events.KothCapEvent;
import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.events.KothLeftEvent;
import subside.plugins.koth.utils.MessageBuilder;

public class RunningKoth {
    private @Getter Koth koth;
    private int captureTime;

    private @Getter String cappingPlayer;
    private String lootChest;
    private int timeCapped;
    private int lootAmount;
    private int timeKnocked;
    private boolean knocked;

    private @Getter int maxRunTime;
    private int timeRunning;

    public RunningKoth(Koth koth, int captureTime, int maxRunTime, int lootAmount, String lootChest) {
        this.koth = koth;
        this.captureTime = captureTime;
        this.timeCapped = 0;
        this.cappingPlayer = null;
        this.lootChest = lootChest;
        this.lootAmount = lootAmount;
        this.maxRunTime = maxRunTime * 60;
        koth.removeLootChest();
        koth.setLastWinner(null);
        new MessageBuilder(Lang.KOTH_PLAYING_STARTING).maxTime(maxRunTime).time(getTimeObject()).koth(koth).buildAndBroadcast();
    }
    
    public TimeObject getTimeObject(){
        return new TimeObject(captureTime, timeCapped);
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
            if (ConfigHandler.getCfgHandler().getKnockTime() > 0) {
                timeKnocked = 0;
                knocked = true;
            }
        } else {
            cappingPlayer = event.getNextCapper();
        }

    }

    @Deprecated
    public void update() {
        timeRunning++;
        if (!ConfigHandler.getCfgHandler().isUsePlayerMoveEvent()) {
            checkPlayerCapping();
        }
        if (knocked && timeKnocked < ConfigHandler.getCfgHandler().getKnockTime()) {
            timeKnocked++;
            return;
        } else if (knocked) {
            knocked = false;
        }

        if (cappingPlayer != null) {
            if (++timeCapped < captureTime) {
                if (timeCapped % 30 == 0) {
                    new MessageBuilder(Lang.KOTH_PLAYING_CAPTIME).maxTime(maxRunTime).time(getTimeObject()).player(cappingPlayer).koth(koth).shouldExcludePlayer().buildAndBroadcast();
                    if (Bukkit.getPlayer(cappingPlayer) != null) {
                        new MessageBuilder(Lang.KOTH_PLAYING_CAPTIME_CAPPER).maxTime(maxRunTime).time(getTimeObject()).player(cappingPlayer).koth(koth).buildAndSend(Bukkit.getPlayer(cappingPlayer));
                    }
                }
            } else {
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

                Bukkit.getScheduler().runTask(KothPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        KothHandler.stopKoth(getKoth().getName());
                    }
                });
            }
        } else {
            if (maxRunTime > 0 && timeRunning > maxRunTime) {
                new MessageBuilder(Lang.KOTH_PLAYING_TIME_UP).maxTime(maxRunTime).koth(koth).buildAndBroadcast();

                Bukkit.getScheduler().runTask(KothPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        KothHandler.stopKoth(getKoth().getName());
                    }
                });
                return;
            }

            List<Player> insideArea = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (koth.isInArea(player)) {
                    insideArea.add(player);
                }
            }
            if (insideArea.size() > 0) {
                String nextCappingPlayer = insideArea.get(new Random().nextInt(insideArea.size())).getName();

                KothCapEvent event = new KothCapEvent(koth, insideArea, nextCappingPlayer);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    cappingPlayer = event.getNextPlayerCapping();
                    new MessageBuilder(Lang.KOTH_PLAYING_PLAYERCAP).maxTime(maxRunTime).player(cappingPlayer).koth(koth).time(getTimeObject()).shouldExcludePlayer().buildAndBroadcast();
                    if (Bukkit.getPlayer(cappingPlayer) != null) {
                        new MessageBuilder(Lang.KOTH_PLAYING_PLAYERCAP_CAPPER).maxTime(maxRunTime).player(cappingPlayer).koth(koth).time(getTimeObject()).buildAndSend(Bukkit.getPlayer(cappingPlayer));
                    }
                }
            }

        }
    }

    public void quickEnd() {
        timeCapped = captureTime;
    }
}
