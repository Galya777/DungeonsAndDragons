package Characters;

import Inventory.Spell;
import Inventory.Weapon;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CharImplTest {

    @Test
    void attack_BothWeaponAndSpellNull_ReturnsBaseAttackPoints() {
        Stats mockedStats = mock(Stats.class);
        when(mockedStats.getAttack()).thenReturn(10);

        Position position = new Position(0, 0);
        CharImpl character = new CharImpl("Hero", "ID123", position, "sprite.png") {
            @Override
            public Stats getStats() {
                return mockedStats;
            }
        };

        int result = character.attack();

        assertEquals(10, result);
        verify(mockedStats, times(1)).getAttack();
    }

    @Test
    void attack_WeaponOnly_ReturnsAttackWithWeaponBonus() {
        Stats mockedStats = mock(Stats.class);
        when(mockedStats.getAttack()).thenReturn(10);

        Weapon mockedWeapon = mock(Weapon.class);
        when(mockedWeapon.getAttack()).thenReturn(5);

        Position position = new Position(0, 0);
        CharImpl character = new CharImpl("Hero", "ID123", position, "sprite.png") {
            @Override
            public Stats getStats() {
                return mockedStats;
            }
        };

        character.currentWeapon = mockedWeapon;

        int result = character.attack();

        assertEquals(15, result);
        verify(mockedStats, times(1)).getAttack();
        verify(mockedWeapon, times(1)).getAttack();
    }

    @Test
    void attack_SpellOnly_ReturnsAttackWithSpellBonusIfManaSufficient() {
        Stats mockedStats = mock(Stats.class);
        when(mockedStats.getAttack()).thenReturn(10);
        when(mockedStats.useMana(15)).thenReturn(true);

        Spell mockedSpell = mock(Spell.class);
        when(mockedSpell.getAttack()).thenReturn(20);
        when(mockedSpell.getManaCost()).thenReturn(15);

        Position position = new Position(0, 0);
        CharImpl character = new CharImpl("Hero", "ID123", position, "sprite.png") {
            @Override
            public Stats getStats() {
                return mockedStats;
            }
        };

        character.currentSpell = mockedSpell;

        int result = character.attack();

        assertEquals(30, result);
        verify(mockedStats, times(1)).getAttack();
        verify(mockedSpell, times(1)).getAttack();
        verify(mockedSpell, times(1)).getManaCost();
        verify(mockedStats, times(1)).useMana(15);
    }

    @Test
    void attack_SpellAndWeapon_SpellPreferredWhenManaSufficient() {
        Stats mockedStats = mock(Stats.class);
        when(mockedStats.getAttack()).thenReturn(10);
        when(mockedStats.useMana(15)).thenReturn(true);

        Spell mockedSpell = mock(Spell.class);
        when(mockedSpell.getAttack()).thenReturn(20);
        when(mockedSpell.getManaCost()).thenReturn(15);

        Weapon mockedWeapon = mock(Weapon.class);
        when(mockedWeapon.getAttack()).thenReturn(5);

        Position position = new Position(0, 0);
        CharImpl character = new CharImpl("Hero", "ID123", position, "sprite.png") {
            @Override
            public Stats getStats() {
                return mockedStats;
            }
        };

        character.currentWeapon = mockedWeapon;
        character.currentSpell = mockedSpell;

        int result = character.attack();

        assertEquals(30, result);
        verify(mockedStats, times(1)).getAttack();
        verify(mockedSpell, times(1)).getAttack();
        verify(mockedSpell, times(1)).getManaCost();
        verify(mockedWeapon, times(1)).getAttack();
        verify(mockedStats, times(1)).useMana(15);
    }

    @Test
    void attack_SpellAndWeapon_WeaponUsedWhenManaInsufficient() {
        Stats mockedStats = mock(Stats.class);
        when(mockedStats.getAttack()).thenReturn(10);
        when(mockedStats.useMana(15)).thenReturn(false);

        Spell mockedSpell = mock(Spell.class);
        when(mockedSpell.getAttack()).thenReturn(20);
        when(mockedSpell.getManaCost()).thenReturn(15);

        Weapon mockedWeapon = mock(Weapon.class);
        when(mockedWeapon.getAttack()).thenReturn(5);

        Position position = new Position(0, 0);
        CharImpl character = new CharImpl("Hero", "ID123", position, "sprite.png") {
            @Override
            public Stats getStats() {
                return mockedStats;
            }
        };

        character.currentWeapon = mockedWeapon;
        character.currentSpell = mockedSpell;

        int result = character.attack();

        assertEquals(15, result);
        verify(mockedStats, times(1)).getAttack();
        verify(mockedSpell, times(1)).getAttack();
        verify(mockedSpell, times(1)).getManaCost();
        verify(mockedWeapon, times(1)).getAttack();
        verify(mockedStats, times(1)).useMana(15);
    }
}