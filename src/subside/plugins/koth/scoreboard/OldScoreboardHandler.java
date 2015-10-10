package subside.plugins.koth.scoreboard;

import java.lang.ref.WeakReference;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import subside.plugins.koth.MessageBuilder;
import subside.plugins.koth.adapter.Area;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.adapter.RunningKoth;

class OldScoreboardHandler {

    private static String titleLoader;

    private static String[] textLoader;

    private static OldKothSB scoreboard;

    static void load(String titleLoader, String[] textLoader) {
        OldScoreboardHandler.titleLoader = titleLoader;
        OldScoreboardHandler.textLoader = textLoader;
        scoreboard = new OldKothSB(new String[textLoader.length]);
    }

    static void updateScoreboard() {
        WeakReference<RunningKoth> koth = KothHandler.getRunningKoth();
        if (koth.get() == null) {
            return;
        }
        
        String[] text = textLoader.clone();

        Area area = koth.get().getArea();
        String player = koth.get().getCappingPlayer();

        scoreboard.setTitle(new MessageBuilder(titleLoader).maxTime(koth.get().getMaxRunTime()).area(area.getName()).player(player).time(koth.get().getTimeObject()).build());

        for (int x = 0; x < text.length; x++) {
            scoreboard.setScore(x, new MessageBuilder(text[x]).maxTime(koth.get().getMaxRunTime()).area(area.getName()).player(player).time(koth.get().getTimeObject()).build());

        }
        
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (pl.getScoreboard() != scoreboard.getScoreboard()) {
                pl.setScoreboard(scoreboard.getScoreboard());
            }
        }

        return;
    }
    
    static void clearAll() {
        for (Player pl : Bukkit.getOnlinePlayers()){
            clearPlayer(pl);
        }
    }
    
    static void clearPlayer(Player pl){
        try {
            if(pl.getScoreboard() == scoreboard.getScoreboard()) {
                pl.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
        } catch(Exception e){
            
        }
    }
}
