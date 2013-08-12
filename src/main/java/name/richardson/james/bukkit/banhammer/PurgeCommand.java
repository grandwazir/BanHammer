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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;
import name.richardson.james.bukkit.utilities.permissions.Permissions;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

@Permissions(permissions = {PurgeCommand.PERMISSION_ALL, PurgeCommand.PERMISSION_OWN, PurgeCommand.PERMISSION_OTHERS})
public class PurgeCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.purge";
	public static final String PERMISSION_OWN = "banhammer.purge.own";
	public static final String PERMISSION_OTHERS = "banhammer.purge.others";

	private final BanRecordManager banRecordManager;
	private final BanCountChoiceFormatter choiceFormatter;
	private final PluginManager pluginManager;
	private final PlayerRecordManager playerRecordManager;

	private String playerName;
	private PlayerRecord playerRecord;

	public PurgeCommand(PermissionManager permissionManager, PluginManager pluginManager, PlayerRecordManager playerRecordManager, BanRecordManager banRecordManager) {
		super(permissionManager);
		this.pluginManager = pluginManager;
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
		this.choiceFormatter = new BanCountChoiceFormatter();
		this.choiceFormatter.setMessage("bans-purged");
	}

	@Override
	public void execute(CommandContext context) {
		if (!setPlayerName(context)) return;
		if (!setPlayerRecord(context)) return;
		List<BanRecord> records = new ArrayList<BanRecord>();
		boolean own = context.getCommandSender().hasPermission(PERMISSION_OWN);
		boolean others = context.getCommandSender().hasPermission(PERMISSION_OTHERS);
		for (BanRecord ban : playerRecord.getBans()) {
			boolean banCreatedBySender = ban.getCreator().getName().equalsIgnoreCase(context.getCommandSender().getName());
			if (banCreatedBySender && !own) continue;
			if (!banCreatedBySender && !others) continue;
			records.add(ban);
		}
		this.choiceFormatter.setArguments(records.size(), playerName);
		banRecordManager.delete(records);
		context.getCommandSender().sendMessage(this.choiceFormatter.getColouredMessage(ColourScheme.Style.INFO));
	}

	private boolean setPlayerName(CommandContext context) {
		playerName = null;
		if (context.has(0)) playerName = context.getString(0);
		if (playerName == null) {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.ERROR, "must-specify-player"));
		  return false;
		} else {
			return true;
		}
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(playerName);
		if (playerRecord == null || playerRecord.getBans().size() == 0) {
			context.getCommandSender().sendMessage(getColouredMessage(ColourScheme.Style.WARNING, "player-has-never-been-banned", playerName));
			return false;
		} else {
			return true;
		}
	}

}

