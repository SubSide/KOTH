package subside.plugins.koth.events;

import org.bukkit.Location;

import subside.plugins.koth.area.Area;

public class KothDummy {
    private Location lootPos;
    private Location min;
    private Location middle;
    private Location max;
    private String lastWinner;
    private String name;
    
    public KothDummy(Area area){
        min = area.getMin().clone();
        middle = area.getMiddle().clone();
        max = area.getMax().clone();
        try {
            lootPos = area.getLootPos().clone();
        } catch(Exception e){}
        lastWinner = area.getLastWinner();
        name = area.getName();
    }
    
    public Location getLootPos(){
        return lootPos;
    }
    
    public Location getMin(){
        return min;
    }
    
    public Location getMiddle(){
        return middle;
    }
    
    public Location getMax(){
        return max;
    }
    
    public String getLastWinner(){
        return lastWinner;
    }
    
    public String getName(){
        return name;
    }
}
