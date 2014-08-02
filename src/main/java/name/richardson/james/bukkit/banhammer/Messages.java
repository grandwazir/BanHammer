package name.richardson.james.bukkit.banhammer;

import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.utilities.localisation.ColouredMessage;
import name.richardson.james.bukkit.utilities.localisation.MessageType;
import name.richardson.james.bukkit.utilities.localisation.PluralCount;

public interface Messages {

	String allOptionArgumentDescription();

	String allOptionArgumentId();

	String allOptionArgumentName();

	String auditCommandDescription();

	String auditCommandName();

	@ColouredMessage(type = MessageType.NOTICE) String auditExpiredBanPercentage(int expiredBanCount, float expiredBanCountPercentage);

	@ColouredMessage(type = MessageType.NOTICE) String auditNormalBanPercentage(int normalBanCount, float normalBanCountPercentage);

	@ColouredMessage(type = MessageType.NOTICE) String auditPardonedBanPercentage(int pardonedBanCount, float pardonedBanCountPercentage);

	@ColouredMessage(type = MessageType.NOTICE) String auditPermanentBanPercentage(int permanentBanCount, float pardonedBanCountPercentage);

	@ColouredMessage(type = MessageType.HEADER) String auditSummary(@PluralCount int totalBanCount, String playerName, float totalBanCountPercentage);

	String auditSummaryAll();

	@ColouredMessage(type = MessageType.HEADER) String auditSummaryStatus();

	@ColouredMessage(type = MessageType.HEADER) String auditSummaryType();

	@ColouredMessage(type = MessageType.NOTICE) String auditTemporaryBanPercentage(int temporaryBanCount, float temporaryBanCountPercentage);

	String banCommandDescription();

	String banCommandName();

	String banCountArgumentDescription();

	String banCountArgumentId();

	String banCountArgumentName();

	@ColouredMessage(type = MessageType.NOTICE) String banExpiresAt(String duration);

	@ColouredMessage(type = MessageType.NOTICE)
	String banLength(String duration);

	@ColouredMessage(type = MessageType.NOTICE)
	String banReason(String reason);

	@ColouredMessage(type = MessageType.HEADER) String banSummary(String creatorName, String playerName, String creationDate);

	String bansExported(@PluralCount int size);

	@ColouredMessage(type = MessageType.HEADER)
	String bansPurged(@PluralCount int count);

	String checkCommandDescription();

	String checkCommandName();

	String defaultImportReason();

	String defaultKickReason();

	String exportCommandDescription();

	String exportCommandName();

	String historyCommandDescription();

	String historyCommandName();

	String importCommandDescription();

	String importCommandName();

	String kickCommandDescription();

	String kickCommandName();

	String limitCommandDescription();

	String limitCommandName();

	@ColouredMessage(type = MessageType.HEADER) String limitsFound(@PluralCount int size);

	@ColouredMessage(type = MessageType.NOTICE)
	String noBansMade();

	@ColouredMessage(type = MessageType.ERROR) String noPermissionToAuditAllBans();

	@ColouredMessage(type = MessageType.ERROR) String notAllowedToAuditThatPlayer(String playerName);

	String pardonCommandDescription();

	String pardonCommandName();

	String playerArgumentDescription();

	String playerArgumentId();

	String playerArgumentInvalid();

	String playerArgumentName();

	@ColouredMessage(type = MessageType.NOTICE) String playerBanned(String playerName);

	@ColouredMessage(type = MessageType.NOTICE) String playerBannedBy(String target, String creator);

	@ColouredMessage(type = MessageType.ERROR) String playerBannedPermanently(String reason, String creator);

	@ColouredMessage(type = MessageType.ERROR) String playerBannedTemporarily(String reason, String creator, String bannedUntil);

	@ColouredMessage(type = MessageType.NOTICE) String playerHasMadeNoBans();

	@ColouredMessage(type = MessageType.WARNING) String playerIsAlreadyBanned(String playerName);

	@ColouredMessage(type = MessageType.ERROR) String playerIsImmune(String playerName);

	@ColouredMessage(type = MessageType.NOTICE) String playerKicked(String name);

	@ColouredMessage(type = MessageType.NOTICE) String playerKickedBy(String target, String kickedBy);

	@ColouredMessage(type = MessageType.ERROR) String playerKickedNotification(String reason, String creatorName);

	@ColouredMessage(type = MessageType.ERROR) String playerLookupException();

	String playerNameArgumentDescription();

	String playerNameArgumentError();

	String playerNameArgumentId();

	String playerNameArgumentName();

	@ColouredMessage(type = MessageType.WARNING) String playerNeverBanned(String playerName);

	@ColouredMessage(type = MessageType.NOTICE) String playerNotBanned(String playerName);

	@ColouredMessage(type = MessageType.NOTICE) String playerPardoned(String playerName);

	@ColouredMessage(type = MessageType.NOTICE) String playerPardonedBy(String target, CommandSender pardonedBy);

	@ColouredMessage(type = MessageType.NOTICE) String bansImported(@PluralCount int size);

	String purgeCommandDescription();

	String purgeCommandName();

	String reasonArgumentDescription();

	String reasonArgumentId();

	String reasonArgumentInvalid();

	String reasonArgumentName();

	String recentCommandDescription();

	String recentCommandName();

	String silentArgumentDescription();

	String silentArgumentId();

	String silentArgumentName();

	String timeArgumentDescription();

	String timeArgumentId();

	String timeArgumentName();

	@ColouredMessage(type = MessageType.ERROR) String unableToBanForThatLong(String playerName);

	@ColouredMessage(type = MessageType.WARNING) String unableToPardonPlayer(String playerName);

	String undoCommandDescription();

	String undoCommandName();

	@ColouredMessage(type = MessageType.NOTICE)
	String undoComplete(String creatorName, String playerName);

	@ColouredMessage(type = MessageType.ERROR)
	String undoNotPermitted(String name);

	@ColouredMessage(type = MessageType.ERROR)
	String undoTimeExpired();

}
