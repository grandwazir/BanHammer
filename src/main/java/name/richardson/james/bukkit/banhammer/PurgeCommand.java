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

import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.DefaultColourFormatter;
import name.richardson.james.bukkit.utilities.localisation.Localisation;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundleByClassLocalisation;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.formatters.BanCountChoiceFormatter;

public class PurgeCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.purge";
	public static final String PERMISSION_OWN = "banhammer.purge.own";
	public static final String PERMISSION_OTHERS = "banhammer.purge.others";

	private static final String BANS_PURGED_KEY = "bans-purged";
	private static final String NO_PERMISSION_KEY = "no-permission";
	private static final String MUST_SPECIFY_PLAYER_KEY = "must-specify-player";
	private static final String PLAYER_HAS_NEVER_BEEN_BANNED_KEY = "player-has-never-been-banned";

	private final BanRecordManager banRecordManager;
	private final ChoiceFormatter choiceFormatter;
	private final PlayerRecordManager playerRecordManager;
	private final Localisation localisation = new ResourceBundleByClassLocalisation(PurgeCommand.class);
	private final ColourFormatter colourFormatter = new DefaultColourFormatter();

	private String playerName;
	private PlayerRecord playerRecord;

	public PurgeCommand(PlayerRecordManager playerRecordManager, BanRecordManager banRecordManager) {
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
		this.choiceFormatter = new BanCountChoiceFormatter();
		this.choiceFormatter.setMessage(colourFormatter.format(localisation.getMessage(BANS_PURGED_KEY), ColourFormatter.FormatStyle.INFO));
	}

	@Override
	public void execute(CommandContext context) {
		if (isAuthorised(context.getCommandSender())) {
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
			context.getCommandSender().sendMessage(choiceFormatter.getMessage());
		} else {
			context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(NO_PERMISSION_KEY), ColourFormatter.FormatStyle.ERROR));
		}
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		if (permissible.hasPermission(PERMISSION_ALL)) return true;
		if (permissible.hasPermission(PERMISSION_OTHERS)) return true;
		if (permissible.hasPermission(PERMISSION_OWN)) return true;
		return false;
	}

	private boolean setPlayerName(CommandContext context) {
		playerName = context.getString(0);
		if (playerName == null) {
			context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(MUST_SPECIFY_PLAYER_KEY), ColourFormatter.FormatStyle.ERROR));
		  return false;
		} else {
			return true;
		}
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(playerName);
		if (playerRecord == null || playerRecord.getBans().size() == 0) {
			context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(PLAYER_HAS_NEVER_BEEN_BANNED_KEY), ColourFormatter.FormatStyle.INFO, playerName));
			return false;
		} else {
			return true;
		}
	}

}

