package Inventory;

import Characters.Hero;
import Characters.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PotionTest {

    // Description:
    // The PotionTest class contains unit tests for the `heal` method in the Potion class.
    // The `heal` method returns the number of points the potion heals when used.

    private static class TestPotion extends Potion {
        protected TestPotion(String name, int points, int experience, Position position) {
            super(name, points, experience, position);
        }

        @Override
        public void setPosition(Position position) {
            this.position = position;
        }

        @Override
        public String use(Hero hero) {
            return "Test potion used by hero.";
        }

        @Override
        public String getTreasureStats() {
            return "Name: " + name + ", Points: " + points + ", Experience: " + experience;
        }
    }

    @Test
    void testHealReturnsCorrectPoints() {
        // Arrange
        String name = "Health Potion";
        int points = 50;
        int experience = 10;
        Position positionMock = mock(Position.class);
        Potion potion = new TestPotion(name, points, experience, positionMock);

        // Act
        int result = potion.heal();

        // Assert
        assertEquals(50, result);
    }

    @Test
    void testHealReturnsZeroWhenPointsAreZero() {
        // Arrange
        String name = "Weak Potion";
        int points = 0;
        int experience = 5;
        Position positionMock = mock(Position.class);
        Potion potion = new TestPotion(name, points, experience, positionMock);

        // Act
        int result = potion.heal();

        // Assert
        assertEquals(0, result);
    }

    @Test
    void testHealReturnsNegativePointsWhenSpecified() {
        // Arrange
        String name = "Cursed Potion";
        int points = -10;
        int experience = 20;
        Position positionMock = mock(Position.class);
        Potion potion = new TestPotion(name, points, experience, positionMock);

        // Act
        int result = potion.heal();

        // Assert
        assertEquals(-10, result);
    }

    @Test
    void testHealWorksForHighPointValues() {
        // Arrange
        String name = "Mega Potion";
        int points = Integer.MAX_VALUE;
        int experience = 100;
        Position positionMock = mock(Position.class);
        Potion potion = new TestPotion(name, points, experience, positionMock);

        // Act
        int result = potion.heal();

        // Assert
        assertEquals(Integer.MAX_VALUE, result);
    }
}