package subside.plugins.koth.adapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.KothLoader;
import subside.plugins.koth.events.KothStartEvent;
import subside.plugins.koth.exceptions.AreaAlreadyExistException;
import subside.plugins.koth.exceptions.AreaAlreadyRunningException;
import subside.plugins.koth.exceptions.AreaNotExistException;
import subside.plugins.koth.scheduler.ScheduleHandler;
import subside.plugins.koth.scoreboard.SBManager;

public class KothHandler {
    private static ArrayList<RunningKoth> runningKoths = new ArrayList<>();
    private static ArrayList<Area> availableAreas = new ArrayList<>();

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
            ScheduleHandler.tick();
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

    public static void startKoth(Area area, int time, int maxRunTime, int lootAmount, boolean isScheduled) throws AreaAlreadyRunningException {
        synchronized (runningKoths) {
            for (RunningKoth koth : runningKoths) {
                if (koth.getArea() == area) {
                    throw new AreaAlreadyRunningException(area.getName());
                }
            }
            KothStartEvent event = new KothStartEvent(area, time, maxRunTime, isScheduled);

            if (isScheduled && Bukkit.getOnlinePlayers().size() < ConfigHandler.getCfgHandler().getMinimumPlayersNeeded()) {
                event.setCancelled(true);
            }

            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                RunningKoth koth = new RunningKoth(area, event.getLength(), event.getMaxLength(), lootAmount);
                runningKoths.add(koth);
            }
        }

    }

    public static void startKoth(String area, int time, int maxRunTime, int lootAmount, boolean isScheduled) {
        if (area.equalsIgnoreCase("random")) {
            if (availableAreas.size() > 0) {
                startKoth(availableAreas.get(new Random().nextInt(availableAreas.size())), time, maxRunTime, lootAmount, isScheduled);
                return;
            }
        }

        for (Area ar : availableAreas) {
            if (ar.getName().equalsIgnoreCase(area)) {
                startKoth(ar, time, maxRunTime, lootAmount, isScheduled);
                return;
            }
        }
        throw new AreaNotExistException(area);
    }

    public static void createArea(String name, Location min, Location max) {
        if (getArea(name) == null && !name.equalsIgnoreCase("random")) {
            Area area = new Area(name, min, max);
            availableAreas.add(area);
            KothLoader.save();
        } else {
            throw new AreaAlreadyExistException(name);
        }
    }

    public static void removeArea(String name) {
        Area area = getArea(name);
        if (area == null) {
            throw new AreaNotExistException(name);
        }
        
        if (area.getInventory() != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (area.getInventory().getViewers().contains(player)) {
                    player.closeInventory();
                }
            }
        }

        availableAreas.remove(area);
        KothLoader.save();
    }

    public static Area getArea(String name) {
        for (Area area : availableAreas) {
            if (area.getName().equalsIgnoreCase(name)) {
                return area;
            }
        }
        return null;
    }

    public static void stopAllKoths() {
        synchronized (runningKoths) {
            Iterator<RunningKoth> it = runningKoths.iterator();
            while (it.hasNext()) {
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
                if (koth.getArea().getName().equalsIgnoreCase(name)) {
                    it.remove();
                    SBManager.getManager().clearAll();
                    return;
                }
            }
            throw new AreaNotExistException(name);
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
                if (koth.getArea().getName().equalsIgnoreCase(name)) {
                    koth.quickEnd();
                    return;
                }
            }
            throw new AreaNotExistException(name);
        }
    }

    public static List<Area> getAvailableAreas() {
        return availableAreas;
    }
}
