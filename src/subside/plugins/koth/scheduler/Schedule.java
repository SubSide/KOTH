package subside.plugins.koth.scheduler;

import subside.plugins.koth.area.KothHandler;
import subside.plugins.koth.scheduler.ScheduleHandler.Day;

public class Schedule {
	private long nextEventMillis;
	private String area;
	private int runTime;
	private Day day;
	private String time;

	public Schedule(long nextEvent, String area, int runTime, Day day, String time) {
		this.nextEventMillis = nextEvent;
		this.area = area;
		this.runTime = runTime;
		this.day = day;
		this.time = time;
		
	}

	public void tick() {
		if (System.currentTimeMillis() > nextEventMillis) {
			setNextEventTime();
			KothHandler.startKoth(area, runTime*60);
		}
	}

	private void setNextEventTime() {
		nextEventMillis += 1000 * 60 * 60 * 24 * 7;
	}
	
	public int getRunTime(){
		return runTime;
	}
	
	public long getNextEvent(){
		return nextEventMillis;
	}
	
	public String getArea(){
		return area;
	}
	public String getTime(){
		return time;
	}
	
	public Day getDay(){
		return day;
	}

}
