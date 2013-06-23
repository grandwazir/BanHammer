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
package name.richardson.james.bukkit.banhammer.ban;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

import name.richardson.james.bukkit.utilities.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArguments;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.command.argument.InvalidArgumentException;
import name.richardson.james.bukkit.utilities.localisation.LocalisedCoreColourScheme;

import name.richardson.james.bukkit.banhammer.persistence.BanRecordManager;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.argument.PlayerRecordArgument;

@CommandPermissions(permissions = {"banhammer.pardon", "banhammer.pardon.own", "banhammer.pardon.others"})
@CommandArguments(arguments = {PlayerRecordArgument.class})
public class PardonCommand extends AbstractCommand {

	private final BanRecordManager banRecordManager;
	private final ColourScheme colourScheme = new LocalisedCoreColourScheme(this.getResourceBundle());
	private final PlayerRecordManager playerRecordManager;
	private final Server server;

	private OfflinePlayer player;
	private PlayerRecord playerRecord;

	public PardonCommand(final PlayerRecordManager playerRecordManager, final BanRecordManager banRecordManager, final Server server) {
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
		this.server = server;
	}

	@Override
	protected void execute() {
		if (this.hasPermission()) {
			banRecordManager.delete(playerRecord.getActiveBan());
			player.setBanned(false);
			getCommandSender().sendMessage(colourScheme.format(ColourScheme.Style.INFO, "player-pardoned", playerRecord.getName()));
		} else {
			getCommandSender().sendMessage(colourScheme.format(ColourScheme.Style.ERROR, "not-allowed", playerRecord.getName()));
		}
	}

	@Override
	protected boolean parseArguments() {
		try {
			super.parseArguments();
			this.playerRecord = (PlayerRecord) getArgumentValidators().get(0).getValue();
			return true;
		} catch (InvalidArgumentException e) {
			getCommandSender().sendMessage(colourScheme.format(ColourScheme.Style.ERROR, e.getMessage(), e.getArgument()));
			return false;
		} finally {
			if (playerRecord != null && !playerRecord.isBanned()) {
				getCommandSender().sendMessage(colourScheme.format(ColourScheme.Style.WARNING, "player-is-not-banned", playerRecord.getName()));
				return false;
			} else {
				this.player = server.getOfflinePlayer(playerRecord.getName());
				return true;
			}
		}
	}

	@Override
	protected void setArgumentValidators() {
		super.setArgumentValidators();
		PlayerRecordArgument argument = (PlayerRecordArgument) getArgumentValidators().get(0);
		argument.setRequired(true);
		argument.setPlayerStatus(PlayerRecordManager.PlayerStatus.BANNED);
		PlayerRecordArgument.setPlayerRecordManager(playerRecordManager);
	}

	private boolean hasPermission() {
		final boolean isSenderTargetingSelf = (playerRecord.getActiveBan().getCreator().getName().equalsIgnoreCase(getCommandSender().getName())) ? true : false;
		if (getCommandSender().hasPermission("banhammer.pardon.own") && isSenderTargetingSelf) return true;
		if (getCommandSender().hasPermission("banhammer.pardon.others") && !isSenderTargetingSelf) return true;
		return false;
	}

}
