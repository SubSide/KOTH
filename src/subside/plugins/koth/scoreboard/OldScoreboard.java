package subside.plugins.koth.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import subside.plugins.koth.adapter.RunningKoth;
import subside.plugins.koth.utils.MessageBuilder;

public class OldScoreboard extends AbstractScoreboard {

    private String titleLoader;
    private String[] textLoader;
    private RunningKoth rKoth;
    private SBObject sbObject;

    @Override
    public void load(RunningKoth rKoth, String titleLoader, String[] textLoader) {
        this.titleLoader = titleLoader;
        this.textLoader = textLoader;
        this.rKoth = rKoth;
        this.sbObject = new SBObject();
    }

    @Override
    public void updateScoreboard() {
        String[] text = textLoader.clone();

        sbObject.setTitle(rKoth.fillMessageBuilder(new MessageBuilder(titleLoader)).build()[0]);

        for (int x = 0; x < text.length; x++) {
            sbObject.setScore(x, rKoth.fillMessageBuilder(new MessageBuilder(text[x])).build()[0]);

        }
        
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (pl.getScoreboard() != sbObject.getScoreboard()) {
                pl.setScoreboard(sbObject.getScoreboard());
            }
        }
    }
    
    @Override
    public void destroy() {
        return;
    }


    @Override
    public void playerQuit(Player player) {
        sbObject.removePlayer(player);
    }
    
    class SBObject {
        private String title;
        private Team[] teams;
        private Scoreboard scoreboard;
        private Objective obj;

        
        public SBObject() {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            obj = scoreboard.registerNewObjective("koth" + ((int) (Math.random() * 10000)) + "_board", "dummy");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            teams = new Team[textLoader.length];
            for(int x = 0; x < teams.length; x++){
                teams[x] = scoreboard.registerNewTeam("team"+x);
                teams[x].addEntry(ChatColor.values()[x].toString());
                obj.getScore(ChatColor.values()[x].toString()).setScore(teams.length-x);
            }
        }
        

        public void setTitle(String ttl) {
            if (this.title != null) if (this.title.equals(ttl)) return;
            ttl = chop(ttl);
            
            this.title = ttl;
            obj.setDisplayName(title);

        }

        public void setScore(int x, String scr) {
            scr = chop(scr);
            teams[x].setPrefix(scr);

        }
        
        public void removePlayer(Player player){
            if(player.getScoreboard() == scoreboard) {
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
        }

        public Scoreboard getScoreboard() {
            return scoreboard;
        }
    }
}
