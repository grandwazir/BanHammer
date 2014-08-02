package name.richardson.james.bukkit.banhammer.ban;

import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.listener.AbstractListener;

import name.richardson.james.bukkit.banhammer.BanRecord;
import name.richardson.james.bukkit.banhammer.Messages;
import name.richardson.james.bukkit.banhammer.MessagesFactory;
import name.richardson.james.bukkit.banhammer.BanHammer;

public class PlayerNotifier extends AbstractListener {

	private static final Messages MESSAGES = MessagesFactory.getColouredMessages();
	private final Server server;

	public PlayerNotifier(final Plugin plugin, final PluginManager pluginManager, final Server server) {
		super(plugin, pluginManager);
		this.server = server;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBanned(final BanHammerPlayerBannedEvent event) {
		if (event.isSilent()) return;
		for (BanRecord record : event.getRecords()) {
			BanRecordFormatter formatter = new SimpleBanRecordFormatter(record);
			String message = MESSAGES.playerBannedBy(record.getPlayer().getName(), record.getCreator().getName());
			server.broadcast(message, BanHammer.NOTIFY_PERMISSION_NAME);
			server.broadcast(formatter.getReason(), BanHammer.NOTIFY_PERMISSION_NAME);
			server.broadcast(formatter.getLength(), BanHammer.NOTIFY_PERMISSION_NAME);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPardoned(final BanHammerPlayerPardonedEvent event) {
		if (event.isSilent()) return;
		for (BanRecord record : event.getRecords()) {
			String message = MESSAGES.playerPardonedBy(record.getPlayer().getName(), event.getCommandSender());
			server.broadcast(message, BanHammer.NOTIFY_PERMISSION_NAME);
		}
	}

}
