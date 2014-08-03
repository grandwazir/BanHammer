/*******************************************************************************
 Copyright (c) 2014 James Richardson.

 PlayerNotifier.java is part of BanHammer.

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
package name.richardson.james.bukkit.banhammer.ban;

import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.listener.AbstractListener;

import name.richardson.james.bukkit.banhammer.*;

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
			String message = MESSAGES.playerPardonedBy(record.getPlayer().getName(), event.getCommandSender().getName());
			server.broadcast(message, BanHammer.NOTIFY_PERMISSION_NAME);
			message = MESSAGES.banReason(record.getComment(CommentRecord.Type.PARDON_REASON).getComment());
			server.broadcast(message, BanHammer.NOTIFY_PERMISSION_NAME);
		}
	}

}
