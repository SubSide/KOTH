package subside.plugins.koth.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import subside.plugins.koth.area.RunningKoth;


public class KothAdapter {
    private static KothAdapter adapter = new KothAdapter();
    private ArrayList<KothWrapper> runningKoths;

    public static KothAdapter getAdapter() {
        return adapter;
    }

    private KothAdapter(){
        runningKoths = new ArrayList<>();
    }
    
    public void addRunningKoth(RunningKoth koth){
        synchronized(runningKoths){
            runningKoths.add(new KothWrapper(koth));
        }
    }
    
    public void removeRunningKoth(RunningKoth koth){
        synchronized(runningKoths){
            Iterator<KothWrapper> it = runningKoths.iterator();
            while(it.hasNext()){
                KothWrapper kw = it.next();
                if(kw.getKoth().getName().equalsIgnoreCase(koth.getArea().getName())){
                    it.remove();
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<KothWrapper> getRunningKoths(){
        return (List<KothWrapper>)runningKoths.clone();
    }
    
}
