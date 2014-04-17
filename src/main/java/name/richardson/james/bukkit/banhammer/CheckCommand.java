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
package name.richardson.james.bukkit.banhammer;

import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.argument.Argument;
import name.richardson.james.bukkit.utilities.command.argument.PlayerNamePositionalArgument;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation.CHECK_COMMAND_DESC;
import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammerLocalisation.CHECK_COMMAND_NAME;

public class CheckCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.check";

	private final Argument player;
	private final PlayerRecordManager playerRecordManager;

	public CheckCommand(PlayerRecordManager playerRecordManager) {
		super(CHECK_COMMAND_NAME, CHECK_COMMAND_DESC);
		this.playerRecordManager = playerRecordManager;
		this.player = PlayerNamePositionalArgument.getInstance(playerRecordManager, 0, true, PlayerRecordManager.PlayerStatus.BANNED);
		addArgument(player);
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL);
	}

	@Override
	public boolean isAsynchronousCommand() {
		return true;
	}

	@Override
	protected void execute() {
		// currently only supports checking one player at once.
		if (playerRecordManager.exists(player.getString())) {
			PlayerRecord record = playerRecordManager.find(player.getString());
			BanRecord.BanRecordFormatter formatter = record.getActiveBan().getFormatter();
			getContext().getCommandSender().sendMessage(formatter.getMessages().toArray(new String[3]));
		} else {
			String message = getLocalisation().formatAsErrorMessage(BanHammerLocalisation.PLAYER_NOT_BANNED, player.getString());
			getContext().getCommandSender().sendMessage(message);
		}
	}

}
