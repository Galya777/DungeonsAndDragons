package Characters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelTest {

    /**
     * This class tests the methods of the Level class, specifically the behavior of the levelUp method,
     * which is triggered when enough experience points are gained to push a character to the next level.
     */

    @Test
    void shouldLevelUpWhenCalled() {
        // Arrange
        Level level = Level.getLevelInstance();
        int initialLevel = level.getLevel();

        // Act
        boolean leveledUp = level.levelUp();

        // Assert
        assertTrue(leveledUp, "levelUp should return true when successfully leveling up");
        assertEquals(initialLevel + 1, level.getLevel(), "The level should increase by 1 after leveling up");
    }

    @Test
    void shouldResetExperienceToRemainderAfterLevelUp() {
        // Arrange
        Level level = Level.getLevelInstance();
        level.gainExperience(150); // Add enough experience to trigger a level up
        int expectedExperience = 150 % 100; // Expect the remainder to be stored after level up

        // Act
        boolean leveledUp = level.levelUp();

        // Assert
        assertTrue(leveledUp, "levelUp should return true when successfully leveling up");
        assertEquals(expectedExperience, level.getExperience(), "Experience should reset to the remainder after level up");
    }
}