package name.richardson.james.bukkit.banhammer.model;

import name.richardson.james.bukkit.banhammer.comment.CommentRecord;
import name.richardson.james.bukkit.banhammer.player.PlayerRecord;

public class CommentRecordTest {

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
