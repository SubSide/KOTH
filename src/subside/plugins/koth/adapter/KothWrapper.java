package subside.plugins.koth.adapter;

import java.lang.ref.WeakReference;

import subside.plugins.koth.area.RunningKoth;
import subside.plugins.koth.exceptions.KothNotRunningException;

public class KothWrapper {
    private WeakReference<RunningKoth> koth;
    
    public KothWrapper(RunningKoth koth){
        this.koth = new WeakReference<>(koth);
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
    
    public String getTimeCappedFormatted(){
        check();
        return String.format("%02d", getMinutesCapped())+":"+String.format("%02d", getSecondsCapped());
    }
    
    public String getTimeLeftFormatted(){
        check();
        return String.format("%02d", getMinutesLeft())+":"+String.format("%02d", getSecondsLeft());
    }
    
    
    // Cap time
    public int getSecondsCapped(){
        check();
        return koth.get().getTimeCapped()%60;
    }
    
    public int getTotalSecondsCapped(){
        check();
        return koth.get().getTimeCapped();
    }
    
    public int getMinutesCapped(){
        check();
        return (int)(koth.get().getTimeCapped()/60);
    }
    
    
    // Captime left
    public int getSecondsLeft(){
        check();
        return (koth.get().getCaptureTime() - koth.get().getTimeCapped()) % 60;
    }
    
    public int getTotalSecondsLeft(){
        check();
        return koth.get().getCaptureTime() - koth.get().getTimeCapped();
    }
    
    public int getMinutesLeft(){
        check();
        return (koth.get().getCaptureTime() - koth.get().getTimeCapped()) / 60;
    }
    
    
    // Total Captime
    public int getLengthInMinutes(){
        check();
        return koth.get().getCaptureTime()/60;
    }
    
    public int getLengthInSeconds(){
        check();
        return koth.get().getCaptureTime();
    }
}
