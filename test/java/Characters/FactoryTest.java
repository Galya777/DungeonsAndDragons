package Characters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mockito;

class FactoryTest {

    /**
     * Test class for the Factory class.
     * <p>
     * Verifies the behavior of the getActor method, which creates and returns
     * a specific implementation of the Character interface based on the input parameters.
     */

    @Test
    void testGetActorWithHeroType() {
        // Arrange
        CharType actorType = CharType.HERO;
        String name = "HeroName";
        String id = "hero001";
        Position position = Mockito.mock(Position.class);

        // Act
        Character result = Factory.getActor(actorType, name, id, position);

        // Assert
        assertNotNull(result);
        assertEquals("HeroName", result.getName());
        assertEquals("hero001", result.getId());
        assertEquals(position, result.getPosition());
        assertTrue(result instanceof Hero);
    }

    @Test
    void testGetActorWithMinionType() {
        // Arrange
        CharType actorType = CharType.MINION;
        String name = "MinionName";
        String id = "minion001";
        Position position = Mockito.mock(Position.class);

        // Act
        Character result = Factory.getActor(actorType, name, id, position);

        // Assert
        assertNotNull(result);
        assertEquals("MinionName", result.getName());
        assertEquals("minion001", result.getId());
        assertEquals(position, result.getPosition());
        assertTrue(result instanceof Minion);
    }

    @Test
    void testGetActorThrowsExceptionForNullActorType() {
        // Arrange
        CharType actorType = null;
        String name = "TestName";
        String id = "test001";
        Position position = Mockito.mock(Position.class);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Factory.getActor(actorType, name, id, position)
        );
        assertEquals("Illegal arguments: cannot be null", exception.getMessage());
    }

    @Test
    void testGetActorThrowsExceptionForNullName() {
        // Arrange
        CharType actorType = CharType.HERO;
        String name = null;
        String id = "test001";
        Position position = Mockito.mock(Position.class);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Factory.getActor(actorType, name, id, position)
        );
        assertEquals("Illegal arguments: cannot be null", exception.getMessage());
    }

    @Test
    void testGetActorThrowsExceptionForNullId() {
        // Arrange
        CharType actorType = CharType.MINION;
        String name = "TestName";
        String id = null;
        Position position = Mockito.mock(Position.class);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Factory.getActor(actorType, name, id, position)
        );
        assertEquals("Illegal arguments: cannot be null", exception.getMessage());
    }

    @Test
    void testGetActorThrowsExceptionForNullPosition() {
        // Arrange
        CharType actorType = CharType.HERO;
        String name = "TestName";
        String id = "test001";
        Position position = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Factory.getActor(actorType, name, id, position)
        );
        assertEquals("Illegal arguments: cannot be null", exception.getMessage());
    }
}