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

	@SuppressWarnings("deprecation")
	public static void updateScoreboard() {
		WeakReference<RunningKoth> koth = KothHandler.getRunningKoth();
		if (koth == null) {
			return;
		}
		String[] text = textLoader.clone();

		Area area = koth.get().getArea();
		String player = koth.get().getCappingPlayer();
		int secs = koth.get().getTimeCapped() % 60;
		int mins = koth.get().getTimeCapped() / 60;
		int secs_left = (koth.get().getCaptureTime() - koth.get().getTimeCapped()) % 60;
		int mins_left = (koth.get().getCaptureTime() - koth.get().getTimeCapped()) / 60;

		int posX = Math.round((area.getMin().getBlockX() + area.getMax().getBlockX()) / 2);
		int posY = Math.round((area.getMin().getBlockY() + area.getMax().getBlockY()) / 2);
		int posZ = Math.round((area.getMin().getBlockZ() + area.getMax().getBlockZ()) / 2);

		scoreboard.setTitle(new MessageBuilder(titleLoader).area(area.getName()).player(player).seconds(secs).minutes(mins).secondsLeft(secs_left).minutesLeft(mins_left).x(posX).y(posY).z(posZ).build());

		for (int x = 0; x < text.length; x++) {
			scoreboard.setScore(x, new MessageBuilder(text[x]).area(area.getName()).player(player).seconds(secs).minutes(mins).secondsLeft(secs_left).minutesLeft(mins_left).x(posX).y(posY).z(posZ).build());

		}

		for (Player pl : Bukkit.getOnlinePlayers()) {
			if (pl.getScoreboard() != scoreboard.getScoreboard()) {
				pl.setScoreboard(scoreboard.getScoreboard());
			}
		}

		return;
	}

	@SuppressWarnings("deprecation")
	public static void clearAll() {
		for (Player pl : Bukkit.getOnlinePlayers()) {
			if (pl.getScoreboard() == null) continue;
			if (pl.getScoreboard() == scoreboard.getScoreboard()) {
				pl.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}
		}
	}

	public static void clearPlayer(Player pl) {
		if (pl.getScoreboard() == scoreboard.getScoreboard()) {
			pl.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
	}
}
