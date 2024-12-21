package Characters;

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
