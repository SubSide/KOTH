package subside.plugins.koth.events;

import subside.plugins.koth.area.Area;

public class KothEndEvent extends AbstractEvent {
    private String winner;
    private boolean createChest;
    
    
    public KothEndEvent(Area area, String capper){
        super(area);
        this.winner = capper;
        this.createChest = true;
    }
    
    public String getPlayerCapping(){
        return winner;
    }
    
    public boolean isCreatingChest(){
        return createChest;
    }
    
    public void setChestCreation(boolean shouldCreate){
        createChest = shouldCreate;
    }
}
