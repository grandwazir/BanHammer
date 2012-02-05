/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * DatabaseHandler.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.EbeanServer;

public class DatabaseHandler extends name.richardson.james.bukkit.util.Database {

  public static List<Class<?>> getDatabaseClasses() {
    final List<Class<?>> list = new ArrayList<Class<?>>();
    list.add(BanRecord.class);
    return list;
  }

  public DatabaseHandler(final EbeanServer database) throws SQLException {
    super(database);
  }

}
