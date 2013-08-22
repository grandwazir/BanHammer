/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * PardonCommand.java is part of BanHammer.
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

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.PluginManager;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.DefaultColourFormatter;
import name.richardson.james.bukkit.utilities.localisation.Localisation;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundleByClassLocalisation;

import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.ban.event.BanHammerPlayerPardonedEvent;

public class PardonCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.pardon";
	public static final String PERMISSION_OWN = "banhammer.pardon.own";
	public static final String PERMISSION_OTHERS = "banhammer.pardon.others";

	private static final String PLAYER_PARDONED_KEY = "player-pardoned";
	private static final String UNABLE_TO_TARGET_PLAYER_KEY = "unable-to-target-player";
	private static final String MUST_SPECIFY_PLAYER_KEY = "must-specify-player";
	private static final String PLAYER_IS_NOT_BANNED_KEY = "player-is-not-banned";

	private final PluginManager pluginManager;
	private final BanRecordManager banRecordManager;
	private final PlayerRecordManager playerRecordManager;
	private final Localisation localisation = new ResourceBundleByClassLocalisation(PardonCommand.class);
	private final ColourFormatter colourFormatter = new DefaultColourFormatter();

	private String playerName;
	private PlayerRecord playerRecord;

	public PardonCommand(PluginManager pluginManager, BanRecordManager banRecordManager, PlayerRecordManager playerRecordManager) {
		this.pluginManager = pluginManager;
		this.banRecordManager = banRecordManager;
		this.playerRecordManager = playerRecordManager;
	}

	@Override
	public void execute(CommandContext context) {
		if (!setPlayer(context)) return;
		if (!setPlayerRecord(context)) return;
		if (!hasPermission(context.getCommandSender())) return;
		BanHammerPlayerPardonedEvent event = new BanHammerPlayerPardonedEvent(playerRecord.getActiveBan(), context.getCommandSender(), false);
		banRecordManager.delete(playerRecord.getActiveBan());
		context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(PLAYER_PARDONED_KEY), ColourFormatter.FormatStyle.INFO, playerName));
		pluginManager.callEvent(event);
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		if (permissible.hasPermission(PERMISSION_ALL)) return true;
		if (permissible.hasPermission(PERMISSION_OWN)) return true;
		if (permissible.hasPermission(PERMISSION_OTHERS)) return true;
		return false;
	}

	private boolean hasPermission(CommandSender sender) {
		String creatorName = playerRecord.getActiveBan().getCreator().getName();
		final boolean isSenderTargetingSelf = (creatorName.equalsIgnoreCase(sender.getName()));
		if (sender.hasPermission(PERMISSION_OWN) && isSenderTargetingSelf) return true;
		if (sender.hasPermission(PERMISSION_OTHERS) && !isSenderTargetingSelf) return true;
		sender.sendMessage(colourFormatter.format(localisation.getMessage(UNABLE_TO_TARGET_PLAYER_KEY), ColourFormatter.FormatStyle.ERROR, playerName));
		return false;
	}

	private boolean setPlayer(CommandContext context) {
		playerName = null;
		if (context.has(0)) playerName = context.getString(0);
		if (playerName == null) {
			context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(MUST_SPECIFY_PLAYER_KEY), ColourFormatter.FormatStyle.ERROR));
			return false;
		} else {
			return true;
		}
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(playerName);
		if (playerRecord == null || playerRecord.getActiveBan() == null) {
			context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(PLAYER_IS_NOT_BANNED_KEY), ColourFormatter.FormatStyle.INFO, playerName));
			return false;
		} else {
			return true;
		}
	}

}
