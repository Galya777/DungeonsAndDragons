package Characters;

/**
 * A utility class responsible for creating instances of different types of game characters
 * based on the provided parameters. This class includes a factory method to generate objects
 * of type {@link java.lang.Character}, specifically {@link Hero} or {@link Minion}.
 *
 * The Factory ensures that the required arguments for character creation are properly validated
 * and throws an exception if invalid arguments are encountered.
 *
 * Key Responsibilities:
 * - Creates and returns an instance of a {@link java.lang.Character} (Hero or Minion).
 * - Validates arguments to ensure non-null values for character attributes.
 * - Assigns the appropriate image resource path for the created character.
 */
public class Factory {
    private final static String INVALID_ARGUMENTS = "Illegal arguments: cannot be null";

    public static Character getActor(CharType actorType, String name, String id, Position position) {

        if (actorType == null || name == null || id == null || position == null) {
            throw new IllegalArgumentException(INVALID_ARGUMENTS);
        }

        if (actorType.equals(CharType.HERO)) {
            return new Hero(name, id, position, "images/mainChar2.png"); //change to the image
        }

        return new Minion(name, id, position, "images/minion.png"); //change to the image

    }

}
