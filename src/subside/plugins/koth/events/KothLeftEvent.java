package subside.plugins.koth.events;

import subside.plugins.koth.area.Area;

public class KothLeftEvent extends AbstractEvent {
    private String capper;
    private int amountSecondsCapped;
    private String nextCapper;
    
    public KothLeftEvent(Area area, String capper, int amountSecondsCapped){
        super(area);
        this.capper = capper;
        this.amountSecondsCapped = amountSecondsCapped;
    }
    
    public String getPlayerCapping(){
        return capper;
    }
    
    public int getAmountSecondsCapped(){
        return amountSecondsCapped;
    }
    
    public String getNextCapper(){
        return nextCapper;
    }
    
    public void setNextCapper(String nextCapper){
        this.nextCapper = nextCapper;
    }
}
