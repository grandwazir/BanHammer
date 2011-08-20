
package name.richardson.james.banhammer.exceptions;

public class PlayerAlreadyBannedException extends Exception {

  private static final long serialVersionUID = 430445873851296870L;
  private String playerName;

  public PlayerAlreadyBannedException(String playerName) {
    this.playerName = playerName;
  }

  public String getPlayerName() {
    return this.playerName;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

}
