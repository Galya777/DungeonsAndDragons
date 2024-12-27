package Characters;

import Characters.Character;
import Characters.GamePanel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GamePanelTest {

    /**
     * Test class for the GamePanel class.
     * <p>
     * The GamePanel class is responsible for managing a list of game objects
     * and rendering them onto the panel. The addGameObject method adds a new
     * Character object into the list of gameObjects.
     */

    @Test
    public void testAddGameObject_SingleObjectAddedSuccessfully() {
        // Arrange
        GamePanel gamePanel = new GamePanel();
        Character mockCharacter = Mockito.mock(Character.class);

        // Act
        gamePanel.addGameObject(mockCharacter);

        // Assert
        List<Character> gameObjects = getGameObjects(gamePanel);
        assertEquals(1, gameObjects.size());
        assertEquals(mockCharacter, gameObjects.get(0));
    }

    @Test
    public void testAddGameObject_MultipleObjectsAddedSuccessfully() {
        // Arrange
        GamePanel gamePanel = new GamePanel();
        Character mockCharacter1 = Mockito.mock(Character.class);
        Character mockCharacter2 = Mockito.mock(Character.class);

        // Act
        gamePanel.addGameObject(mockCharacter1);
        gamePanel.addGameObject(mockCharacter2);

        // Assert
        List<Character> gameObjects = getGameObjects(gamePanel);
        assertEquals(2, gameObjects.size());
        assertEquals(mockCharacter1, gameObjects.get(0));
        assertEquals(mockCharacter2, gameObjects.get(1));
    }

    @Test
    public void testAddGameObject_NullObjectAdded() {
        // Arrange
        GamePanel gamePanel = new GamePanel();

        // Act
        gamePanel.addGameObject(null);

        // Assert
        List<Character> gameObjects = getGameObjects(gamePanel);
        assertEquals(1, gameObjects.size());
        assertEquals(null, gameObjects.get(0));
    }

    // Utility method to directly get the private gameObjects list from GamePanel
    @SuppressWarnings("unchecked")
    private List<Character> getGameObjects(GamePanel gamePanel) {
        try {
            java.lang.reflect.Field field = GamePanel.class.getDeclaredField("gameObjects");
            field.setAccessible(true);
            return (List<Character>) field.get(gamePanel);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access gameObjects field", e);
        }
    }
}