package subside.plugins.koth.datatable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.gamemodes.RunningKoth.EndReason;

public class DataTableEventListener implements Listener {
    DataTable dataTable;
    public DataTableEventListener(DataTable dataTable){
        this.dataTable = dataTable;
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onKothEnd(KothEndEvent event){

        if(event.getReason() != EndReason.GRACEFUL && event.getReason() != EndReason.WON){
            return;
        }
        
        if(event.getWinner() == null || event.getWinner().getObject() == null){
            return;
        }
        

        try {
            PreparedStatement ps = dataTable.getDatabaseProvider().getConnection().prepareStatement(
                    "INSERT INTO results(id, koth, gamemode, date, capper_uuid, capper_displayname, capper_type) "
                    + "VALUES (NULL, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, event.getKoth().getName());
            ps.setString(2, event.getRunningKoth().getType());
            ps.setInt(3, (int)System.currentTimeMillis()/1000);
            ps.setString(4, event.getWinner().getUniqueObjectIdentifier());
            ps.setString(5, event.getWinner().getName());
            ps.setString(6, event.getWinner().getUniqueClassIdentifier());
            ps.execute();
        }
        catch (SQLException e) {
            dataTable.getPlugin().getLogger().log(Level.SEVERE, "Couldn't execute a query!", e);
        }
    }
}
