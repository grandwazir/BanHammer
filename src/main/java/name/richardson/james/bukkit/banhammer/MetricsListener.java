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
