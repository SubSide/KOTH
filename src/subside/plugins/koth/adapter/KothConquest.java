package subside.plugins.koth.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.captypes.Capper;
import subside.plugins.koth.adapter.captypes.CappingGroup;
import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.scoreboard.ConquestScoreboard;
import subside.plugins.koth.scoreboard.ScoreboardManager;
import subside.plugins.koth.utils.MessageBuilder;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothConquest implements RunningKoth {
    private @Getter Koth koth;
    private @Getter String lootChest;
    private @Getter int lootAmount;

    private @Getter int maxRunTime;
    private @Getter int maxPoints = 100; // TODO
    
    private @Getter List<ConquestArea> areas;
    private @Getter List<FactionScore> fScores;
    private ConquestScoreboard scoreboard;
    private int runTime;


    @Override
    public void init(StartParams params) {
        this.koth = params.getKoth();
        this.lootAmount = params.getLootAmount();
        
        areas = new ArrayList<>();
        fScores = new ArrayList<>();
        
        for(Area area : koth.getAreas()){
            areas.add(new ConquestArea(this, area));
        }

        final KothConquest thiz = this;
        Bukkit.getScheduler().runTask(KothPlugin.getPlugin(), new Runnable(){
            @Override
            public void run() {
                scoreboard = (ConquestScoreboard)ScoreboardManager.getInstance().loadScoreboard("conquest", thiz);
            }    
        });
    }

    @Override
    public Capper getCapper(){
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
    
    @Deprecated
    public void update() {
        runTime++;
        for(ConquestArea cArea : areas){
            cArea.update();
        }
    }

    @Override
    public void endKoth(EndReason reason) {
        Arrays.sort(fScores.toArray());
        CappingGroup faction = fScores.get(fScores.size()-1).getFaction();
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


        KothEndEvent event = new KothEndEvent(koth, faction, reason);
        Bukkit.getServer().getPluginManager().callEvent(event);

        koth.setLastWinner(faction);
        if (event.isCreatingChest()) {
            Bukkit.getScheduler().runTask(KothPlugin.getPlugin(), new Runnable() {
                public void run() {
                    koth.createLootChest(lootAmount, lootChest);
                }
            });
        }
        
        
        final RunningKoth thisObj = this;
        Bukkit.getScheduler().runTask(KothPlugin.getPlugin(), new Runnable() {
            @SuppressWarnings("deprecation")
            public void run() {
                KothHandler.getInstance().remove(thisObj);
            }
        });
    }
    
    
    public FactionScore getFactionScore(CappingGroup faction){
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
        
        @Deprecated
        public void update(){
            	capInfo.update();
            	if(capInfo.getCapper() != null){
            		getFactionScore((CappingGroup)capInfo.getCapper()).addPoint();
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
        private @Getter CappingGroup faction;
        private @Getter @Setter int points;
        private int updateTime = 0;
        private int holdingTime = 0;
        FactionScore(CappingGroup faction){
            this.faction = faction;
            this.points = 0;
        }
        
        public void addPoint(){
            holdingTime++;
            updateTime++;
            if(updateTime == runTime){
                if(holdingTime % 30 == 0){
                    points++;
                    scoreboard.getSbObject().setFactionScore(faction.getName(), points);
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
}
