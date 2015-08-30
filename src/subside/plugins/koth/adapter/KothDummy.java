package subside.plugins.koth.adapter;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import subside.plugins.koth.area.Area;

public class KothDummy {
    private Area area;
    
    public KothDummy(Area area){
        this.area = area;
    }
    
    public Location getLootPos(){
        return area.getLootPos();
    }
    
    public Location getMin(){
        return area.getMin().clone();
    }
    
    public Location getMiddle(){
        return area.getMiddle().clone();
    }
    
    public Location getMax(){
        return area.getMax().clone();
    }
    
    public String getLastWinner(){
        return area.getLastWinner();
    }
    
    public String getName(){
        return area.getName();
    }
    
    public boolean isInArea(OfflinePlayer player){
        return area.isInArea(player);
    }
}
