package name.richardson.james.bukkit.banhammer.utilities.localisation;

import name.richardson.james.bukkit.utilities.localisation.PluginLocalisation;

public interface BanHammerLocalisation extends PluginLocalisation {

	public static final String PLAYER_HAS_MADE_NO_BANS = "shared.player-has-made-no-bans";

	public static final String ARGUMENT_BANCOUNT_ID = "argument.bancount.id";
	public static final String ARGUMENT_BANCOUNT_NAME = "argument.bancount.name";
	public static final String ARGUMENT_BANCOUNT_DESC = "argument.bancount.desc";

	public static final String ARGUMENT_SILENT_ID = "argument.silent.id";
	public static final String ARGUMENT_SILENT_NAME = "argument.silent.name";
	public static final String ARGUMENT_SILENT_DESC = "argument.silent.desc";

	public static final String ARGUMENT_TIME_ID = "argument.time.id";
	public static final String ARGUMENT_TIME_NAME = "argument.time.name";
	public static final String ARGUMENT_TIME_DESC = "argument.time.desc";

	public static final String ARGUMENT_PLAYER_ID = "argument.player.id";
	public static final String ARGUMENT_PLAYER_NAME = "argument.player.name";
	public static final String ARGUMENT_PLAYER_DESC = "argument.player.desc";
	public static final String ARGUMENT_PLAYER_ERROR = "argument.player.error";
	public static final String ARGUMENT_PLAYER_NAME_ERROR = "argument.player-name.error";



	public static final String ARGUMENT_REASON_ID = "argument.reason.id";
	public static final String ARGUMENT_REASON_NAME = "argument.reason.name";
	public static final String ARGUMENT_REASON_DESC = "argument.reason.desc";
	public static final String ARGUMENT_REASON_ERROR = "argument.reason.error";



	public static final String AUDIT_COMMAND_HEADER = "auditcommand.header";
	public static final String AUDIT_COMMAND_NAME = "command.audit.name";
	public static final String AUDIT_COMMAND_DESC = "command.audit.desc";
	public static final String AUDIT_TYPE_SUMMARY = "auditcommand.type-summary";
	public static final String AUDIT_PERMANENT_BANS_PERCENTAGE = "auditcommand.permanent-bans-percentage";
	public static final String AUDIT_TEMPORARY_BANS_PERCENTAGE = "auditcommand.temporary-bans-percentage";
	public static final String AUDIT_STATUS_SUMMARY = "auditcommand.status-summary";
	public static final String AUDIT_ACTIVE_BANS_PERCENTAGE = "auditcommand.active-bans-percentage";
	public static final String AUDIT_EXPIRED_BANS_PERCENTAGE = "auditcommand.expired-bans-percentage";
	public static final String AUDIT_PARDONED_BANS_PERCENTAGE = "auditcommand.pardoned-bans-percentage";

	public static final String BANCOMMAND_NAME = "command.ban.name";
	public static final String BANCOMMAND_DESC = "command.ban.desc";

	public static final String CHECK_COMMAND_NAME = "command.check.name";
	public static final String CHECK_COMMAND_DESC = "command.check.desc";

	public static final String EXPORT_COMMAND_NAME = "command.export.name";
	public static final String EXPORT_COMMAND_DESC = "command.export.desc";

	public static final String HISTORY_COMMAND_NAME = "command.history.name";
	public static final String HISTORY_COMMAND_DESC = "command.history.desc";

	public static final String IMPORT_COMMAND_NAME = "command.import.name";
	public static final String IMPORT_COMMAND_DESC = "command.import.desc";

	public static final String PLAYER_BANNED = "bancommand.player-banned";
	public static final String PLAYER_NOT_BANNED = "shared.player-is-not-banned";
	public static final String BAN_PLAYER_IS_ALREADY_BANNED = "bancommand.player-is-already-banned";
	public static final String PLUGIN_UNABLE_TO_HOOK_ALIAS = "alias.unable-to-hook-alias";
	public static final String EXPORT_SUMMARY = "exportcommand.summary";
	public static final String PLAYER_NEVER_BEEN_BANNED = "shared.player-has-never-been-banned";

	public static final String IMPORT_SUMMARY = "importcommand.summary";
	public static final String IMPORT_DEFAULT_REASON = "importcommand.default-reason";

	public static final String KICK_COMMAND_NAME = "command.kick.name";
	public static final String KICK_COMMAND_DESC = "command.kick.desc";

	public static final String KICK_PLAYER_NOTIFICATION = "kickcommand.player-notification";
	public static final String KICK_SENDER_NOTIFICATION = "kickcommand.sender-notification";
	public static final String KICK_PLAYER_KICKED = "kickcommand.player-kicked-from-server";
	public static final String KICK_DEFAULT_REASON = "kickcommand.default-reason";

	public static final String LIMIT_COMMAND_NAME = "command.limits.name";
	public static final String LIMIT_COMMAND_DESC = "command.limits.desc";

	public static final String LIMIT_SUMMARY = "limitscommand.summary";
	public static final String LIMIT_ENTRY = "limitscommand.entry";

	public static final String PARDON_COMMAND_NAME = "command.pardon.name";
	public static final String PARDON_COMMAND_DESC = "command.pardon.desc";


	public static final String PARDON_PLAYER = "pardoncommand.player";
	public static final String PARDON_UNABLE_TO_TARGET_PLAYER = "pardoncommand.unable-to-target-player";


	public static final String PURGE_COMMAND_NAME = "command.purge.name";
	public static final String PURGE_COMMAND_DESC = "command.purge.desc";

	public static final String PURGE_SUMMARY = "purgecommand.summary";

	public static final String RECENT_COMMAND_NAME = "command.recent.name";
	public static final String RECENT_COMMAND_DESC = "command.recent.desc";


	public static final String RECENT_NO_BANS = "recentcommand.no-bans";
	public static final String UNDO_COMPLETE = "undocommand.complete";

	public static final String UNDO_COMMAND_NAME = "command.undo.name";
	public static final String UNDO_COMMAND_DESC = "command.undo.desc";

	public static final String UNDO_NOT_PERMITTED = "undocommand.not-permitted";
	public static final String UNDO_TIME_EXPIRED = "undocommand.time-expired";
	public static final String ALIAS_BAN_REASON = "alias.ban-reason";
	public static final String LISTENER_PLAYER_BANNED_TEMPORARILY = "listener.player-banned-temporarily";
	public static final String LISTENER_PLAYER_BANNED_PERMANENTLY = "listener.player-banned-permanently";
	public static final String NOTIFY_PLAYER_BANNED = "notifier.player-banned";
	public static final String NOTIFY_PLAYER_PARDONED = "notifier.player-pardoned";

	public static final String FORMATTER_SUMMARY = "formatter.summary";
	public static final String FORMATTER_REASON = "formatter.reason";
	public static final String FORMATTER_LENGTH = "formatter.length";
	public static final String FORMATTER_PERMANENT = "formatter.permanent";
	public static final String FORMATTER_EXPIRES_AT = "formatter.expires-at";

	public static final String CHOICE_NO_LIMITS = "choice.no-limits";
	public static final String CHOICE_ONE_LIMIT = "choice.one-limit";
	public static final String CHOICE_MANY_LIMITS = "choice.many-limits";

	public static final String CHOICE_NO_BANS = "choice.no-bans";
	public static final String CHOICE_ONE_BAN = "choice.one-ban";
	public static final String CHOICE_MANY_BANS = "choice.many-bans";


}
