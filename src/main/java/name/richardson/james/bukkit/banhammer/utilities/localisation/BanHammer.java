/*
 * Copyright (c) 2013 James Richardson.
 *
 * PluginLocalisation.java is part of BukkitUtilities.
 *
 * BukkitUtilities is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * BukkitUtilities is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * BukkitUtilities. If not, see <http://www.gnu.org/licenses/>.
 */

package name.richardson.james.bukkit.banhammer.utilities.localisation;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import name.richardson.james.bukkit.utilities.formatters.DefaultMessageFormatter;
import name.richardson.james.bukkit.utilities.formatters.MessageFormatter;
import name.richardson.james.bukkit.utilities.localisation.Localised;

public enum BanHammer implements Localised, MessageFormatter {

	ARGUMENT_ALL_ID ("argument.all.id"),
	ARGUMENT_ALL_NAME ("argument.all.name"),
	ARGUMENT_ALL_DESC ("argument.all.desc"),

	ARGUMENT_BANCOUNT_ID ("argument.bancount.id"),
	ARGUMENT_BANCOUNT_NAME ("argument.bancount.name"),
	ARGUMENT_BANCOUNT_DESC ("argument.bancount.desc"),

	ARGUMENT_SILENT_ID ("argument.silent.id"),
	ARGUMENT_SILENT_NAME ("argument.silent.name"),
	ARGUMENT_SILENT_DESC ("argument.silent.desc"),

	ARGUMENT_TIME_ID ("argument.time.id"),
	ARGUMENT_TIME_NAME ("argument.time.name"),
	ARGUMENT_TIME_DESC ("argument.time.desc"),

	ARGUMENT_PLAYER_ID ("argument.player.id"),
	ARGUMENT_PLAYER_NAME ("argument.player.name"),
	ARGUMENT_PLAYER_NAME_MULTIPLE ("argument.player.name-multiple"),
	ARGUMENT_PLAYER_DESC ("argument.player.desc"),
	ARGUMENT_PLAYER_ERROR ("argument.player.error"),
	ARGUMENT_PLAYER_NAME_ERROR ("argument.player-name.error"),

	ARGUMENT_REASON_ID ("argument.reason.id"),
	ARGUMENT_REASON_NAME ("argument.reason.name"),
	ARGUMENT_REASON_DESC ("argument.reason.desc"),
	ARGUMENT_REASON_ERROR ("argument.reason.error"),

	AUDIT_COMMAND_HEADER ("auditcommand.header"),
	AUDIT_COMMAND_NAME ("command.audit.name"),
	AUDIT_COMMAND_DESC ("command.audit.desc"),
	AUDIT_TYPE_SUMMARY ("auditcommand.type-summary"),
	AUDIT_PERMANENT_BANS_PERCENTAGE ("auditcommand.permanent-bans-percentage"),
	AUDIT_TEMPORARY_BANS_PERCENTAGE ("auditcommand.temporary-bans-percentage"),
	AUDIT_STATUS_SUMMARY ("auditcommand.status-summary"),
	AUDIT_ACTIVE_BANS_PERCENTAGE ("auditcommand.active-bans-percentage"),
	AUDIT_EXPIRED_BANS_PERCENTAGE ("auditcommand.expired-bans-percentage"),
	AUDIT_PARDONED_BANS_PERCENTAGE ("auditcommand.pardoned-bans-percentage"),

	BANCOMMAND_NAME ("command.ban.name"),
	BANCOMMAND_DESC ("command.ban.desc"),

	CHECK_COMMAND_NAME ("command.check.name"),
	CHECK_COMMAND_DESC ("command.check.desc"),


	CHOICE_MANY_BANS ("choice.many-bans"),
	CHOICE_MANY_LIMITS ("choice.many-limits"),
	CHOICE_NO_BANS ("choice.no-bans"),
	CHOICE_NO_LIMITS ("choice.no-limits"),
	CHOICE_ONE_BAN ("choice.one-ban"),
	CHOICE_ONE_LIMIT ("choice.one-limit"),

	EXPORT_COMMAND_NAME ("command.export.name"),
	EXPORT_COMMAND_DESC ("command.export.desc"),

	EXPIRES_AT ("expires-at"),
	LENGTH ("length"),
	PERMANENT ("permanent"),
	REASON ("reason"),
	BAN_SUMMARY ("ban-summary"),
	BAN_WAS_PARDONED ("ban-was-pardoned"),

	HISTORY_COMMAND_NAME ("command.history.name"),
	HISTORY_COMMAND_DESC ("command.history.desc"),

	IMPORT_COMMAND_NAME ("command.import.name"),
	IMPORT_COMMAND_DESC ("command.import.desc"),

	PLAYER_BANNED ("bancommand.player-banned"),
	PLAYER_NOT_BANNED ("shared.player-is-not-banned"),
	PLAYER_IS_ALREADY_BANNED ("bancommand.player-is-already-banned"),
	PLUGIN_UNABLE_TO_HOOK_ALIAS ("alias.unable-to-hook-alias"),
	EXPORT_SUMMARY ("exportcommand.summary"),
	PLAYER_NEVER_BEEN_BANNED ("shared.player-has-never-been-banned"),

	IMPORT_SUMMARY ("importcommand.summary"),
	IMPORT_DEFAULT_REASON ("importcommand.default-reason"),

	KICK_COMMAND_NAME ("command.kick.name"),
	KICK_COMMAND_DESC ("command.kick.desc"),
	PLAYER_HAS_NEVER_MADE_ANY_BANS ("player-has-never-made-any-bans"),
	KICK_PLAYER_NOTIFICATION ("kickcommand.player-notification"),
	KICK_SENDER_NOTIFICATION ("kickcommand.sender-notification"),
	KICK_PLAYER_KICKED ("kickcommand.player-kicked-from-server"),
	KICK_DEFAULT_REASON ("kickcommand.default-reason"),

	LIMIT_COMMAND_NAME ("command.limits.name"),
	LIMIT_COMMAND_DESC ("command.limits.desc"),

	LIMIT_SUMMARY ("limitscommand.summary"),
	LIMIT_ENTRY ("limitscommand.entry"),

	PARDON_COMMAND_NAME ("command.pardon.name"),
	PARDON_COMMAND_DESC ("command.pardon.desc"),


	PARDON_PLAYER ("pardoncommand.player"),
	PARDON_UNABLE_TO_TARGET_PLAYER ("pardoncommand.unable-to-target-player"),


	PURGE_COMMAND_NAME ("command.purge.name"),
	PURGE_COMMAND_DESC ("command.purge.desc"),

	PURGE_SUMMARY ("purgecommand.summary"),

	RECENT_COMMAND_NAME ("command.recent.name"),
	RECENT_COMMAND_DESC ("command.recent.desc"),


	RECENT_NO_BANS ("recentcommand.no-bans"),
	UNDO_COMPLETE ("undocommand.complete"),

	UNDO_COMMAND_NAME ("command.undo.name"),
	UNDO_COMMAND_DESC ("command.undo.desc"),

	UNDO_NOT_PERMITTED ("undocommand.not-permitted"),
	UNDO_TIME_EXPIRED ("undocommand.time-expired"),
	ALIAS_BAN_REASON ("alias.ban-reason"),
	LISTENER_PLAYER_BANNED_TEMPORARILY ("listener.player-banned-temporarily"),
	LISTENER_PLAYER_BANNED_PERMANENTLY ("listener.player-banned-permanently"),
	NOTIFY_PLAYER_BANNED ("notifier.player-banned"),
	NOTIFY_PLAYER_PARDONED ("notifier.player-pardoned");

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("localisation/Messages");
	private static final MessageFormatter FORMATTER = new DefaultMessageFormatter();
	private final String key;

	BanHammer(final String key) {
		this.key = key;
	}

	private static String formatMessage(String message, Object... arguments) {
		return MessageFormat.format(message, arguments);
	}

	public String asErrorMessage(final Object... arguments) {
		String message = FORMATTER.asErrorMessage(toString());
		return formatMessage(message, arguments);
	}

	public String asHeaderMessage(final Object... arguments) {
		String message = FORMATTER.asHeaderMessage(toString());
		return formatMessage(message, arguments);
	}

	public String asInfoMessage(final Object... arguments) {
		String message = FORMATTER.asInfoMessage(toString());
		return formatMessage(message, arguments);
	}

	public String asMessage(final Object... arguments) {
		return formatMessage(toString(), arguments);
	}

	public String asWarningMessage(final Object... arguments) {
		String message = FORMATTER.asWarningMessage(toString());
		return formatMessage(message, arguments);
	}

	public String getKey() {
		return this.key;
	}

	public String toString() {
		return RESOURCE_BUNDLE.getString(getKey());
	}

}
