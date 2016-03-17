package subside.plugins.koth.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

import lombok.Getter;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.utils.MessageBuilder;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothConquest implements RunningKoth {
    private @Getter Koth koth;
    private @Getter String lootChest;

    private @Getter int maxRunTime;
    private @Getter int maxScore;
    
    private @Getter List<ConquestArea> areas;
    private @Getter List<FactionScore> fScores;


    @Override
    public void init(StartParams params) {
        // TODO
        this.koth = params.getKoth();
        
        areas = new ArrayList<>();
        fScores = new ArrayList<>();
        
        for(Area area : koth.getAreas()){
            areas.add(new ConquestArea(area));
        }
    }

    /** Get the TimeObject for the running KoTH
     * 
     * @return          The TimeObject
     */
    public TimeObject getTimeObject(){
        // TODO
        return null;
    }
    
    @Deprecated
    public void update() {
        for(ConquestArea cArea : areas){
            cArea.update();
        }
        
        // TODO
    }

    @Override
    public void endKoth(EndReason reason) {
        if (reason == EndReason.WON || reason == EndReason.GRACEFUL) {
            // TODO
        } else if (reason == EndReason.TIMEUP) {
            // TODO
        }

        
        
        final RunningKoth thisObj = this;
        Bukkit.getScheduler().runTask(KothPlugin.getPlugin(), new Runnable() {
            @SuppressWarnings("deprecation")
            public void run() {
                KothHandler.getInstance().remove(thisObj);
            }
        });
    }
    
    
    public FactionScore getFactionScore(Faction faction){
        for(FactionScore score : fScores){
            if(score.getFaction() == faction){
                return score;
            }
        }
        FactionScore fScore = new FactionScore(faction);
        fScores.add(fScore);
        
        return fScore;
    }
    
    
    
    public class ConquestArea {
        private @Getter Area area;
        private @Getter FactionScore fScore;
        ConquestArea(Area area){
            this.area = area;
        }
        
        @Deprecated
        public void update(){
            if(fScore != null){
                boolean isInArea = false;
                for(Player fPlayer : fScore.getFaction().getOnlinePlayers()){
                    if(area.isInArea(fPlayer)){
                        isInArea = true;
                        break;
                    }
                }
                
                if(!isInArea){
                    fScore = null;
                    return;
                }
                
                fScore.addPoint();
                
                
            } else {
                List<Player> insideArea = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Faction f = FPlayers.getInstance().getByPlayer(player).getFaction();
                    if(f.isSafeZone() || f.isWarZone() || f.isWilderness()){
                        return;
                    }
                    if (area.isInArea(player)) {
                        insideArea.add(player);
                    }
                }
                if (insideArea.size() < 1) {
                    return;
                }
                
                fScore = getFactionScore(FPlayers.getInstance().getByPlayer(insideArea.get(new Random().nextInt(insideArea.size()))).getFaction());
            }
        }
    }
    
    public class FactionScore {
        private @Getter Faction faction;
        private @Getter int score;
        FactionScore(Faction faction){
            this.faction = faction;
            this.score = 0;
        }
        
        public void addPoint(){
            score++;
            if(score >= maxScore){
                // TODO
            }
        }
    }
    
    public MessageBuilder fillMessageBuilder(MessageBuilder mB){
        return mB.maxTime(maxRunTime).time(getTimeObject()).koth(koth);
    }
}
