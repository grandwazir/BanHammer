package name.richardson.james.banhammer.exceptions;


public class UnableToCreateConfigurationException extends Exception {

  private String path;

  public UnableToCreateConfigurationException(String path) {
    this.setPath(path);
  }

  public void setPath(String path) {
    this.path = path;
  }


  public String getPath() {
    return path;
  }


  private static final long serialVersionUID = 1L;
  

}
