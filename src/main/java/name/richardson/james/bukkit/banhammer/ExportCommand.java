/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * ExportCommand.java is part of BanHammer.
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

import org.bukkit.Server;
import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.formatters.ChoiceFormatter;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.utilities.formatters.BanCountChoiceFormatter;

import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.EXPORT_COMMAND_NAME;
import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.EXPORT_COMMAND_DESC;
import static name.richardson.james.bukkit.banhammer.utilities.localisation.BanHammer.EXPORT_SUMMARY;

public class ExportCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.export";

	private final ChoiceFormatter choiceFormatter;
	private final PlayerRecordManager playerRecordManager;
	private final Server server;

	public ExportCommand(PlayerRecordManager playerRecordManager, Server server) {
		super(EXPORT_COMMAND_NAME, EXPORT_COMMAND_DESC);
		this.playerRecordManager = playerRecordManager;
		this.server = server;
		this.choiceFormatter = new BanCountChoiceFormatter();
		this.choiceFormatter.setMessage(EXPORT_SUMMARY.asInfoMessage());
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		return permissible.hasPermission(PERMISSION_ALL);
	}

	@Override
	public boolean isAsynchronousCommand() {
		return false;
	}

	@Override
	protected void execute() {
		for (PlayerRecord playerRecord : playerRecordManager.list("", PlayerRecordManager.PlayerStatus.BANNED)) {
			this.server.getOfflinePlayer(playerRecord.getName()).setBanned(true);
		}
		this.choiceFormatter.setArguments(playerRecordManager.count());
		getContext().getCommandSender().sendMessage(choiceFormatter.getMessage());
	}

}
