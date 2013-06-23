/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * CheckCommand.java is part of BanHammer.
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

import name.richardson.james.bukkit.utilities.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.CommandArguments;
import name.richardson.james.bukkit.utilities.command.CommandPermissions;
import name.richardson.james.bukkit.utilities.command.argument.InvalidArgumentException;

import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.argument.PlayerRecordArgument;

@CommandPermissions(permissions = {"banhammer.check"})
@CommandArguments(arguments = {PlayerRecordArgument.class})
public class CheckCommand extends AbstractCommand {

	private final PlayerRecordManager playerRecordManager;

	private PlayerRecord playerRecord;

	public CheckCommand(final PlayerRecordManager playerRecordManager) {
		this.playerRecordManager = playerRecordManager;
	}

	@Override
	protected void execute() {
		final BanRecord ban = playerRecord.getActiveBan();
		final BanSummary summary = new BanSummary(ban);
		getCommandSender().sendMessage(summary.getHeader());
		getCommandSender().sendMessage(summary.getReason());
		getCommandSender().sendMessage(summary.getLength());
		if (ban.getType() == BanRecord.Type.TEMPORARY) {
			getCommandSender().sendMessage(summary.getExpiresAt());
		}
	}

	@Override
	protected boolean parseArguments() {
		try {
			super.parseArguments();
			this.playerRecord = (PlayerRecord) getArgumentValidators().get(0).getValue();
			return true;
		} catch (InvalidArgumentException e) {
			getCommandSender().sendMessage(getColourScheme().format(ColourScheme.Style.ERROR, e.getMessage(), e.getArgument()));
			return false;
		} finally {
			if (playerRecord == null || (playerRecord != null && playerRecord.isBanned())) {
				getCommandSender().sendMessage(getColourScheme().format(ColourScheme.Style.WARNING, "player-is-not-banned", getArguments().get(0)));
				return false;
			}
		}
	}

	@Override
	protected void setArgumentValidators() {
		super.setArgumentValidators();
		PlayerRecordArgument argument = (PlayerRecordArgument) getArgumentValidators().get(0);
		PlayerRecordArgument.setPlayerRecordManager(playerRecordManager);
		argument.setRequired(false);
		argument.setPlayerStatus(PlayerRecordManager.PlayerStatus.BANNED);
	}

}
