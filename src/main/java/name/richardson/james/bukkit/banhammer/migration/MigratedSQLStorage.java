package name.richardson.james.bukkit.banhammer.migration;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.PersistenceException;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.OldBanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.internals.Logger;
import name.richardson.james.bukkit.utilities.persistence.SQLStorage;

public class MigratedSQLStorage extends SQLStorage {

  private final Logger logger = new Logger(this.getClass());
  
  private List<OldBanRecord> legacyRecords;
  
  public MigratedSQLStorage(BanHammer plugin) {
    super(plugin);
  }
  
  @SuppressWarnings("unchecked")
  protected void beforeDatabaseDrop() {
    try {
      legacyRecords = (List<OldBanRecord>) this.list(OldBanRecord.class);
      logger.warning(this.getFormattedBanMigrationCount("records-to-migrate", legacyRecords.size()));
    } catch (PersistenceException exception) {
      legacyRecords = new LinkedList<OldBanRecord>();
    }
  }
  
  protected void afterDatabaseCreate() {
    if (!legacyRecords.isEmpty()) {
      int migrated = 0;
      for (OldBanRecord ban : legacyRecords) {
        this.migrateRecord(ban);
        migrated++;
      }
      logger.info(this.getFormattedBanMigrationCount("records-migrated", migrated));
    }
  }
  
  
  private void migrateRecord(final OldBanRecord record) {
    /** Get the various database records required */
    final PlayerRecord playerRecord = PlayerRecord.find(this, record.getPlayer());
    final PlayerRecord creatorRecord = PlayerRecord.find(this, record.getCreatedBy());
    final BanRecord banRecord = new BanRecord();
    /** Set the specifics of the ban */
    banRecord.setCreatedAt(new Timestamp(record.getCreatedAt()));
    banRecord.setPlayer(playerRecord);
    banRecord.setCreator(creatorRecord);
    banRecord.setReason(record.getReason());
    banRecord.setExpiresAt(new Timestamp(record.getExpiresAt()));
    banRecord.setState(BanRecord.State.NORMAL);
    /** Save records */
    Object[] records = {playerRecord, creatorRecord, banRecord};
    this.getEbeanServer().save(records);
  }
  
  private String getFormattedBanMigrationCount(String key, final int count) {
    final Object[] arguments = { count };
    final double[] limits = { 0, 1, 2 };
    final String[] formats = { this.getMessage("no-bans"), this.getMessage("one-ban"), this.getMessage("many-bans") };
    return this.getChoiceFormattedMessage(key, arguments, formats, limits);
  }

  
  
}
