package name.richardson.james.bukkit.banhammer.ban;

import java.util.Set;

import org.bukkit.command.CommandSender;

import name.richardson.james.bukkit.banhammer.BanRecord;

public interface BanHammerPlayerEvent {

	public Set<BanRecord> getRecords();

	public boolean isSilent();

	public CommandSender getCommandSender();

}
