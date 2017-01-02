package subside.plugins.koth.datatable;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import subside.plugins.koth.KothPlugin;

public class SQLite implements IDatabase {
    private Connection connection;
    private KothPlugin plugin;
    
    public SQLite(KothPlugin plugin){
        this.plugin = plugin;
    }
    
    public Connection getConnection() throws SQLException {
        if(connection != null && !connection.isClosed())
            return connection;
        
        File dataFile = new File(plugin.getDataFolder(), plugin.getConfigHandler().getDatabase().getDatabase() + ".db");
        
        // If the file doesn't exists, create it.
        if (!dataFile.exists()){
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Couldn't create the file: \"" + plugin.getConfigHandler().getDatabase().getDatabase() + ".db\".");
                return null;
            }
        }
        
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dataFile.getPath());
            return this.connection;
        } catch (ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't find the SQLite library!", e);
        }
        
        return null;
    }
}