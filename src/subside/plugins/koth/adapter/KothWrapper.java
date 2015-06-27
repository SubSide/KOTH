package subside.plugins.koth.adapter;

import java.lang.ref.WeakReference;

import subside.plugins.koth.area.RunningKoth;
import subside.plugins.koth.exceptions.KothNotRunningException;

public class KothWrapper {
    private WeakReference<RunningKoth> koth;
    private TimeObject timeObject;
    
    public KothWrapper(RunningKoth koth){
        this.koth = new WeakReference<>(koth);
        timeObject = new TimeObject(koth.getCaptureTime(), koth.getTimeCapped());
    }

    public KothDummy getKoth(){
        check();
        return new KothDummy(koth.get().getArea());
    }
    
    public String getCapper(){
        check();
        String player = koth.get().getCappingPlayer();
        if(player != null)
            return player;
        return "None";
    }
    
    
    private void check() throws KothNotRunningException {
        if(koth == null || koth.get() == null){
            throw new KothNotRunningException();
        }
    }
    
    
    public boolean isRunning(){
        try {
            check();
        } catch(KothNotRunningException e){
            return false;
        }
        return true;
    }
    
    public TimeObject getTimeObject(){
        return timeObject;
    }
}
