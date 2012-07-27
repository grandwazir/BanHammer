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

import org.bukkit.plugin.java.JavaPlugin;

import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.api.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.utilities.metrics.AbstractMetricsListener;
import name.richardson.james.bukkit.utilities.metrics.Metrics.Graph;
import name.richardson.james.bukkit.utilities.metrics.Metrics.Plotter;

public class MetricsListener extends AbstractMetricsListener {

  /** The number of permenant bans made since the server started */
  private int permenantBans;

  /** The number of temporary bans made since the server started */
  private int temporaryBans;

  /** The number of bans pardoned since the server started */
  private int pardonedBans;

  /** The total number of permenant bans made by this server */
  private int totalPermenantBans;

  /** The total number of temporary bans made by this server */
  private int totalTemporaryBans;

  /** The total number of pardoned bans made by this server */
  private int totalPardonedBans;

  public MetricsListener(final JavaPlugin plugin) throws IOException {
    super(plugin);
  }

  public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
    switch (event.getRecord().getType()) {
    case PERMENANT:
      this.permenantBans++;
      this.totalPermenantBans++;
    case TEMPORARY:
      this.temporaryBans++;
      this.totalTemporaryBans++;
    }
  }

  public void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
    this.pardonedBans++;
    this.totalPardonedBans++;
    switch (event.getRecord().getType()) {
    case PERMENANT:
      this.totalPermenantBans--;
    case TEMPORARY:
      this.totalTemporaryBans--;
    }
  }

  @Override
  protected void setupCustomMetrics() {
    // Create a graph to show the total amount of kits issued.
    final Graph graph = this.metrics.createGraph("Realtime Ban Statistics");
    graph.addPlotter(new Plotter("Permenant bans") {
      @Override
      public int getValue() {
        final int i = MetricsListener.this.permenantBans;
        return i;
      }
    });
    graph.addPlotter(new Plotter("Temporary bans") {
      @Override
      public int getValue() {
        final int i = MetricsListener.this.temporaryBans;
        return i;
      }
    });
    graph.addPlotter(new Plotter("Pardoned bans") {
      @Override
      public int getValue() {
        final int i = MetricsListener.this.pardonedBans;
        return i;
      }
    });
    // Create a graph to show total ban statistics
    final Graph graph2 = this.metrics.createGraph("Overall Ban Statistics");
    graph2.addPlotter(new Plotter("Permenant bans") {
      @Override
      public int getValue() {
        final int i = MetricsListener.this.totalPermenantBans;
        return i;
      }
    });
    graph2.addPlotter(new Plotter("Temporary bans") {
      @Override
      public int getValue() {
        final int i = MetricsListener.this.totalTemporaryBans;
        return i;
      }
    });
    graph2.addPlotter(new Plotter("Pardoned bans") {
      @Override
      public int getValue() {
        final int i = MetricsListener.this.totalPardonedBans;
        return i;
      }
    });
  }

}
