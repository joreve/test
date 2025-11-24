/**
 * The base class for users of the system, storing basic user information.
 * Other user types like Employee and Customer will extend this class.
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
     * Returns the name of the user.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name The new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the username of the user.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The new username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password of the user.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password The new password.
     */
    public void setPassword(String password) {
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
}