package subside.plugins.koth.scoreboard;

import java.lang.ref.WeakReference;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import subside.plugins.koth.adapter.Koth;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.adapter.RunningKoth;
import subside.plugins.koth.utils.MessageBuilder;

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
        WeakReference<RunningKoth> wKoth = KothHandler.getRunningKoth();
        if (wKoth.get() == null) {
            return;
        }
        
        String[] text = textLoader.clone();

        RunningKoth rKoth = wKoth.get();
        Koth koth = rKoth.getKoth();
        
        String player = wKoth.get().getCappingPlayer();

        scoreboard.setTitle(new MessageBuilder(titleLoader).maxTime(rKoth.getMaxRunTime()).koth(koth.getName()).player(player).time(rKoth.getTimeObject()).build()[0]);

        for (int x = 0; x < text.length; x++) {
            scoreboard.setScore(x, new MessageBuilder(text[x]).maxTime(rKoth.getMaxRunTime()).koth(koth.getName()).player(player).time(rKoth.getTimeObject()).build()[0]);

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
