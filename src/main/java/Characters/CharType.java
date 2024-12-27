package Characters;

/**
 * The CharType enum represents the two main types of characters in the game: HERO and MINION.
 *
 * HERO:
 *   - Represents the primary character controlled by the player, typically with advanced abilities
 *     such as equipping weapons, learning spells, collecting treasures, and progressing in levels.
 *   - Instances of HERO extend the CharImpl base behavior and encapsulate game mechanics like health, mana, and combat actions.
 *
 * MINION:
 *   - Represents non-player characters or entities typically engaged as adversaries or supporting characters in the game.
 *   - These characters usually have simpler mechanics compared to HERO characters.
 */
public enum CharType {
    HERO, MINION
}
