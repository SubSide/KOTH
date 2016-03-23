package subside.plugins.koth.adapter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.adapter.captypes.CappingFaction;
import subside.plugins.koth.utils.MessageBuilder;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothConquest implements RunningKoth {
    private @Getter Koth koth;
    private @Getter String lootChest;

    private @Getter int maxRunTime;
    private @Getter int maxScore = 100; // TODO
    
    private @Getter List<ConquestArea> areas;
    private @Getter List<FactionScore> fScores;


    @Override
    public void init(StartParams params) {
        // TODO
        this.koth = params.getKoth();
        
        areas = new ArrayList<>();
        fScores = new ArrayList<>();
        
        for(Area area : koth.getAreas()){
            areas.add(new ConquestArea(this, area));
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
        System.out.println();
        for(FactionScore fScore : fScores){
        	System.out.println(fScore.getFaction().getName()+" "+fScore.getScore());
        }
        System.out.println();
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
    
    
    public FactionScore getFactionScore(CappingFaction faction){
        for(FactionScore score : fScores){
            if(score.getFaction().getObject().equals(faction.getObject())){
                return score;
            }
        }
        FactionScore fScore = new FactionScore(faction);
        fScores.add(fScore);
        
        return fScore;
    }
    
    
    
    public class ConquestArea {
        private @Getter Area area;
        private @Getter CapInfo capInfo;
        ConquestArea(KothConquest kC, Area area){
            this.area = area;
            this.capInfo = new CapInfo(kC, area, true, false);
        }
        
        @Deprecated
        public void update(){
            	capInfo.update();
            	if(capInfo.getCapper() != null){
            		getFactionScore((CappingFaction)capInfo.getCapper()).addPoint();
            	} else {
        			List<Player> insideArea = new ArrayList<>();
            		for (Player player : Bukkit.getOnlinePlayers()) {
                        if (area.isInArea(player)) {
                            insideArea.add(player);
                        }
                    }
                    if (insideArea.size() < 1) {
                        return;
                    }
                    capInfo.setCapper(capInfo.getRandomCapper(insideArea));
            	}
        }
    }
    
    public class FactionScore {
        private @Getter CappingFaction faction;
        private @Getter int score;
        FactionScore(CappingFaction faction){
            this.faction = faction;
            this.score = 0;
        }
        
        public void addPoint(){
            score++;
            if(score >= maxScore){
                System.out.println("GG");
            }
        }
    }
    
    public MessageBuilder fillMessageBuilder(MessageBuilder mB){
    	// TODO
        return mB.maxTime(maxRunTime).koth(koth);
    }
}
