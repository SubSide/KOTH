package subside.plugins.koth.area;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.Koth;
import subside.plugins.koth.KothLoader;
import subside.plugins.koth.exceptions.AreaAlreadyExistException;
import subside.plugins.koth.exceptions.AreaAlreadyRunningException;
import subside.plugins.koth.exceptions.AreaNotExistException;
import subside.plugins.koth.scheduler.ScheduleHandler;
import subside.plugins.koth.scoreboard.ScoreboardHandler;

public class KothHandler {
	private static ArrayList<RunningKoth> runningKoths = new ArrayList<RunningKoth>();
	private static ArrayList<Area> availableAreas = new ArrayList<Area>();

	public static void update() {
		synchronized (runningKoths) {
			Iterator<RunningKoth> it = runningKoths.iterator();
			while (it.hasNext()) {
				it.next().update();
			}
			if (ConfigHandler.getCfgHandler().getUseScoreboard()) {
				ScoreboardHandler.updateScoreboard();
			}
			ScheduleHandler.tick();
		}
	}

	public static void handleMoveEvent(Player player) {
		synchronized (runningKoths) {
			Iterator<RunningKoth> it = runningKoths.iterator();
			while (it.hasNext()) {
				RunningKoth koth = it.next();
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
				return null;
			}
		}
	}

	public static void startKoth(Area area) throws AreaAlreadyRunningException {
		startKoth(area, 15 * 60);
	}

	public static void startKoth(Area area, int time) throws AreaAlreadyRunningException {
		synchronized (runningKoths) {
			for (RunningKoth koth : runningKoths) {
				if (koth.getArea() == area) {
					throw new AreaAlreadyRunningException(area.getName());
				}
			}
			runningKoths.add(new RunningKoth(area, time));
		}

	}

	public static void startKoth(String area) {
		startKoth(area, 15 * 60);
	}

	public static void startKoth(String area, int time) {
		for (Area ar : availableAreas) {
			if (ar.getName().equalsIgnoreCase(area)) {
				startKoth(ar, time);
				return;
			}
		}
		throw new AreaNotExistException(area);
	}

	public static void createArea(String name, Location min, Location max) {
		if (getArea(name) == null) {
			Area area = new Area(name, min, max);
			availableAreas.add(area);
			KothLoader.save();
		} else {
			throw new AreaAlreadyExistException(name);
		}
	}
	
	public static void removeArea(String name) {
		Area area = getArea(name);
		if (area != null) {
			if(area.getInventory() != null){
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (area.getInventory().getViewers().contains(player)) {
						player.closeInventory();
					}
				}
			}

			availableAreas.remove(area);
			KothLoader.save();
		} else {
			throw new AreaNotExistException(name);
		}
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
				it.next();
				it.remove();
			}
		}

		Bukkit.getScheduler().runTask(Koth.getPlugin(), new Runnable() {
			public void run() {
				ScoreboardHandler.clearAll();
			}
		});
	}

	public static void stopKoth(String name) {
		synchronized (runningKoths) {
			Iterator<RunningKoth> it = runningKoths.iterator();
			while (it.hasNext()) {
				RunningKoth koth = it.next();
				if (koth.getArea().getName().equalsIgnoreCase(name)) {
					runningKoths.remove(koth);
					Bukkit.getScheduler().runTask(Koth.getPlugin(), new Runnable() {
						public void run() {
							ScoreboardHandler.clearAll();
						}
					});
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

	public static ArrayList<Area> getAvailableAreas() {
		return availableAreas;
	}
}
