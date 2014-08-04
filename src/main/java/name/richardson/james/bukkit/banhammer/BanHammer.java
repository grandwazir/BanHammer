/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 BanHammer.java is part of BanHammer.

 BanHammer is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option) any
 later version.

 BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import name.richardson.james.bukkit.utilities.command.Command;
import name.richardson.james.bukkit.utilities.command.CommandInvoker;
import name.richardson.james.bukkit.utilities.command.RootCommandInvoker;
import name.richardson.james.bukkit.utilities.command.SimpleCommandInvoker;
import name.richardson.james.bukkit.utilities.persistence.configuration.DatabaseConfiguration;
import name.richardson.james.bukkit.utilities.updater.BukkitDevPluginUpdater;
import name.richardson.james.bukkit.utilities.updater.PluginUpdater;

import name.richardson.james.bukkit.banhammer.ban.*;
import name.richardson.james.bukkit.banhammer.ban.event.PlayerListener;
import name.richardson.james.bukkit.banhammer.ban.event.PlayerNotifier;
import name.richardson.james.bukkit.banhammer.model.BanHammerDatabase;
import name.richardson.james.bukkit.banhammer.player.AuditCommand;
import name.richardson.james.bukkit.banhammer.player.CheckCommand;
import name.richardson.james.bukkit.banhammer.player.CommentCommand;
import name.richardson.james.bukkit.banhammer.player.HistoryCommand;
import name.richardson.james.bukkit.banhammer.player.KickCommand;

public class BanHammer extends JavaPlugin {

	public static final String NOTIFY_PERMISSION_NAME = "banhammer.notify";
	public static final int PROJECT_ID = 31269;
	private static final String CONFIG_NAME = "config.yml";
	private static final String DATABASE_CONFIG_NAME = "database.yml";
	private BanHammerPluginConfiguration configuration;

	@Override
	public void onEnable() {
		try {
			loadConfiguration();
			loadDatabase();
			registerCommands();
			registerListeners();
			// TODO: Reimplement this - this.setupMetrics();
			updatePlugin();
		} catch (final IOException e) {
			getLogger().severe("There was an error enabling the plugin!");
			getLogger().severe(e.getMessage());
		}
	}

	private void loadConfiguration()
	throws IOException {
		File file = new File(getDataFolder().getPath() + File.separatorChar + CONFIG_NAME);
		InputStream defaults = getResource(CONFIG_NAME);
		configuration = new BanHammerPluginConfiguration(file, defaults);
	}

	private void loadDatabase()
	throws IOException {
		File file = new File(getDataFolder().getPath() + File.separatorChar + DATABASE_CONFIG_NAME);
		InputStream defaults = getResource(DATABASE_CONFIG_NAME);
		DatabaseConfiguration configuration = BanHammerDatabase.configure(getServer(), file, defaults);
		BanHammerDatabase.initialise(configuration);
	}

	private void registerCommands() {
		Set<Command> commands = new HashSet<Command>();
		Command command = new AuditCommand(this, getServer().getScheduler());
		commands.add(command);
		command = new BanCommand(this, getServer().getScheduler(), configuration, getServer());
		commands.add(command);
		getCommand("ban").setExecutor(new SimpleCommandInvoker(this, getServer().getScheduler(), command));
		command = new CheckCommand(this, getServer().getScheduler());
		commands.add(command);
		command = new CommentCommand(this, getServer().getScheduler(), getServer());
		commands.add(command);
		command = new HistoryCommand(this, getServer().getScheduler());
		commands.add(command);
		command = new ExportCommand(this, getServer().getScheduler(), getServer());
		commands.add(command);
		command = new ImportCommand(this, getServer().getScheduler(), getServer());
		commands.add(command);
		command = new KickCommand(this, getServer().getScheduler(), getServer());
		commands.add(command);
		getCommand("kick").setExecutor(new SimpleCommandInvoker(this, getServer().getScheduler(), command));
		command = new LimitsCommand(this, getServer().getScheduler(), configuration);
		commands.add(command);
		command = new PardonCommand(this, getServer().getScheduler());
		commands.add(command);
		getCommand("pardon").setExecutor(new SimpleCommandInvoker(this, getServer().getScheduler(), command));
		command = new PurgeCommand(this, getServer().getScheduler(), getDatabase());
		commands.add(command);
		command = new RecentCommand(this, getServer().getScheduler());
		commands.add(command);
		command = new UndoCommand(this, getServer().getScheduler(), configuration);
		commands.add(command);
		CommandInvoker invoker = new RootCommandInvoker(this, getServer().getScheduler(), commands, "/bh");
		getCommand("bh").setExecutor(invoker);
	}

	private void registerListeners() {
		new PlayerListener(this, getServer().getPluginManager(), getServer());
		new PlayerNotifier(this, getServer().getPluginManager(), getServer());
	}

	private void updatePlugin() {
		if (!configuration.getAutomaticUpdaterState().equals(PluginUpdater.State.OFF)) {
			PluginUpdater updater = new BukkitDevPluginUpdater(getDescription(), configuration.getAutomaticUpdaterBranch(), configuration.getAutomaticUpdaterState(), PROJECT_ID, getDataFolder(), Bukkit.getVersion());
			getServer().getScheduler().runTaskAsynchronously(this, updater);
			new name.richardson.james.bukkit.utilities.updater.PlayerNotifier(this, getServer().getPluginManager(), updater);
		}
	}

}
