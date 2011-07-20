
package name.richardson.james.banhammer.exceptions;

public class PlayerAlreadyBanned extends Exception {

  private static final long serialVersionUID = 430445873851296870L;
  private String playerName;

  public PlayerAlreadyBanned(String playerName) {
    this.playerName = playerName;
  }

  public String getPlayerName() {
    return playerName;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

}
