package Characters;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HeroTest {

    @Test
    void testTakeHealingWhenHeroIsAliveHealthIncreased() {
        // Arrange
        Stats statsMock = mock(Stats.class);
        when(statsMock.isAlive()).thenReturn(true);
        when(statsMock.increaseHealth(50)).thenReturn(true);

        Hero hero = new Hero("TestHero", "hero123", mock(Position.class), "path/to/image");
        hero.stats = statsMock;

        // Act
        boolean result = hero.takeHealing(50);

        // Assert
        assertTrue(result);
        verify(statsMock).increaseHealth(50);
    }

    @Test
    void testTakeHealingWhenHeroIsDeadReturnsFalse() {
        // Arrange
        Stats statsMock = mock(Stats.class);
        when(statsMock.isAlive()).thenReturn(false);

        Hero hero = new Hero("TestHero", "hero123", mock(Position.class), "path/to/image");
        hero.stats = statsMock;

        // Act
        boolean result = hero.takeHealing(50);

        // Assert
        assertFalse(result);
        verify(statsMock, never()).increaseHealth(anyInt());
    }

    @Test
    void testTakeHealingWithNegativeHealthPointsReturnsFalse() {
        // Arrange
        Stats statsMock = mock(Stats.class);
        when(statsMock.isAlive()).thenReturn(true);

        Hero hero = new Hero("TestHero", "hero123", mock(Position.class), "path/to/image");
        hero.stats = statsMock;

        // Act
        boolean result = hero.takeHealing(-10);

        // Assert
        assertFalse(result);
        verify(statsMock, never()).increaseHealth(anyInt());
    }

    @Test
    void testTakeHealingWithZeroHealthPointsReturnsTrue() {
        // Arrange
        Stats statsMock = mock(Stats.class);
        when(statsMock.isAlive()).thenReturn(true);
        when(statsMock.increaseHealth(0)).thenReturn(true);

        Hero hero = new Hero("TestHero", "hero123", mock(Position.class), "path/to/image");
        hero.stats = statsMock;

        // Act
        boolean result = hero.takeHealing(0);

        // Assert
        assertTrue(result);
        verify(statsMock).increaseHealth(0);
    }
}