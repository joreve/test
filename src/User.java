/**
 * The base class for users of the system, storing basic user information.
 * Other user types like Employee and Customer will extend this class.
 * 
 * @author Joreve P. De Jesus
 */
public class User {
    private String name;
    private String username;
    private String password;

    /**
     * Constructs a new User with a given name.
     *
     * @param name The name of the user.
     */
    public User(String name) {
        this.name = name;
        this.username = null;
        this.password = null;
    }

    /**
     * Constructs a new User with name, username, and password.
     *
     * @param name The name of the user.
     * @param username The username for login.
     * @param password The password for login.
     */
    public User(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    /**
     * Validates the provided password against the stored password.
     *
     * @param inputPassword The password to validate.
     * @return true if the password matches, false otherwise.
     */
    public boolean validatePassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}