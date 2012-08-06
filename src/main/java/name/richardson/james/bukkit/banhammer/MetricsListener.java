/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * MetricsListener.java is part of BanHammer.
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

import java.io.IOException;

import com.avaje.ebean.EbeanServer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.utilities.listener.Listener;
import name.richardson.james.bukkit.utilities.metrics.Metrics;
import name.richardson.james.bukkit.utilities.metrics.Metrics.Graph;
import name.richardson.james.bukkit.utilities.metrics.Metrics.Plotter;

public class MetricsListener implements Listener {

  /** The number of permenant bans made since the server started. */
  private int permenantBans = 0;

  /** The number of temporary bans made since the server started. */
  private int temporaryBans = 0;

  /** The number of bans pardoned since the server started. */
  private int pardonedBans = 0;

  /** The total number of permenant bans made by this server. */
  private int totalPermanentBans;

  /** The total number of temporary bans made by this server. */
  private int totalTemporaryBans;

  /** The total number of pardoned bans made by this server. */
  private int totalPardonedBans;

  /** The database. */
  private final EbeanServer database;

  private final Metrics metrics;

  /**
   * Instantiates a new metrics listener.
   * 
   * @param plugin the plugin that this listener belongs to.
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public MetricsListener(final BanHammer plugin) throws IOException {
    this.database = plugin.getDatabase();
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    this.metrics = new Metrics(plugin);
    this.setInitialValues();
    this.setupCustomMetrics();
    this.metrics.start();
  }

  /**
   * When a player is banned, increment the statistics.
   * 
   * @param event the event
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
    switch (event.getRecord().getType()) {
    case PERMANENT:
      this.permenantBans++;
      this.totalPermanentBans++;
      break;
    case TEMPORARY:
      this.temporaryBans++;
      this.totalTemporaryBans++;
      break;
    }
  }

  /**
   * When a player is pardoned, increment the statistics.
   * 
   * @param event the event
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
    this.pardonedBans++;
    this.totalPardonedBans++;
    switch (event.getRecord().getType()) {
    case PERMANENT:
      this.totalPermanentBans--;
      break;
    case TEMPORARY:
      this.totalTemporaryBans--;
      break;
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.utilities.metrics.AbstractMetricsListener#
   * setupCustomMetrics()
   */
  private void setupCustomMetrics() {

    // Create a graph to show the total amount of kits issued.
    final Graph graph = this.metrics.createGraph("Realtime Ban Statistics");
    graph.addPlotter(new Plotter("Permanent bans") {
      @Override
      public int getValue() {
        return MetricsListener.this.permenantBans;
      }
    });
    graph.addPlotter(new Plotter("Temporary bans") {
      @Override
      public int getValue() {
        return MetricsListener.this.temporaryBans;
      }
    });
    graph.addPlotter(new Plotter("Pardoned bans") {
      @Override
      public int getValue() {
        return MetricsListener.this.pardonedBans;
      }
    });
    // Create a graph to show total ban statistics
    final Graph graph2 = this.metrics.createGraph("Overall Ban Statistics");
    graph2.addPlotter(new Plotter("Permanent bans") {
      @Override
      public int getValue() {
        return MetricsListener.this.totalPermanentBans;
      }
    });
    graph2.addPlotter(new Plotter("Temporary bans") {
      @Override
      public int getValue() {
        return MetricsListener.this.totalTemporaryBans;
      }
    });
    graph2.addPlotter(new Plotter("Pardoned bans") {
      @Override
      public int getValue() {
        return MetricsListener.this.totalPardonedBans;
      }
    });
  }

  /**
   * Sets the initial values to report with Metrics.
   */
  private void setInitialValues() {
    this.totalPermanentBans = BanRecord.getPermanentBanCount(database);
    this.totalTemporaryBans = BanRecord.getTemporaryBanCount(database);
    this.totalPardonedBans = BanRecord.getPardonedBanCount(database);
  }

}
