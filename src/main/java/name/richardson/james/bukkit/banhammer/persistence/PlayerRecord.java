package name.richardson.james.bukkit.banhammer.persistence;

import java.util.List;

public interface PlayerRecord {

  public long getId();
  
  public void setId(long id);
  
  public String getName();
  
  public void setName(String name);
  
  public List<BanRecord> getBans();
  
  public void setBans(List<BanRecord> records);
  
  public List<BanRecord> getCreatedBans();
  
  public void setCreatedBans(List<BanRecord> records);
  
  public boolean isBanned();
  
}
