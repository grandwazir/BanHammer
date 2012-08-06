/*******************************************************************************
 * Copyright (c) 2012 James Richardson.
 * 
 * MigratedSQLStorage.java is part of BanHammer.
 * 
 * BanHammer is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * BanHammer is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * BanHammer. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package name.richardson.james.bukkit.banhammer.migration;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.PersistenceException;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.persistence.BanRecord;
import name.richardson.james.bukkit.banhammer.persistence.PlayerRecord;
import name.richardson.james.bukkit.utilities.configuration.DatabaseConfiguration;
import name.richardson.james.bukkit.utilities.persistence.SQLStorage;

public class MigratedSQLStorage extends SQLStorage {

  /** The legacy records to migrate */
  private List<OldBanRecord> legacyRecords;

  /**
   * Instantiates a new migrated sql storage.
   *
   * @param plugin the plugin
   */
  public MigratedSQLStorage(final BanHammer plugin, DatabaseConfiguration configuration, List<Class<?>> classes) {
    super(plugin, configuration, classes);
  }

  /* (non-Javadoc)
   * @see name.richardson.james.bukkit.utilities.persistence.SQLStorage#afterDatabaseCreate()
   */
  @Override
  protected void afterDatabaseCreate() {
    if (!this.legacyRecords.isEmpty()) {
      int migrated = 0;
      for (final OldBanRecord ban : this.legacyRecords) {
        this.migrateRecord(ban);
        migrated++;
      }
      this.getLogger().info(this, "records-migrated", migrated);
    }
  }

  /* (non-Javadoc)
   * @see name.richardson.james.bukkit.utilities.persistence.SQLStorage#beforeDatabaseDrop()
   */
  @Override
  protected void beforeDatabaseDrop() {
    try {
      this.legacyRecords = this.getEbeanServer().find(OldBanRecord.class).findList();
      this.getLogger().warning(this, "records-to-migrate", this.legacyRecords.size());
    } catch (final PersistenceException exception) {
      exception.printStackTrace();
      this.legacyRecords = new LinkedList<OldBanRecord>();
    }
  }
  
  protected void beforeDatabaseCreate() {
    this.getClasses().remove(OldBanRecord.class);
  }

  /**
   * Migrate a legacy record to the new format.
   *
   * @param record the record to migrate
   */
  private void migrateRecord(final OldBanRecord record) {
    /** Get the various database records required */
    final PlayerRecord playerRecord = this.getPlayerRecord(record.getPlayer());
    final PlayerRecord creatorRecord = this.getPlayerRecord(record.getCreatedBy());
    final BanRecord banRecord = new BanRecord();
    /** Set the specifics of the ban */
    banRecord.setCreatedAt(new Timestamp(record.getCreatedAt()));
    banRecord.setPlayer(playerRecord);
    banRecord.setCreator(creatorRecord);
    banRecord.setReason(record.getReason());
    if (record.getExpiresAt() != 0) banRecord.setExpiresAt(new Timestamp(record.getExpiresAt()));
    banRecord.setState(BanRecord.State.NORMAL);
    /** Save records */
    final Object[] records = { playerRecord, creatorRecord, banRecord };
    this.getEbeanServer().save(Arrays.asList(records));
  }
  
  /**
   * Gets the database record for a player.
   * 
   * @param playerName the player name
   * @return the player record
   */
  private PlayerRecord getPlayerRecord(String playerName) {
    if (!PlayerRecord.exists(this.getEbeanServer(), playerName)) {
      PlayerRecord playerRecord = new PlayerRecord();
      playerRecord.setName(playerName);
      this.getEbeanServer().save(playerRecord);
      return PlayerRecord.find(this.getEbeanServer(), playerName);
    } else {
      return PlayerRecord.find(this.getEbeanServer(), playerName);
    }
  }

}
