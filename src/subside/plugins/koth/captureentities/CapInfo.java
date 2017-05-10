package subside.plugins.koth.captureentities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.areas.Capable;
import subside.plugins.koth.events.KothCapEvent;
import subside.plugins.koth.events.KothLeftEvent;
import subside.plugins.koth.gamemodes.RunningKoth;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.MessageBuilder;

@SuppressWarnings("rawtypes")
public class CapInfo {
    private @Getter @Setter int timeCapped;

    private @Getter int channelTime;
    private @Getter int knockTime;
    
    private @Getter @Setter Capper capper;
    private @Getter RunningKoth runningKoth;
    private @Getter Capable captureZone;
    private @Getter Class<? extends Capper> ofType;
    private boolean sendMessages;
    
    public CapInfo(RunningKoth runningKoth, Capable captureZone, Class<? extends Capper> ofType, boolean sendMessages){
        this.runningKoth = runningKoth;
        this.captureZone = captureZone;
        this.sendMessages = sendMessages;
        
        this.channelTime = 0;
        this.knockTime = 0;
        
        // If the type is null, set the capEntity to the prefered class.
        if(ofType != null){
            this.ofType = ofType;
        } else {
            this.ofType = runningKoth.getPlugin().getCaptureTypeRegistry().getPreferedClass();
        }
    }
    
    public CapInfo(RunningKoth runningKoth, Capable captureZone, Class<? extends Capper> ofType){
    	this(runningKoth, captureZone, ofType, true);
    }
    
    public CapInfo(RunningKoth runningKoth, Capable captureZone){
    	this(runningKoth, captureZone, null);
    }

    
    /** Override this if you want to use a different type of capper
     * 
     * @param playerList a list of players to choose from
     * @return the correct capper type
     */
    public Capper getRandomCapper(List<Player> playerList){
        return runningKoth.getPlugin().getCaptureTypeRegistry().getCapper(ofType, playerList);
    }
    
    /** Gets updated every single tick
     * 
     */
    public CapStatus update(){
        // If the capper is null find a new entity
        if(capper == null || capper.getObject() == null){
            findAndSetNewEntity();
            // Make sure not to continue if none is found
            if(capper == null || capper.getObject() == null){
                return CapStatus.EMPTY;
            }
        }
        
        
        // If the capper is still in the area, check channelingTime and add a second to the time
        if(capper.areaCheck(captureZone)){
            knockTime = runningKoth.getPlugin().getConfigHandler().getKoth().getKnockTime();
            // Channeling time
            if (channelTime >= 0) {
                if(channelTime > 0){
                    int configChannelTime = runningKoth.getPlugin().getConfigHandler().getKoth().getChannelTime();
                    
                    if (configChannelTime != 0 && channelTime == configChannelTime && sendMessages) {
                        new MessageBuilder(Lang.KOTH_PLAYING_CAP_CHANNELING).capper(getName()).time(""+channelTime).exclude(capper, captureZone).buildAndBroadcast();
                        new MessageBuilder(Lang.KOTH_PLAYING_CAP_CHANNELING_CAPPER).capper(getName()).time(""+channelTime).buildAndSend(capper, captureZone);
                    }
                } else if (sendMessages){
                    runningKoth.fillMessageBuilder(new MessageBuilder(Lang.KOTH_PLAYING_CAP_START)).capper(getName()).exclude(capper, captureZone).buildAndBroadcast();
                    runningKoth.fillMessageBuilder(new MessageBuilder(Lang.KOTH_PLAYING_CAP_START_CAPPER)).capper(getName()).buildAndSend(capper, captureZone);
                }
                
                
                channelTime--;
                return CapStatus.CHANNELING;
            }
            // end channeling time

            return addTime() ? CapStatus.CAPPING : CapStatus.CONTESTED;
        }
        
        // Knocktime
        if(knockTime > 0){
            knockTime--;
            return CapStatus.KNOCKED;
        }
        
        // Trigger an KothLeftEvent
        KothLeftEvent event = new KothLeftEvent(runningKoth, captureZone, capper, timeCapped);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            // If the event is cancelled we still need to add the time.

            return addTime() ? CapStatus.CAPPING : CapStatus.CONTESTED;
        }
        
        // if the event is null it means the entity left the KoTH
        if (event.getNextCapper() == null) {
        	if(sendMessages && channelTime < 0) { // If we were still channeling don't message!
                runningKoth.fillMessageBuilder(new MessageBuilder(Lang.KOTH_PLAYING_LEFT)).capper(getName()).exclude(capper, captureZone).buildAndBroadcast();
                runningKoth.fillMessageBuilder(new MessageBuilder(Lang.KOTH_PLAYING_LEFT_CAPPER)).capper(getName()).buildAndSend(capper, captureZone);
        	}
            capper = null;
            timeCapped = 0;
            return CapStatus.EMPTY;
        } else {
            // If for some reason it has a next capper, we just want it to change to the next capper and then add time
            capper = event.getNextCapper();

            return addTime() ? CapStatus.CAPPING : CapStatus.CONTESTED;
        }
    }
    
    /** Check for contesting (if other players/factions are on the point)
     *  and then add a second to timeCapped.
     */
    public boolean addTime(){
        // Handles contestFreeze by looping over all players to check if someone else is in
        if(runningKoth.getPlugin().getConfigHandler().getKoth().isContestFreeze()){
            for(Player player : getInsidePlayers()){
                if(!capper.isInOrEqualTo(player)){
                    return false;
                }
            }
        }
        
        // Add to timeCapped
        timeCapped++;
        
        return true;
    }
    
    /** finds a new entity that can capture, and sets it.
     *  This is more of a convinience function to keep the update method clean and readable
     */
    private void findAndSetNewEntity(){
        List<Player> insideArea = getInsidePlayers();
        
        if (insideArea.size() < 1) {
            return;
        }
        
        // Get a random capper
        Capper capper = getRandomCapper(insideArea);
        if(capper == null)
            return;
        
        // Create the event.
        KothCapEvent event = new KothCapEvent(runningKoth, captureZone, insideArea, capper);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }
        
        // Change the capper
        this.capper = event.getNextCapper();
        this.channelTime = runningKoth.getPlugin().getConfigHandler().getKoth().getChannelTime();
        
    }
    
    /** Get all the players inside the capture zone except dismissed by hooks.
     * 
     * @return the players inside the capture zone
     */
    public List<Player> getInsidePlayers(){
        List<Player> insideArea = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (captureZone.isInArea(player)) {
                if(runningKoth.getPlugin().getHookManager().canCap(player)) {
                    insideArea.add(player);
                }
            }
        }
        return insideArea;
    }
    
    /**
     * @return the name of the object (Playername for players, Factionname for factions)
     */
    public String getName(){
    	if(capper != null && capper.getObject() != null){
    		return capper.getName();
    	} else {
    		return "None";
    	}
    }
    
    public enum CapStatus {
        EMPTY, CHANNELING, CONTESTED, CAPPING, KNOCKED
    }
}