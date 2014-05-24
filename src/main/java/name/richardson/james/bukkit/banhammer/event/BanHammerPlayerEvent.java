package name.richardson.james.bukkit.banhammer.event;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

import name.richardson.james.bukkit.banhammer.record.BanRecord;

public interface BanHammerPlayerEvent {

	public Set<BanRecord> getRecords();

	public boolean isSilent();

	public CommandSender getCommandSender();

}
