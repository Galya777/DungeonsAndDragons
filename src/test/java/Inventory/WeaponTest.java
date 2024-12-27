package Inventory;

import Characters.Hero;
import Characters.Position;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class WeaponTest {

    /**
     * Test class for the Weapon class's `use` method.
     * The `use` method attempts to equip the weapon onto the provided hero.
     * It returns a success message if the hero equipped the weapon successfully,
     * otherwise it returns a message indicating the hero was under-leveled.
     */

    @Test
    public void testUse_SuccessfullyEquipsWeapon() {
        // Arrange
        Position position = mock(Position.class);
        Weapon weapon = new Weapon("Excalibur", 5, 50, 100, position);
        Hero hero = mock(Hero.class);

        when(hero.equip(weapon)).thenReturn(true);

        // Act
        String result = weapon.use(hero);

        // Assert
        assertEquals("Equiped with weapon Excalibur! Attack points: 50", result);
        verify(hero, times(1)).equip(weapon);
    }

    @Test
    public void testUse_HeroLevelTooLow() {
        // Arrange
        Position position = mock(Position.class);
        Weapon weapon = new Weapon("Excalibur", 5, 50, 100, position);
        Hero hero = mock(Hero.class);

        when(hero.equip(weapon)).thenReturn(false);

        // Act
        String result = weapon.use(hero);

        // Assert
        assertEquals("You need to be level 5 to use this weapon.", result);
        verify(hero, times(1)).equip(weapon);
    }
}