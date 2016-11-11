package subside.plugins.koth.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import lombok.Getter;
import subside.plugins.koth.adapter.KothConquest;
import subside.plugins.koth.adapter.RunningKoth;
import subside.plugins.koth.utils.MessageBuilder;

public class ConquestScoreboard extends AbstractScoreboard {
    protected String titleLoader;
    protected RunningKoth rKoth;
    private @Getter SBObject sbObject;

    @Override
    public void load(RunningKoth koth, String titleLoader, String[] textLoader) {
        this.rKoth = koth;
        this.titleLoader = titleLoader;
        this.sbObject = new SBObject();
    }

    @Override
    public void updateScoreboard() {
        if (!(this.rKoth instanceof KothConquest)) {
            return;
        }
        sbObject.setTitle(rKoth.fillMessageBuilder(new MessageBuilder(titleLoader)).build()[0]);

    }

    @Override
    public void playerQuit(Player player) {
        return;
    }

    @Override
    public void destroy() {
        sbObject.clearScoreboard();
    }

    public class SBObject {
        private String title;
        private Scoreboard scoreboard;
        private Objective obj;

        SBObject() {
            scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            obj = scoreboard.getObjective("KoTH_Scoreboard");
            if (obj == null) {
                obj = scoreboard.registerNewObjective("KoTH_Scoreboard", "dummy");
            }
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        public void setTitle(String ttl) {
            if (this.title != null) if (this.title.equals(ttl)) return;
            ttl = chop(ttl);

            this.title = ttl;
            obj.setDisplayName(title);

        }
        
        public void setFactionScore(String faction, int score){
            if(scoreboard.getTeam(faction) == null){
                scoreboard.registerNewTeam(faction);
            }
            scoreboard.getTeam(faction).addEntry(faction);
            obj.getScore(faction).setScore(score);
        }


        public void clearScoreboard(){
            for(Team team : scoreboard.getTeams()){
                for(String entry : team.getEntries()){
                    team.removeEntry(entry);
                    scoreboard.resetScores(entry);
                }
                team.unregister();
            }
        }

        public Scoreboard getScoreboard() {
            return scoreboard;
        }
    }

}
