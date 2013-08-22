/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 *
 * RecentCommand.java is part of BanHammer.
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

import java.util.List;

import org.bukkit.permissions.Permissible;

import name.richardson.james.bukkit.utilities.command.AbstractCommand;
import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.formatters.ColourFormatter;
import name.richardson.james.bukkit.utilities.formatters.DefaultColourFormatter;
import name.richardson.james.bukkit.utilities.localisation.Localisation;
import name.richardson.james.bukkit.utilities.localisation.ResourceBundleByClassLocalisation;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;

public class RecentCommand extends AbstractCommand {

	public static final String PERMISSION_ALL = "banhammer.recent";

	private static final int DEFAULT_LIMIT = 5;
	private static final String NO_PERMISSION_KEY = "no-permission";

	private final BanRecordManager banRecordManager;
	private final Localisation localisation = new ResourceBundleByClassLocalisation(RecentCommand.class);
	private final ColourFormatter colourFormatter = new DefaultColourFormatter();

	private int count;

	public RecentCommand(BanRecordManager banRecordManager) {
		this.banRecordManager = banRecordManager;
	}

	@Override
	public void execute(CommandContext context) {
		if (isAuthorised(context.getCommandSender())) {
			setLimit(context);
			List<BanRecord> bans = banRecordManager.list(count);
			for (BanRecord ban : bans) {
				BanRecord.BanRecordFormatter formatter = ban.getFormatter();
				context.getCommandSender().sendMessage(formatter.getMessages());
			}
		} else {
			context.getCommandSender().sendMessage(colourFormatter.format(localisation.getMessage(NO_PERMISSION_KEY), ColourFormatter.FormatStyle.ERROR));
		}
	}

	@Override
	public boolean isAuthorised(Permissible permissible) {
		if(permissible.hasPermission(PERMISSION_ALL)) return true;
		return false;
	}

	private void setLimit(CommandContext context) {
		try {
			count = Integer.parseInt(context.getString(0));
		} catch (NumberFormatException e) {
			count = DEFAULT_LIMIT;
		}
	}

}
