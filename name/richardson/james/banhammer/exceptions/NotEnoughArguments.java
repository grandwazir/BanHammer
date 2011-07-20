
package name.richardson.james.banhammer.exceptions;

public class NotEnoughArguments extends Exception {

  private static final long serialVersionUID = 430445873851296870L;
  private String command;
  private String usage;

  public NotEnoughArguments(String command, String usage) {
    this.setUsage(usage);
    this.setCommand(command);
  }

  public String getCommand() {
    return command;
  }

  public String getUsage() {
    return usage;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public void setUsage(String usage) {
    this.usage = usage;
  }

}
