/**
 * The base class for users of the system, storing basic user information.
 * Other user types like Employee and Customer will extend this class.
 */
public class User {
    /** The name of the user. */
    private String name;

    /**
     * Constructs a new User with a given name.
     *
     * @param name The name of the user.
     */
    public User(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the user.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }
}