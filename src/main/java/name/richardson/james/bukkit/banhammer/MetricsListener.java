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

  public MetricsListener(JavaPlugin plugin) throws IOException {
    super(plugin);
  }
  
  public void onPlayerBanned(BanHammerPlayerBannedEvent event) {
    switch (event.getRecord().getType()) {
      case PERMENANT:
        permenantBans++;
        totalPermenantBans++;
      case TEMPORARY:
        temporaryBans++;
        totalTemporaryBans++;
    }
  }
  
  public void onPlayerPardoned(BanHammerPlayerPardonedEvent event) {
    pardonedBans++;
    totalPardonedBans++;
    switch (event.getRecord().getType()) {
      case PERMENANT:
        totalPermenantBans--;
      case TEMPORARY:
        totalTemporaryBans--;
    }
  }
  
  protected void setupCustomMetrics() {
    // Create a graph to show the total amount of kits issued.
    Graph graph = this.metrics.createGraph("Realtime Ban Statistics");
    graph.addPlotter(new Plotter("Permenant bans") {
      @Override
      public int getValue() {
        int i = permenantBans;
        return i;
      }
    });
    graph.addPlotter(new Plotter("Temporary bans") {
      @Override
      public int getValue() {
        int i = temporaryBans;
        return i;
      }
    });
    graph.addPlotter(new Plotter("Pardoned bans") {
      @Override
      public int getValue() {
        int i = pardonedBans;
        return i;
      }
    });
    // Create a graph to show total ban statistics
    Graph graph2 = this.metrics.createGraph("Overall Ban Statistics");
    graph2.addPlotter(new Plotter("Permenant bans") {
      @Override
      public int getValue() {
        int i = totalPermenantBans;
        return i;
      }
    });
    graph2.addPlotter(new Plotter("Temporary bans") {
      @Override
      public int getValue() {
        int i = totalTemporaryBans;
        return i;
      }
    });
    graph2.addPlotter(new Plotter("Pardoned bans") {
      @Override
      public int getValue() {
        int i = totalPardonedBans;
        return i;
      }
    });
  }

}
