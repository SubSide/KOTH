package subside.plugins.koth.adapter;

import lombok.Getter;

import org.bukkit.Location;

import subside.plugins.koth.area.Area;

public class KothDummy {
    private @Getter Location lootPos;
    private @Getter Location min;
    private @Getter Location middle;
    private @Getter Location max;
    private @Getter String lastWinner;
    private @Getter String name;
    
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
}
