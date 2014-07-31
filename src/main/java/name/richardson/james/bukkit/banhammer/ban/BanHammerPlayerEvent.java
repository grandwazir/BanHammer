package name.richardson.james.bukkit.banhammer.ban;

import java.util.Set;

import org.bukkit.command.CommandSender;

public interface BanHammerPlayerEvent {

	public Set<BanRecord> getRecords();

	public boolean isSilent();

	public CommandSender getCommandSender();

}
