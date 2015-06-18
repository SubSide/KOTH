package subside.plugins.koth.events;

import subside.plugins.koth.area.Area;

public class KothStartEvent extends AbstractEvent {
    private int length;
    
    public KothStartEvent(Area area, int length){
        super(area);
        this.length = length;
    }
    
    public int getLengthInSeconds(){
        return length;
    }
}
