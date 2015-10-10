package subside.plugins.koth.adapter;

import java.util.ArrayList;
import java.util.Random;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.MessageBuilder;
import subside.plugins.koth.events.KothCapEvent;
import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.events.KothLeftEvent;

public class RunningKoth {
    private @Getter Area area;
    private int captureTime;

    private @Getter String cappingPlayer;
    private int timeCapped;
    private int lootAmount;
    private int timeKnocked;
    private boolean knocked;

    private @Getter int maxRunTime;
    private int timeRunning;

    public RunningKoth(Area area, int time, int maxRunTime, int lootAmount) {
        this.area = area;
        this.captureTime = time;
        this.timeCapped = 0;
        this.cappingPlayer = null;
        this.lootAmount = lootAmount;
        this.maxRunTime = maxRunTime * 60;
        area.removeLootChest();
        area.setLastWinner(null);
        new MessageBuilder(Lang.KOTH_STARTING).maxTime(maxRunTime).time(getTimeObject()).area(area.getName()).buildAndBroadcast();
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
            shouldClear = !area.isInArea(Bukkit.getOfflinePlayer(cappingPlayer));
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

        KothLeftEvent event = new KothLeftEvent(area, cappingPlayer, timeCapped);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        
        if (event.getNextCapper() == null) {
            new MessageBuilder(Lang.KOTH_LEFT).maxTime(maxRunTime).time(getTimeObject()).player(cappingPlayer).area(area.getName()).shouldExcludePlayer().buildAndBroadcast();
            if (Bukkit.getPlayer(cappingPlayer) != null) {
                new MessageBuilder(Lang.KOTH_LEFT_CAPPER).maxTime(maxRunTime).time(getTimeObject()).player(cappingPlayer).area(area.getName()).buildAndSend(Bukkit.getPlayer(cappingPlayer));
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
                    new MessageBuilder(Lang.KOTH_CAPTIME).maxTime(maxRunTime).time(getTimeObject()).player(cappingPlayer).area(area.getName()).shouldExcludePlayer().buildAndBroadcast();
                    if (Bukkit.getPlayer(cappingPlayer) != null) {
                        new MessageBuilder(Lang.KOTH_CAPTIME_CAPPER).maxTime(maxRunTime).time(getTimeObject()).player(cappingPlayer).area(area.getName()).buildAndSend(Bukkit.getPlayer(cappingPlayer));
                    }
                }
            } else {
                new MessageBuilder(Lang.KOTH_WON).maxTime(maxRunTime).player(cappingPlayer).area(area.getName()).shouldExcludePlayer().buildAndBroadcast();
                if (Bukkit.getPlayer(cappingPlayer) != null) {
                    new MessageBuilder(Lang.KOTH_WON_CAPPER).maxTime(maxRunTime).player(cappingPlayer).area(area.getName()).buildAndSend(Bukkit.getPlayer(cappingPlayer));
                }

                KothEndEvent event = new KothEndEvent(area, cappingPlayer);
                Bukkit.getServer().getPluginManager().callEvent(event);

                area.setLastWinner(cappingPlayer);
                if (event.isCreatingChest()) {
                    Bukkit.getScheduler().runTask(KothPlugin.getPlugin(), new Runnable() {
                        public void run() {
                            area.createLootChest(lootAmount);
                        }
                    });
                }

                Bukkit.getScheduler().runTask(KothPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        KothHandler.stopKoth(getArea().getName());
                    }
                });
            }
        } else {
            if (maxRunTime > 0 && timeRunning > maxRunTime) {
                new MessageBuilder(Lang.KOTH_TIME_UP).maxTime(maxRunTime).area(area.getName()).buildAndBroadcast();

                Bukkit.getScheduler().runTask(KothPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        KothHandler.stopKoth(getArea().getName());
                    }
                });
                return;
            }

            ArrayList<Player> insideArea = new ArrayList<Player>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (area.isInArea(player)) {
                    insideArea.add(player);
                }
            }
            if (insideArea.size() > 0) {
                String nextCappingPlayer = insideArea.get(new Random().nextInt(insideArea.size())).getName();

                KothCapEvent event = new KothCapEvent(area, insideArea, nextCappingPlayer);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    cappingPlayer = event.getNextPlayerCapping();
                    new MessageBuilder(Lang.KOTH_PLAYERCAP).maxTime(maxRunTime).player(cappingPlayer).area(area.getName()).time(getTimeObject()).shouldExcludePlayer().buildAndBroadcast();
                    if (Bukkit.getPlayer(cappingPlayer) != null) {
                        new MessageBuilder(Lang.KOTH_PLAYERCAP_CAPPER).maxTime(maxRunTime).player(cappingPlayer).area(area.getName()).time(getTimeObject()).buildAndSend(Bukkit.getPlayer(cappingPlayer));
                    }
                }
            }

        }
    }

    public void quickEnd() {
        timeCapped = captureTime;
    }
}
