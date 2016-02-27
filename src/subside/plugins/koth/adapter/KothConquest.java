package subside.plugins.koth.adapter;

import org.bukkit.entity.Player;

import lombok.Getter;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothConquest implements RunningKoth {
    private @Getter Koth koth;
    private @Getter String lootChest;

    private @Getter int maxRunTime;


    @Override
    public void init(StartParams params) {
        // TODO
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
    public void checkPlayerCapping(Player player){
        // TODO
    }

    @Deprecated
    public void checkPlayerCapping() {
        // TODO

    }

    @Deprecated
    public void update() {
        // TODO
    }

    @Override
    public void endKoth(EndReason reason) {
        // TODO
        
    }
}
