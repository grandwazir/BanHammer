package name.richardson.james.bukkit.banhammer;

import java.util.UUID;

import com.avaje.ebean.ValidationException;
import org.junit.Test;

import name.richardson.james.bukkit.utilities.persistence.DatabaseLoader;

import name.richardson.james.bukkit.banhammer.model.PlayerRecord;

public class PlayerRecordTest {

	private static final DatabaseLoader loader = TestDatabaseFactory.getSQLiteDatabaseLoader();

	@Test(expected = ValidationException.class)
	public void savingBlankRecordShouldFailValidation() {
		PlayerRecord record = new PlayerRecord();
		record.save();
	}

	@Test
	public void savingValidRecordShouldPassValidation() {
		createValidRecord().save();
	}

	public static PlayerRecord createValidRecord() {
		PlayerRecord record = new PlayerRecord();
		record.setName(UUID.randomUUID().toString());
		record.setId(UUID.randomUUID());
		return record;
	}



}
