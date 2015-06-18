package subside.plugins.koth.scoreboard;

import java.lang.ref.WeakReference;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import subside.plugins.koth.MessageBuilder;
import subside.plugins.koth.area.Area;
import subside.plugins.koth.area.KothHandler;
import subside.plugins.koth.area.RunningKoth;

public class ScoreboardHandler {

	private static String titleLoader;

	private static String[] textLoader;

	private static KothSB scoreboard;

	public static void load(String titleLoader, String[] textLoader) {
		ScoreboardHandler.titleLoader = titleLoader;
		ScoreboardHandler.textLoader = textLoader;
		scoreboard = new KothSB(new String[textLoader.length]);
	}

	public static void updateScoreboard() {
		WeakReference<RunningKoth> koth = KothHandler.getRunningKoth();
		if (koth.get() == null) {
			return;
		}
		String[] text = textLoader.clone();

		Area area = koth.get().getArea();
		String player = koth.get().getCappingPlayer();

		scoreboard.setTitle(new MessageBuilder(titleLoader).area(area.getName()).player(player).time(koth.get().getCaptureTime(), koth.get().getTimeCapped()).build());

		for (int x = 0; x < text.length; x++) {
			scoreboard.setScore(x, new MessageBuilder(text[x]).area(area.getName()).player(player).time(koth.get().getCaptureTime(), koth.get().getTimeCapped()).build());

		}

		for (Player pl : Bukkit.getOnlinePlayers()) {
			if (pl.getScoreboard() != scoreboard.getScoreboard()) {
				pl.setScoreboard(scoreboard.getScoreboard());
			}
		}

		return;
	}

	public static void clearAll() {
		for (Player pl : Bukkit.getOnlinePlayers()) {
			clearPlayer(pl);
		}
	}

	public static void clearPlayer(Player pl) {
		try {
			if (pl.getScoreboard() == scoreboard.getScoreboard()) {
				pl.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}
		} catch(Exception e){
			
		}
	}
}
