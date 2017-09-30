package subside.plugins.koth.gamemodes;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import subside.plugins.koth.areas.Area;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.captureentities.CapInfo;
import subside.plugins.koth.captureentities.Capper;
import subside.plugins.koth.captureentities.CappingGroup;
import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.MessageBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothConquest extends RunningKoth {
    private @Getter Koth koth;
    private @Getter String lootChest;
    private @Getter int lootAmount;

    private @Getter int maxRunTime;
    private @Getter int maxPoints = 100; // TODO
    
    private @Getter List<ConquestArea> areas;
    private @Getter List<FactionScore> fScores;
    private int runTime;

    public KothConquest(GamemodeRegistry gamemodeRegistry){
        super(gamemodeRegistry);
    }
    
    @Override
    public void init(StartParams params) {
        this.koth = params.getKoth();
        this.lootAmount = params.getLootAmount();
        
        areas = new ArrayList<>();
        fScores = new ArrayList<>();
        
        for(Area area : koth.getAreas()){
            areas.add(new ConquestArea(this, area));
        }
    }

    @Override
    public Capper<?> getCapper(){
        if(fScores.size() > 0){
            Arrays.sort(fScores.toArray());
            return fScores.get(fScores.size()-1).getFaction();
        }
        return null;
        
    }

    /** Get the TimeObject for the running KoTH
     * 
     * @return          The TimeObject
     */
    public TimeObject getTimeObject(){
        // TODO
        return null;
    }
    
    public void update() {
        runTime++;
        for(ConquestArea cArea : areas){
            cArea.update();
        }
    }

    @Override
    public void endKoth(EndReason reason) {
        Arrays.sort(fScores.toArray());
        CappingGroup<?> faction = fScores.get(fScores.size()-1).getFaction();
        if (reason == EndReason.WON || reason == EndReason.GRACEFUL) {
            
            if (faction != null) {
                new MessageBuilder(Lang.KOTH_PLAYING_WON).maxTime(maxRunTime).capper(faction.getName()).koth(koth)/*.shouldExcludePlayer()*/.buildAndBroadcast();
//                if (Bukkit.getPlayer(cappingPlayer) != null) {
//                    new MessageBuilder(Lang.KOTH_PLAYING_WON_CAPPER).maxTime(maxRunTime).capper(capInfo.getCapper().getName()).koth(koth).buildAndSend(Bukkit.getPlayer(cappingPlayer));
//                }
                // TO-DO
            }
        } else if (reason == EndReason.TIMEUP) {
            // TODO
        }


        KothEndEvent event = new KothEndEvent(this, faction, reason);
        Bukkit.getServer().getPluginManager().callEvent(event);

        koth.setLastWinner(faction);
        if (event.isTriggerLoot()) {
            Bukkit.getScheduler().runTask(getPlugin(), new Runnable() {
                public void run() {
                    koth.triggerLoot(lootAmount, lootChest);
                }
            });
        }
        
        
        final RunningKoth thisObj = this;
        Bukkit.getScheduler().runTask(getPlugin(), new Runnable() {
            public void run() {
                getPlugin().getKothHandler().removeRunningKoth(thisObj);
            }
        });
    }
    
    
    public FactionScore getFactionScore(CappingGroup<?> faction){
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
            this.capInfo = new CapInfo(kC, area, CappingGroup.class, false);
        }
        
        public void update(){
            	capInfo.update();
            	if(capInfo.getCapper() != null){
            		getFactionScore((CappingGroup<?>)capInfo.getCapper()).addPoint();
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
    
    public class FactionScore implements Comparable<FactionScore> {
        private @Getter CappingGroup<?> faction;
        private @Getter @Setter int points;
        private int updateTime = 0;
        private int holdingTime = 0;
        FactionScore(CappingGroup<?> faction){
            this.faction = faction;
            this.points = 0;
        }
        
        public void addPoint(){
            holdingTime++;
            updateTime++;
            if(updateTime == runTime){
                if(holdingTime % 30 == 0){
                    points++;
                    if(points >= maxPoints){
                        Bukkit.broadcastMessage("GG");
                    }
                }
            } else {
                holdingTime = 0;
                updateTime = runTime;
            }
        }

        @Override
        public int compareTo(FactionScore fScore) {
            return fScore.points - this.points;
        }
    }
    
    public MessageBuilder fillMessageBuilder(MessageBuilder mB){
        return mB.maxTime(maxRunTime).koth(koth);
    }
    
    public String getType(){
        return "conquest";
    }

    @Override
    public RunningKoth load(JSONObject obj) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JSONObject save() {
        // TODO Auto-generated method stub
        return null;
    }
}
