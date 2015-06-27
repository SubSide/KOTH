package subside.plugins.koth.adapter;

public class TimeObject {
    private int captureTime;
    private int timeCapped;
    
    public TimeObject(int captureTime, int timeCapped){
        this.captureTime = captureTime;
        this.timeCapped = timeCapped;
    }
    
    public String getTimeCappedFormatted(){
        return String.format("%02d", getMinutesCapped())+":"+String.format("%02d", getSecondsCapped());
    }
    
    public String getTimeLeftFormatted(){
        return String.format("%02d", getMinutesLeft())+":"+String.format("%02d", getSecondsLeft());
    }
    
    
    // Cap time
    public int getSecondsCapped(){
        return timeCapped%60;
    }
    
    public int getTotalSecondsCapped(){
        return timeCapped;
    }
    
    public int getMinutesCapped(){
        return (int)(timeCapped/60);
    }
    
    
    // Captime left
    public int getSecondsLeft(){
        return (captureTime - timeCapped) % 60;
    }
    
    public int getTotalSecondsLeft(){
        return captureTime - timeCapped;
    }
    
    public int getMinutesLeft(){
        return (captureTime - timeCapped) / 60;
    }
    
    
    // Total Captime
    public int getLengthInMinutes(){
        return captureTime/60;
    }
    
    public int getLengthInSeconds(){
        return captureTime;
    }
}
