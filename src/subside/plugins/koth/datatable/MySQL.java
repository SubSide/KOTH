package subside.plugins.koth.datatable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.modules.ConfigHandler;

public class MySQL implements IDatabase {
    private Connection connection;
    private KothPlugin plugin;
    
    public MySQL(KothPlugin plugin){
        this.plugin = plugin;
    }
    
    public Connection getConnection() throws SQLException {
        if(connection != null && !connection.isClosed())
            return connection;
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            ConfigHandler.Database cDB = plugin.getConfigHandler().getDatabase();
            String url = "//" + cDB.getHost() +  ":" + cDB.getPort() + "/" + cDB.getDatabase();
            this.connection = DriverManager.getConnection("jdbc:mysql:" + url, cDB.getUsername(), cDB.getPassword());
            return this.connection;
        } catch (ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't find the MySQL library!", e);
        }
        
        return null;
    }
}
