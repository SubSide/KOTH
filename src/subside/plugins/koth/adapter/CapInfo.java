package subside.plugins.koth.adapter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.captypes.Capper;
import subside.plugins.koth.adapter.captypes.CappingFactionNormal;
import subside.plugins.koth.adapter.captypes.CappingFactionUUID;
import subside.plugins.koth.adapter.captypes.CappingPlayer;
import subside.plugins.koth.events.KothCapEvent;
import subside.plugins.koth.events.KothLeftEvent;
import subside.plugins.koth.exceptions.NoCompatibleCapperException;
import subside.plugins.koth.utils.MessageBuilder;

public class CapInfo {
    
    private @Getter @Setter int timeCapped;
    
    private @Getter @Setter Capper capper;
    private @Getter RunningKoth runningKoth;
    private @Getter Capable captureZone;
    private @Getter boolean useFactions;
    
    public CapInfo(RunningKoth runningKoth, Capable captureZone, boolean useFactions){
        this.runningKoth = runningKoth;
        this.captureZone = captureZone;
        this.useFactions = useFactions;
    }
    
    /* Override this if you want to have more control over the capturing
     * 
     */
    public void onCapture(){
    }
    
    /* Override this if you want to use a different type of capper
     * 
     */
    public Capper getRandomCapper(List<Player> playerList){
        if(!useFactions) return new CappingPlayer(playerList);
        
        try {
            Class.forName("com.massivecraft.factions.entity.FactionColl");
            return new CappingFactionNormal(playerList);
        } catch(ClassNotFoundException e){
            return new CappingFactionUUID(playerList);
        } catch(NoCompatibleCapperException e){
        	return null;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    /* Returns true if capper is still on field
     * 
     */
    public void update(){
        if(capper != null && capper.getObject() != null){
            if(capper.areaCheck(captureZone)){
                timeCapped++;
                return;
            }
            KothLeftEvent event = new KothLeftEvent(runningKoth, captureZone, capper, timeCapped);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                timeCapped++;
                return;
            }
            
        
        

            if (event.getNextCapper() == null) {
//                new MessageBuilder(Lang.KOTH_PLAYING_LEFT).maxTime(maxRunTime).time(getTimeObject()).player(pCapper.getName()).koth(koth).shouldExcludePlayer().buildAndBroadcast();
//                if (pCapper.isOnline()) {
//                    new MessageBuilder(Lang.KOTH_PLAYING_LEFT_CAPPER).maxTime(maxRunTime).time(getTimeObject()).player(pCapper.getName()).koth(koth).buildAndSend(pCapper.getPlayer());
//                }

                runningKoth.fillMessageBuilder(new MessageBuilder(Lang.KOTH_PLAYING_LEFT)).capper(getName()).shouldExcludePlayer().buildAndBroadcast();
                capper = null;
                timeCapped = 0;
            } else {
                timeCapped++;
                capper = event.getNextCapper();
            }   
        } else {
        	capper = null;
            List<Player> insideArea = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (captureZone.isInArea(player)) {
                    insideArea.add(player);
                }
            }
            if (insideArea.size() < 1) {
                return;
            }
            
            
            Capper capper = getRandomCapper(insideArea);
            if(capper == null)
            	return;
            
            KothCapEvent event = new KothCapEvent(runningKoth, captureZone, insideArea, capper);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }

            capper = event.getNextCapper();
            runningKoth.fillMessageBuilder(new MessageBuilder(Lang.KOTH_PLAYING_CAP_START)).capper(getName()).shouldExcludePlayer().buildAndBroadcast();
//            if (Bukkit.getPlayer(cappingPlayer) != null) {
//                new MessageBuilder(Lang.KOTH_PLAYING_PLAYERCAP_CAPPER).maxTime(maxRunTime).capper(cappingPlayer).koth(koth).time(getTimeObject()).buildAndSend(Bukkit.getPlayer(cappingPlayer));
//            }
            // TODO
        }
    }
    
    public String getName(){
    	if(capper != null && capper.getObject() != null){
    		return capper.getName();
    	} else {
    		return "None";
    	}
    }
}