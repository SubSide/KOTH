package subside.plugins.koth.scoreboard;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class KothSB {

	private String title;
	private Team[] teams;
	private Scoreboard scoreboard;
	private Objective obj;
	private @Getter boolean isInitialized;

	public KothSB() {
	    scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
	}
	
	@SuppressWarnings("deprecation")
    public void init(String[] scores){
	    if(isInitialized) return;
	    
	    obj = scoreboard.getObjective("KoTH_Scoreboard");
	    if(obj == null){
	        obj = scoreboard.registerNewObjective("KoTH_Scoreboard", "dummy");
	    }
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        teams = new Team[scores.length];
        for(int x = 0; x < teams.length; x++){
            teams[x] = scoreboard.getTeam("Kteam"+x);
            if(teams[x] == null){
                teams[x] = scoreboard.registerNewTeam("Kteam"+x);
            }
            teams[x].addPlayer(Bukkit.getOfflinePlayer(ChatColor.values()[x].toString()));
            obj.getScore(Bukkit.getOfflinePlayer(ChatColor.values()[x].toString())).setScore(teams.length-x);
            
        }
        isInitialized = true;
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
	
    @SuppressWarnings("deprecation")
    public void clearScoreboard(){
        if(!isInitialized) return;
	    //Set<Team> teams = scoreboard.getTeams();
	    for(Team team : teams){
	        for(OfflinePlayer player : team.getPlayers()){
	            team.removePlayer(player);
	            scoreboard.resetScores(player);
	        }
	        team.unregister();
	    }
	    isInitialized = false;
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}
}
