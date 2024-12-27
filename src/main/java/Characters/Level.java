package Characters;

/**
 * The Level class represents the leveling system of a character or entity,
 * managing their current level and experience points. It provides methods
 * to gain experience, handle level progression, and retrieve level-related
 * information.
 *
 * The Level follows a singleton factory approach for controlled instance creation.
 */
public class Level {
    private int level;
    private int experience;
    private static final int EXPIERENCE_FACTOR_LEVEL_INCREASE = 100;
    private static final int INITIAL_LEVEL = 1;
    private static final int INITIAL_EXPERIENCE = 0;

    private Level() {
        this.level = INITIAL_LEVEL;
        this.experience = INITIAL_EXPERIENCE;
    }

    public boolean gainExperience(int experience) {
        this.experience += experience;
        if (experience >= (level * EXPIERENCE_FACTOR_LEVEL_INCREASE)) {
            return levelUp();
        }

        return false;
    }

    public boolean levelUp() {
        level++;
        experience %= EXPIERENCE_FACTOR_LEVEL_INCREASE;
        return true;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public static Level getLevelInstance() {
        return new Level();
    }
}
