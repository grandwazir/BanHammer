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
import name.richardson.james.bukkit.utilities.localisation.PluginLocalisation;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.ban.event.BanHammerPlayerPardonedEvent;
import name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation;

public class PardonCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.pardon";
	public static final String PERMISSION_OWN = "banhammer.pardon.own";
	public static final String PERMISSION_OTHERS = "banhammer.pardon.others";

	private final BanRecordManager banRecordManager;
	private final PlayerRecordManager playerRecordManager;
	private final PluginManager pluginManager;
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
		boolean silent = (context.hasSwitch("s") || context.hasSwitch("silent"));
		BanRecord ban = playerRecord.getActiveBan();
		ban.setState(BanRecord.State.PARDONED);
		banRecordManager.save(ban);
		BanHammerPlayerPardonedEvent event = new BanHammerPlayerPardonedEvent(ban, context.getCommandSender(), silent);
		String message = getLocalisation().formatAsInfoMessage(BanHammerLocalisation.PARDON_PLAYER, playerName);
		context.getCommandSender().sendMessage(message);
		pluginManager.callEvent(event);
	}

	private boolean setPlayerRecord(CommandContext context) {
		playerRecord = playerRecordManager.find(playerName);
		if (playerRecord == null || playerRecord.getActiveBan() == null) {
			String message = getLocalisation().formatAsErrorMessage(BanHammerLocalisation.PLAYER_NOT_BANNED);
			context.getCommandSender().sendMessage(message);
			return false;
		} else {
			return true;
		}
	}

	private boolean setPlayer(CommandContext context) {
		playerName = null;
		if (context.hasArgument(0)) playerName = context.getString(0);
		if (playerName == null) {
			String message = getLocalisation().formatAsErrorMessage(PluginLocalisation.COMMAND_MUST_SPECIFY_PLAYER);
			context.getCommandSender().sendMessage(message);
			return false;
		} else {
			return true;
		}
	}

	private boolean hasPermission(CommandSender sender) {
		String creatorName = playerRecord.getActiveBan().getCreator().getName();
		final boolean isSenderTargetingSelf = (creatorName.equalsIgnoreCase(sender.getName()));
		if (sender.hasPermission(PERMISSION_OWN) && isSenderTargetingSelf) return true;
		if (sender.hasPermission(PERMISSION_OTHERS) && !isSenderTargetingSelf) return true;
		String message = getLocalisation().formatAsErrorMessage(BanHammerLocalisation.PARDON_UNABLE_TO_TARGET_PLAYER, playerName);
		sender.sendMessage(message);
		return false;
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL) || permissible.hasPermission(PERMISSION_OWN) || permissible.hasPermission(PERMISSION_OTHERS);
	}

}
