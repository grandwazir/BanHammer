package name.richardson.james.bukkit.banhammer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.EbeanServer;


public class DatabaseHandler extends name.richardson.james.bukkit.util.Database {

  public DatabaseHandler(final EbeanServer database) throws SQLException {
    super(database);
  }

  public static List<Class<?>> getDatabaseClasses() {
    final List<Class<?>> list = new ArrayList<Class<?>>();
    list.add(BanRecord.class);
    return list;
  }

}
