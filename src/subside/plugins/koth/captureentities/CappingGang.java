package subside.plugins.koth.captureentities;

import java.util.Collection;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.brcdev.gangs.GangsPlugin;
import net.brcdev.gangs.gang.Gang;

public class CappingGang extends CappingGroup<Gang> {
    
    public CappingGang(CaptureTypeRegistry captureTypeRegistry, Gang gang){
        super(captureTypeRegistry, "gang", gang);
    }
    
    public CappingGang(CaptureTypeRegistry captureTypeRegistry, Collection<Player> playerList){
        this(captureTypeRegistry,
                playerList.stream() // Create a stream
                .filter(player -> GangsPlugin.getInstance().gangManager.isInGang(player)) // Filter to only players in a gang
                .map(player -> GangsPlugin.getInstance().gangManager.getPlayersGang(player)) // Create a new stream containing gangs
                .findAny() // Grab a single gang
                .orElse(null) // If no gang exists return null
        );
    }

    public CappingGang(CaptureTypeRegistry captureTypeRegistry, String name){
        this(captureTypeRegistry, GangsPlugin.getInstance().gangManager.getGang(Integer.parseInt(name)));
    }
    
    @Override
    public boolean isInOrEqualTo(OfflinePlayer oPlayer){
        if(!oPlayer.isOnline()) return false;
        return getObject().equals(GangsPlugin.getInstance().gangManager.getPlayersGang(oPlayer));
    }
    
    @Override
    public String getUniqueObjectIdentifier(){
        return ""+getObject().getId();
    }
    
    @Override
    public String getName(){
        return getObject().getName();
    }
    
    @Override 
    public Collection<Player> getAllOnlinePlayers(){
        return getObject().getOnlineMembers();
    }
}
