package name.richardson.james.bukkit.banhammer;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import name.richardson.james.bukkit.utilities.persistence.DatabaseLoader;

@PrepareForTest(BanHammer.class)
public class BanRecordTest {

	private static DatabaseLoader loader = TestDatabaseFactory.getSQLiteDatabaseLoader();

	@Test
	public void createPermanentBanSuccessfully() {
		BanRecord ban = createBanRecord();
		ban.save();
	}

	private static BanRecord createBanRecord() {
		BanRecord ban = new BanRecord();
		PlayerRecord creator = PlayerRecordTest.createValidRecord();
		PlayerRecord player = PlayerRecordTest.createValidRecord();
		ban.setCreator(creator);
		ban.setPlayer(player);
		ban.setState(BanRecord.State.NORMAL);
		CommentRecord comment = new CommentRecord();
		comment.setCreator(creator);
		comment.setComment("Test");
		ban.setReason(comment);
		return ban;
	}

	@Test
	public void replaceBanReasonSuccessfully() {
		BanRecord record = createBanRecord();
		record.save();
		CommentRecord comment = new CommentRecord();
		comment.setCreator(record.getCreator());
		comment.setComment("Another comment");
		record.setReason(comment);
		record.save();
		Assert.assertEquals("Ban reason has not been replaced", comment.getComment(), record.getReason().getComment());
	}

	@Test
	public void addBanCommentSuccessfully() {
		BanRecord record = createBanRecord();
		CommentRecord comment = CommentRecordTest.CommentRecord("Yet another test comment.");
		comment.setType(CommentRecord.Type.NORMAL);
		record.addComment(comment);
		record.save();
		Assert.assertEquals("Additional ban comment has not been added!", 2, record.getComments().size());

	}




}
