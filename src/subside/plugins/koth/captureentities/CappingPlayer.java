package subside.plugins.koth.captureentities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CappingPlayer extends Capper<OfflinePlayer> {
    
    public CappingPlayer(CaptureTypeRegistry captureTypeRegistry, OfflinePlayer player){
        super(captureTypeRegistry, "player", player);
    }
    
    public CappingPlayer(CaptureTypeRegistry captureTypeRegistry, Collection<Player> playerList){
        this(captureTypeRegistry, (Player)playerList.toArray()[new Random().nextInt(playerList.size())]);
    }
    
    public CappingPlayer(CaptureTypeRegistry captureTypeRegistry, String uuid){
        this(captureTypeRegistry, Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
    }
    
    @Override
    public boolean isInOrEqualTo(OfflinePlayer oPlayer){
        return oPlayer.getUniqueId().equals(getObject().getUniqueId());
    }
    
    @Override
    public String getUniqueObjectIdentifier(){
        return getObject().getUniqueId().toString();
    }
    
    @Override
    public String getName(){
        if(!getObject().isOnline() || !captureTypeRegistry.getPlugin().getConfigHandler().getGlobal().isUseFancyPlayerName()){
            return getObject().getName();
        } else {
            return getObject().getPlayer().getDisplayName();
        }
    }
    
    @Override
    public Collection<Player> getAllOnlinePlayers(){
        List<Player> list = new ArrayList<>();
        if(getObject().isOnline()){
            list.add(getObject().getPlayer());
        }
        return list;
    }
}