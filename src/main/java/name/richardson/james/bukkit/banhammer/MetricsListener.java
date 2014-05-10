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

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.avaje.ebean.EbeanServer;
import org.mcstats.Metrics;

import name.richardson.james.bukkit.utilities.listener.AbstractListener;

import name.richardson.james.bukkit.banhammer.record.BanRecord;
import name.richardson.james.bukkit.banhammer.event.BanHammerPlayerBannedEvent;
import name.richardson.james.bukkit.banhammer.event.BanHammerPlayerPardonedEvent;

public class MetricsListener extends AbstractListener {

	private final EbeanServer database;
	private final Metrics metrics;

	/** The number of bans pardoned since the server started. */
	private int pardonedBans = 0;

	/** The number of permenant bans made since the server started. */
	private int permenantBans = 0;

	/** The number of temporary bans made since the server started. */
	private int temporaryBans = 0;

	/** The total number of pardoned bans made by this server. */
	private int totalPardonedBans;

	/** The total number of permenant bans made by this server. */
	private int totalPermanentBans;

	/** The total number of temporary bans made by this server. */
	private int totalTemporaryBans;

	public MetricsListener(Plugin plugin, PluginManager pluginManager, EbeanServer database) throws IOException {
		super(plugin, pluginManager);
		this.database = database;
		this.metrics = new Metrics(plugin);
		this.setInitialValues();
		this.setupCustomMetrics();
		this.metrics.start();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		for (BanRecord record : event.getRecords()) {
			switch (record.getType()) {
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
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
		for (BanRecord record : event.getRecords()) {
			this.pardonedBans++;
			this.totalPardonedBans++;
			switch (record.getType()) {
				case PERMANENT:
					this.totalPermanentBans--;
					break;
				case TEMPORARY:
					this.totalTemporaryBans--;
					break;
			}
		}
	}

	private void setInitialValues() {
		for (BanRecord record : BanRecord.list(database)) {
			if (record.getState() == BanRecord.State.PARDONED) {
			 	this.totalPardonedBans++;
				continue;
			}
			switch (record.getType()) {
				case PERMANENT:
					this.totalPermanentBans++;
					break;
				case TEMPORARY:
					this.totalTemporaryBans++;
					break;
			}
		}
	}

	private void setupCustomMetrics() {

		// Create a graph to show the total amount of kits issued.
		final Metrics.Graph graph = this.metrics.createGraph("Realtime Ban Statistics");
		graph.addPlotter(new Metrics.Plotter("Permanent bans") {
			@Override
			public int getValue() {
				return MetricsListener.this.permenantBans;
			}
		});
		graph.addPlotter(new Metrics.Plotter("Temporary bans") {
			@Override
			public int getValue() {
				return MetricsListener.this.temporaryBans;
			}
		});
		graph.addPlotter(new Metrics.Plotter("Pardoned bans") {
			@Override
			public int getValue() {
				return MetricsListener.this.pardonedBans;
			}
		});
		// Create a graph to show total ban statistics
		final Metrics.Graph graph2 = this.metrics.createGraph("Overall Ban Statistics");
		graph2.addPlotter(new Metrics.Plotter("Permanent bans") {
			@Override
			public int getValue() {
				return MetricsListener.this.totalPermanentBans;
			}
		});
		graph2.addPlotter(new Metrics.Plotter("Temporary bans") {
			@Override
			public int getValue() {
				return MetricsListener.this.totalTemporaryBans;
			}
		});
		graph2.addPlotter(new Metrics.Plotter("Pardoned bans") {
			@Override
			public int getValue() {
				return MetricsListener.this.totalPardonedBans;
			}
		});
	}

}
