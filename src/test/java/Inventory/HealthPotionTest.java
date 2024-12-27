package Inventory;

import Characters.Hero;
import Characters.Position;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class HealthPotionTest {

    /**
     * HealthPotionTest class contains unit tests for the `use` method in the
     * HealthPotion class. These tests handle various scenarios to ensure
     * proper functionality of healing functionality when used with a hero.
     */

    @Test
    void testUseAddsHealthToHero() {
        // Arrange
        Position position = new Position(1, 1);
        HealthPotion healthPotion = new HealthPotion("Small Health Potion", 50, 10, position);
        Hero hero = Mockito.mock(Hero.class);

        Mockito.when(hero.takeHealing(50)).thenReturn(true);

        // Act
        String result = healthPotion.use(hero);

        // Assert
        assertEquals("50 health points added to your hero!", result);
        Mockito.verify(hero).takeHealing(50);
    }

    @Test
    void testUseDoesNotAddHealthIfHeroCannotTakeHealing() {
        // Arrange
        Position position = new Position(2, 2);
        HealthPotion healthPotion = new HealthPotion("Medium Health Potion", 30, 15, position);
        Hero hero = Mockito.mock(Hero.class);

        Mockito.when(hero.takeHealing(30)).thenReturn(false);

        // Act
        String result = healthPotion.use(hero);

        // Assert
        assertNull(result);
        Mockito.verify(hero).takeHealing(30);
    }

    @Test
    void testUseWithZeroHealingPoints() {
        // Arrange
        Position position = new Position(3, 3);
        HealthPotion healthPotion = new HealthPotion("Zero Health Potion", 0, 5, position);
        Hero hero = Mockito.mock(Hero.class);

        Mockito.when(hero.takeHealing(0)).thenReturn(false);

        // Act
        String result = healthPotion.use(hero);

        // Assert
        assertNull(result);
        Mockito.verify(hero).takeHealing(0);
    }

    @Test
    void testUseThrowsExceptionWithNullHero() {
        // Arrange
        Position position = new Position(4, 4);
        HealthPotion healthPotion = new HealthPotion("Null Hero Potion", 20, 10, position);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> healthPotion.use(null));
    }
}