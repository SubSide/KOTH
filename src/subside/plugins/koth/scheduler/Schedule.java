package subside.plugins.koth.scheduler;

import lombok.Getter;
import subside.plugins.koth.area.KothHandler;
import subside.plugins.koth.scheduler.ScheduleHandler.Day;

public class Schedule {
	private long nextEventMillis;
	private @Getter String area;
	private @Getter int runTime;
	private @Getter Day day;
	private @Getter String time;
	private @Getter int maxRunTime;
	private @Getter int lootAmount;

	public Schedule(long nextEvent, String area, int runTime, Day day, String time, int maxRunTime, int lootAmount) {
		this.nextEventMillis = nextEvent;
		this.area = area;
		this.runTime = runTime;
		this.day = day;
		this.time = time;
		this.maxRunTime = maxRunTime;
		this.lootAmount = lootAmount;
		
	}

	public void tick() {
		if (System.currentTimeMillis() > nextEventMillis) {
			setNextEventTime();
			
			//TODO
			KothHandler.startKoth(area, runTime*60, maxRunTime, lootAmount, true);
		}
	}

	private void setNextEventTime() {
		nextEventMillis += 1000 * 60 * 60 * 24 * 7;
	}
	
	public long getNextEvent(){
		return nextEventMillis;
	}
}
