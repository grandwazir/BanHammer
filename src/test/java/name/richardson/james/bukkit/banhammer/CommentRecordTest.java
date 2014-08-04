package name.richardson.james.bukkit.banhammer;

import name.richardson.james.bukkit.utilities.persistence.DatabaseLoader;

import name.richardson.james.bukkit.banhammer.model.CommentRecord;
import name.richardson.james.bukkit.banhammer.model.PlayerRecord;

public class CommentRecordTest {

	private static final DatabaseLoader loader = TestDatabaseFactory.getSQLiteDatabaseLoader();

	public static CommentRecord CommentRecord(String comment) {
		PlayerRecord player = PlayerRecordTest.createValidRecord();
		return createRecord(player, comment);
	}

	public static CommentRecord createRecord(PlayerRecord playerRecord, String comment) {
		CommentRecord record = new CommentRecord();
		record.setCreator(playerRecord);
		record.setComment(comment);
		return record;
	}

}
