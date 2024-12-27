package Inventory;

import Inventory.Spell;
import Characters.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpellTest {

    /**
     * Test class for the `createSpell` method in the `Spell` class.
     * This method is responsible for creating new `Spell` instances with valid attributes.
     * It throws an IllegalArgumentException if any arguments are invalid.
     */

    @Test
    void testCreateSpellWithValidArguments() {
        // Arrange
        String name = "Fireball";
        int level = 5;
        int damage = 50;
        int attack = 30;
        int experience = 100;
        Position position = new Position(3, 4);

        // Act
        Spell spell = Spell.createSpell(name, level, damage, attack, experience, position);

        // Assert
        assertNotNull(spell);
        assertEquals(name, spell.getName());
        assertEquals(level, spell.getLevel());
        assertEquals(damage, spell.getManaCost());
        assertEquals(attack, spell.getAttack());
        assertEquals(experience, spell.giveExperienceWhenCollected());
        assertEquals(position, spell.getPosition());
    }

    @Test
    void testCreateSpellThrowsExceptionForNullName() {
        // Arrange
        String name = null;
        int level = 5;
        int damage = 50;
        int attack = 30;
        int experience = 100;
        Position position = new Position(3, 4);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Spell.createSpell(name, level, damage, attack, experience, position);
        });
        assertEquals("Invalid arguments.", exception.getMessage());
    }

    @Test
    void testCreateSpellThrowsExceptionForNonPositiveLevel() {
        // Arrange
        String name = "Fireball";
        int level = 0;
        int damage = 50;
        int attack = 30;
        int experience = 100;
        Position position = new Position(3, 4);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Spell.createSpell(name, level, damage, attack, experience, position);
        });
        assertEquals("Invalid arguments.", exception.getMessage());
    }

    @Test
    void testCreateSpellThrowsExceptionForNonPositiveDamage() {
        // Arrange
        String name = "Fireball";
        int level = 5;
        int damage = 0;
        int attack = 30;
        int experience = 100;
        Position position = new Position(3, 4);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Spell.createSpell(name, level, damage, attack, experience, position);
        });
        assertEquals("Invalid arguments.", exception.getMessage());
    }

    @Test
    void testCreateSpellThrowsExceptionForNonPositiveAttack() {
        // Arrange
        String name = "Fireball";
        int level = 5;
        int damage = 50;
        int attack = 0;
        int experience = 100;
        Position position = new Position(3, 4);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Spell.createSpell(name, level, damage, attack, experience, position);
        });
        assertEquals("Invalid arguments.", exception.getMessage());
    }

    @Test
    void testCreateSpellThrowsExceptionForNonPositiveExperience() {
        // Arrange
        String name = "Fireball";
        int level = 5;
        int damage = 50;
        int attack = 30;
        int experience = 0;
        Position position = new Position(3, 4);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Spell.createSpell(name, level, damage, attack, experience, position);
        });
        assertEquals("Invalid arguments.", exception.getMessage());
    }
}