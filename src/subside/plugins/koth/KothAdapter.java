package subside.plugins.koth;

import java.lang.ref.WeakReference;

import org.bukkit.Location;

import subside.plugins.koth.area.KothHandler;
import subside.plugins.koth.area.RunningKoth;

@Deprecated
public class KothAdapter {
	private static KothAdapter adapter = new KothAdapter();

	@Deprecated
	public static KothAdapter getAdapter(){
		return adapter;
	}
	
	// Basic info
	public String getName(){
        WeakReference<RunningKoth> rKoth = KothHandler.getRunningKoth();
        if(rKoth.get() != null){
            return rKoth.get().getArea().getName();
        }
        return "None";
    }
	
	public String getCapper(){
        WeakReference<RunningKoth> rKoth = KothHandler.getRunningKoth();
        if(rKoth.get() != null){
            return rKoth.get().getCappingPlayer();
        }
        return "None";
    }
	
	public Location getLocation(){
		WeakReference<RunningKoth> rKoth = KothHandler.getRunningKoth();
		if(rKoth.get() != null){
			return rKoth.get().getArea().getMiddle();
		}
		return null;
	}
	
	public boolean isRunning(){
		WeakReference<RunningKoth> rKoth = KothHandler.getRunningKoth();
		if(rKoth.get() != null){
			return true;
		}
		return false;
	}
	
	public String getTimeCappedFormatted(){
		return String.format("%02d", getMinutesCapped())+":"+String.format("%02d", getSecondsCapped());
	}
	
	public String getTimeLeftFormatted(){
		return String.format("%02d", getMinutesLeft())+":"+String.format("%02d", getSecondsLeft());
	}
	
	
	// Cap time
	public int getSecondsCapped(){
		WeakReference<RunningKoth> rKoth = KothHandler.getRunningKoth();
		if(rKoth.get() != null){
			return rKoth.get().getTimeCapped()%60;
		}
		return 0;
	}
	
	public int getTotalSecondsCapped(){
		WeakReference<RunningKoth> rKoth = KothHandler.getRunningKoth();
		if(rKoth.get() != null){
			return rKoth.get().getTimeCapped();
		}
		return 0;
	}
	
	public int getMinutesCapped(){
		WeakReference<RunningKoth> rKoth = KothHandler.getRunningKoth();
		if(rKoth.get() != null){
			return (int)(rKoth.get().getTimeCapped()/60);
		}
		return 0;
	}
	
	
	// Captime left
	public int getSecondsLeft(){
		WeakReference<RunningKoth> rKoth = KothHandler.getRunningKoth();
		if(rKoth.get() != null){
			return (rKoth.get().getCaptureTime() - rKoth.get().getTimeCapped()) % 60;
		}
		return 0;
	}
	
	public int getTotalSecondsLeft(){
		WeakReference<RunningKoth> rKoth = KothHandler.getRunningKoth();
		if(rKoth.get() != null){
			return rKoth.get().getCaptureTime() - rKoth.get().getTimeCapped();
		}
		return 0;
	}
	
	public int getMinutesLeft(){
		WeakReference<RunningKoth> rKoth = KothHandler.getRunningKoth();
		if(rKoth.get() != null){
			return (rKoth.get().getCaptureTime() - rKoth.get().getTimeCapped()) / 60;
		}
		return 0;
	}
	
	
	// Total Captime
	public int getLengthInMinutes(){
		WeakReference<RunningKoth> rKoth = KothHandler.getRunningKoth();
		if(rKoth.get() != null){
			return rKoth.get().getCaptureTime()/60;
		}
		return 0;
	}
	
	public int getLengthInSeconds(){
		WeakReference<RunningKoth> rKoth = KothHandler.getRunningKoth();
		if(rKoth.get() != null){
			return rKoth.get().getCaptureTime();
		}
		return 0;
	}
}
