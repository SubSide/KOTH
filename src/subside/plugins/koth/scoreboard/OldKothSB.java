package subside.plugins.koth.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

class OldKothSB {

    private String title;
    private Team[] teams;
    private Scoreboard scoreboard;
    private Objective obj;

    @SuppressWarnings("deprecation")
    public OldKothSB(String[] scores) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        obj = scoreboard.registerNewObjective("koth" + ((int) (Math.random() * 10000)) + "_board", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        teams = new Team[scores.length];
        for(int x = 0; x < teams.length; x++){
            teams[x] = scoreboard.registerNewTeam("team"+x);
            teams[x].addPlayer(Bukkit.getOfflinePlayer(ChatColor.values()[x].toString()));
            obj.getScore(Bukkit.getOfflinePlayer(ChatColor.values()[x].toString())).setScore(teams.length-x);
            
        }
    }
    

    public void setTitle(String ttl) {
        //ttl = ttl.substring(0, 16);
        if (this.title != null) if (this.title.equals(ttl)) return;
        
        this.title = ttl;
        obj.setDisplayName(title);

    }

    public void setScore(int x, String scr) {
        //scr = scr.substring(0, 14);
        teams[x].setPrefix(scr);

    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
