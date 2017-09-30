package subside.plugins.koth.gamemodes;

import lombok.Getter;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.captureentities.Capper;
import subside.plugins.koth.utils.JSONSerializable;
import subside.plugins.koth.utils.MessageBuilder;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public abstract class RunningKoth implements JSONSerializable<RunningKoth> {
    protected @Getter GamemodeRegistry gamemodeRegistry;
    
    public RunningKoth(GamemodeRegistry gamemodeRegistry){
        this.gamemodeRegistry = gamemodeRegistry;
    }
    
    /** Convinience method to get the plugin
     * 
     * @return          The KothPlugin
     */
    public KothPlugin getPlugin(){
        return gamemodeRegistry.getPlugin();
    }
    
    
    /** initializer, enforcing every implementation to have this makes it possible
     *  to dynamicly create games
     * 
     */
    public abstract void init(StartParams params);
    
    /** Get the type of the RunningKoth
     * 
     * @return          The koth type
     */
    public abstract String getType();
    
    /** Get the TimeObject for the running KoTH
     * 
     * @return          The TimeObject
     */
    public abstract TimeObject getTimeObject();
    
    /** Get the KoTH that is currently running
     * 
     * @return          The KoTH object
     */
    public abstract Koth getKoth();
    
    /** Get the current capper of the KoTH (this is for API reasons)
     * 
     * @return          The current capper
     */
    public abstract Capper<?> getCapper();
    
    /** Get the lootchest for the KoTH that is currently running
     * 
     * @return          The Loot object
     */
    public abstract String getLootChest();
    
    /** Add data to the MessageBuilder
     * 
     * @return the same MessageBuilder object
     */
    public abstract MessageBuilder fillMessageBuilder(MessageBuilder mB);

    public abstract void update();
    
    /** End the KoTH with a reason
     */
    public abstract void endKoth(EndReason reason);


    public enum EndReason {
        FORCED, GRACEFUL, TIMEUP, WON, CUSTOM
    }
}
