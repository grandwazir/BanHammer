/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * PurgeCommand.java is part of BanHammer.
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

import java.util.*;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandMatchers;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.localisation.LocalisedCommandSender;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;
import name.richardson.james.bukkit.utilities.permissions.Permissions;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.matchers.PlayerRecordMatcher;
import name.richardson.james.bukkit.banhammer.record.*;

@Permissions(permissions = {"banhammer.purge", "banhammer.purge.own", "banhammer.purge.others"})
public class PurgeCommand extends AbstractCommand {

	private final BanRecordManager banRecordManager;
	private final PlayerRecordManager playerRecordManager;

	private OfflinePlayer player;
	private PlayerRecord playerRecord;

	public PurgeCommand(PermissionManager permissionManager, PlayerRecordManager playerRecordManager, BanRecordManager banRecordManager) {
		super(permissionManager);
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
	}

	@Override
	public void execute(CommandContext context) {
		if (!setPlayer(context)) return;
		if (!setPlayerRecord(context)) return;
		List<BanRecord> records = new ArrayList<BanRecord>();
		boolean own = context.getCommandSender().hasPermission("banhammer.purge.own");
		boolean others = context.getCommandSender().hasPermission("banhammer.purge.others");
		for (BanRecord ban : playerRecord.getBans()) {
			boolean banCreatedBySender = ban.getCreator().getName().equalsIgnoreCase(context.getCommandSender().getName());
			if (banCreatedBySender && !own) continue;
			if (!banCreatedBySender && !others) continue;
			records.add(ban);
		}
		banRecordManager.delete(records);
	}

	private boolean setPlayer(CommandContext context) {
		player = null;
		if (context.has(0)) context.getOfflinePlayer(0);
		if (player == null) context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "must-specify-player"));
		return (player != null);
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(player.getName());
		if (playerRecord == null || playerRecord.getBans().size() == 0) {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.WARNING, "player-has-never-been-banned", player.getName()));
		}
		return (playerRecord != null);
	}

}

