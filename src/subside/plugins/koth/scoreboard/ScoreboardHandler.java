package subside.plugins.koth.scoreboard;

import java.lang.ref.WeakReference;

import subside.plugins.koth.MessageBuilder;
import subside.plugins.koth.area.Area;
import subside.plugins.koth.area.KothHandler;
import subside.plugins.koth.area.RunningKoth;

class ScoreboardHandler {

	private static String titleLoader;

	private static String[] textLoader;

	private static KothSB scoreboard;

	static void load(String titleLoader, String[] textLoader) {
		ScoreboardHandler.titleLoader = titleLoader;
		ScoreboardHandler.textLoader = textLoader;
		scoreboard = new KothSB();
	}

	static void updateScoreboard() {
		WeakReference<RunningKoth> koth = KothHandler.getRunningKoth();
		if (koth.get() == null) {
			return;
		}
		
		if(!scoreboard.isInitialized()){
		    scoreboard.init(new String[textLoader.length]);
		}
		
		String[] text = textLoader.clone();

		Area area = koth.get().getArea();
		String player = koth.get().getCappingPlayer();

		scoreboard.setTitle(new MessageBuilder(titleLoader).maxTime(koth.get().getMaxRunTime()).area(area.getName()).player(player).time(koth.get().getCaptureTime(), koth.get().getTimeCapped()).build());

		for (int x = 0; x < text.length; x++) {
			scoreboard.setScore(x, new MessageBuilder(text[x]).maxTime(koth.get().getMaxRunTime()).area(area.getName()).player(player).time(koth.get().getCaptureTime(), koth.get().getTimeCapped()).build());

		}
		/*
		for (Player pl : Bukkit.getOnlinePlayers()) {
			if (pl.getScoreboard() != scoreboard.getScoreboard()) {
				pl.setScoreboard(scoreboard.getScoreboard());
			}
		}*/

		return;
	}
	
	static void clearSB(){
	    if(scoreboard != null)
	        scoreboard.clearScoreboard();
	}
}
