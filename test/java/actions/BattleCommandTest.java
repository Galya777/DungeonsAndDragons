// BattleCommandTest.java

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BattleCommandTest {

    @Test
    public void testBattle_HeroWins() {
        // Arrange
        BattleCommand battleCommand = new BattleCommand();
        Hero hero = new Hero();
        Enemy enemy = new Enemy();

        // Act
        boolean result = battleCommand.battle(hero, enemy);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testBattle_EnemyWins() {
        // Arrange
        BattleCommand battleCommand = new BattleCommand();
        Hero hero = new Hero();
        Enemy enemy = new Enemy();

        // Act
        boolean result = battleCommand.battle(hero, enemy);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testBattle_Draw() {
        // Arrange
        BattleCommand battleCommand = new BattleCommand();
        Hero hero = new Hero();
        Enemy enemy = new Enemy();

        // Act
        boolean result = battleCommand.battle(hero, enemy);

        // Assert
        assertFalse(result);
    }
}