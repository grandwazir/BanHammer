/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * UndoCommand.java is part of BanHammer.
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
import name.richardson.james.bukkit.banhammer.persistence.BanRecordManager;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.argument.PlayerRecordArgument;

@CommandPermissions(permissions = {"banhammer.undo", "banhammer.undo.own", "banhammer.undo.others", "banhammer.undo.unrestricted"})
@CommandArguments(arguments = {PlayerRecordArgument.class})
public class UndoCommand extends AbstractCommand {

	private final BanRecordManager banRecordManager;
	private final PlayerRecordManager playerRecordManager;
	private final long undoTime;

	private BanRecord ban;
	private PlayerRecord playerRecord;

	public UndoCommand(final PlayerRecordManager playerRecordManager, final BanRecordManager banRecordManager, final long undoTime) {
		this.playerRecordManager = playerRecordManager;
		this.banRecordManager = banRecordManager;
		this.undoTime = undoTime;
	}

	public void execute() {
		this.banRecordManager.delete(ban);
		getCommandSender().sendMessage(getColourScheme().format(ColourScheme.Style.INFO, "ban-undone", ban.getPlayer().getName()));
	}

	@Override
	protected boolean parseArguments() {
		try {
			super.parseArguments();
			this.playerRecord = (PlayerRecord) getArgumentValidators().get(0).getValue();
			if (playerRecord == null || playerRecord.getCreatedBans().size() == 0) {
				getCommandSender().sendMessage(getColourScheme().format(ColourScheme.Style.INFO, "no-ban-to-undo"));
				return false;
			} else {
				ban = playerRecord.getCreatedBans().get(playerRecord.getCreatedBans().size() - 1);
				return hasPermission();
			}
		} catch (InvalidArgumentException e) {
			getCommandSender().sendMessage(getColourScheme().format(ColourScheme.Style.ERROR, e.getMessage(), e.getArgument()));
			return false;
		}
	}

	@Override
	protected void setArgumentValidators() {
		super.setArgumentValidators();
		PlayerRecordArgument.setPlayerRecordManager(playerRecordManager);
		PlayerRecordArgument argument = (PlayerRecordArgument) getArgumentValidators().get(0);
		argument.setPlayerStatus(PlayerRecordManager.PlayerStatus.CREATOR);
		argument.setRequired(false);
	}

	private boolean hasPermission() {
		final boolean isSenderTargetingSelf = (this.ban.getCreator().getName().equalsIgnoreCase(getCommandSender().getName())) ? true : false;
		final boolean withinTimeLimit = this.withinTimeLimit();
		if (getCommandSender().hasPermission("banhammer.undo.own") && withinTimeLimit && isSenderTargetingSelf) return true;
		if (getCommandSender().hasPermission("banhammer.undo.others") && withinTimeLimit && !isSenderTargetingSelf) return true;
		getCommandSender().sendMessage(getColourScheme().format(ColourScheme.Style.ERROR, "may-not-undo-that-players-ban"));
		return false;
	}

	private boolean withinTimeLimit() {
		if (getCommandSender().hasPermission("banhammer.undo.unrestricted")) return true;
		if ((System.currentTimeMillis() - ban.getExpiresAt().getTime()) <= this.undoTime) return true;
		getCommandSender().sendMessage(getColourScheme().format(ColourScheme.Style.ERROR, "undo-time-expired"));
		return false;
	}

}
