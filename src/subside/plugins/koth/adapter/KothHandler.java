package subside.plugins.koth.adapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.events.KothStartEvent;
import subside.plugins.koth.exceptions.KothAlreadyExistException;
import subside.plugins.koth.exceptions.KothAlreadyRunningException;
import subside.plugins.koth.exceptions.KothNotExistException;
import subside.plugins.koth.loaders.KothLoader;
import subside.plugins.koth.scheduler.Schedule;
import subside.plugins.koth.scheduler.ScheduleHandler;
import subside.plugins.koth.scoreboard.SBManager;

public class KothHandler {
    private static @Getter List<RunningKoth> runningKoths = new ArrayList<>();
    private static @Getter List<Koth> availableKoths = new ArrayList<>();
    private static @Getter List<Loot> loots = new ArrayList<>();

    @Deprecated
    public static void update() {
        synchronized (runningKoths) {
            Iterator<RunningKoth> it = runningKoths.iterator();
            while (it.hasNext()) {
                it.next().update();
            }
            if (ConfigHandler.getCfgHandler().isUseScoreboard()) {
                SBManager.getManager().update();
            }
            ScheduleHandler.getInstance().tick();
        }
    }

    @Deprecated
    public static void handleMoveEvent(Player player) {
        synchronized (runningKoths) {
            Iterator<RunningKoth> it = runningKoths.iterator();
            while (it.hasNext()) {
                RunningKoth koth = it.next();
                if (koth.getCappingPlayer() == null) continue;
                if (koth.getCappingPlayer().equalsIgnoreCase(player.getName())) {
                    koth.checkPlayerCapping();
                }
            }
        }
    }

    public static WeakReference<RunningKoth> getRunningKoth() {
        synchronized (runningKoths) {
            if (runningKoths.size() > 0) {
                return new WeakReference<RunningKoth>(runningKoths.get(0));
            } else {
                return new WeakReference<RunningKoth>(null);
            }
        }
    }
    
    public static void startKoth(Schedule schedule){
        startKoth(schedule.getKoth(), schedule.getCaptureTime()*60, schedule.getMaxRunTime(), schedule.getLootAmount(), schedule.getLootChest(), true);
    }

    public static void startKoth(Koth koth, int captureTime, int maxRunTime, int lootAmount, String lootChest, boolean isScheduled) throws KothAlreadyRunningException {
        synchronized (runningKoths) {
            for (RunningKoth rKoth : runningKoths) {
                if (rKoth.getKoth() == koth) {
                    throw new KothAlreadyRunningException(koth.getName());
                }
            }
            KothStartEvent event = new KothStartEvent(koth, captureTime, maxRunTime, isScheduled);

            if (isScheduled && Bukkit.getOnlinePlayers().size() < ConfigHandler.getCfgHandler().getMinimumPlayersNeeded()) {
                event.setCancelled(true);
            }

            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                RunningKoth rKoth = new RunningKoth(koth, event.getCaptureTime(), event.getMaxLength(), lootAmount, lootChest);
                runningKoths.add(rKoth);
            }
        }

    }

    public static void startKoth(String name, int captureTime, int maxRunTime, int lootAmount, String lootChest, boolean isScheduled) {
        String kth = name;
        if (kth.equalsIgnoreCase("random")) {
            if (availableKoths.size() > 0) {
                kth = availableKoths.get(new Random().nextInt(availableKoths.size())).getName();
            }
        }

        for (Koth koth : availableKoths) {
            if (koth.getName().equalsIgnoreCase(name)) {
                startKoth(koth, captureTime, maxRunTime, lootAmount, lootChest, isScheduled);
                return;
            }
        }
        throw new KothNotExistException(name);
    }

    public static void createKoth(String name, Location min, Location max) {
        if (getKoth(name) == null && !name.equalsIgnoreCase("random")) {
            Koth koth = new Koth(name);
            koth.getAreas().add(new Area(name, min, max));
            availableKoths.add(koth);
            KothLoader.save();
        } else {
            throw new KothAlreadyExistException(name);
        }
    }

    public static void removeKoth(String name) {
        Koth koth = getKoth(name);
        if (koth == null) {
            throw new KothNotExistException(name);
        }

        availableKoths.remove(koth);
        KothLoader.save();
    }
    
    public static Loot getLoot(String name){
        for(Loot loot : loots){
            if(loot.getName().equalsIgnoreCase(name)){
                return loot;
            }
        }
        return null;
    }

    public static Koth getKoth(String name) {
        for (Koth koth : availableKoths) {
            if (koth.getName().equalsIgnoreCase(name)) {
                return koth;
            }
        }
        return null;
    }

    public static void stopAllKoths() {
        synchronized (runningKoths) {
            Iterator<RunningKoth> it = runningKoths.iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        }

        SBManager.getManager().clearAll();
    }

    public static void stopKoth(String name) {
        synchronized (runningKoths) {
            Iterator<RunningKoth> it = runningKoths.iterator();
            while (it.hasNext()) {
                RunningKoth koth = it.next();
                if (koth.getKoth().getName().equalsIgnoreCase(name)) {
                    it.remove();
                    SBManager.getManager().clearAll();
                    return;
                }
            }
            throw new KothNotExistException(name);
        }
    }

    public static void endAllKoths() {
        synchronized (runningKoths) {
            Iterator<RunningKoth> it = runningKoths.iterator();
            while (it.hasNext()) {
                it.next().quickEnd();
            }
        }
    }

    public static void endKoth(String name) {
        synchronized (runningKoths) {
            Iterator<RunningKoth> it = runningKoths.iterator();
            while (it.hasNext()) {
                RunningKoth koth = it.next();
                if (koth.getKoth().getName().equalsIgnoreCase(name)) {
                    koth.quickEnd();
                    return;
                }
            }
            throw new KothNotExistException(name);
        }
    }
}
