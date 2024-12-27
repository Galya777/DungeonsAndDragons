package graphicScenes;

import Characters.Hero;
import Characters.Position;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MapGeneratorTest {

    @Test
    void testMoveHeroMoveUp() {
        // Arrange
        String[][] map = new String[20][35];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 35; j++) {
                map[i][j] = ".";
            }
        }
        Set<Position> freePositions = new HashSet<>();
        freePositions.add(new Position(1, 1));
        Hero hero = new Hero("HeroName", "HeroID", new Position(1, 1), "path/to/image.png");
        hero.setPosition(1, 1);
        map[1][1] = "H";
        MapGenerator mapGenerator = new MapGenerator(map, new ArrayList<>(), new ArrayList<>(), freePositions, hero);

        // Act
        mapGenerator.moveHero("MOVE UP");

        // Assert
        assertEquals("H", map[0][1]);
        assertEquals(".", map[1][1]);
        assertEquals(new Position(0, 1), hero.getPosition());
    }

    @Test
    void testMoveHeroMoveDown() {
        // Arrange
        String[][] map = new String[20][35];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 35; j++) {
                map[i][j] = ".";
            }
        }
        Set<Position> freePositions = new HashSet<>();
        freePositions.add(new Position(1, 1));
        Hero hero = new Hero("DefaultHero", "DefaultID", new Position(1, 1), "path/to/default_image.png");
        hero.setPosition(1, 1);
        map[1][1] = "H";
        MapGenerator mapGenerator = new MapGenerator(map, new ArrayList<>(), new ArrayList<>(), freePositions, hero);

        // Act
        mapGenerator.moveHero("MOVE DOWN");

        // Assert
        assertEquals("H", map[2][1]);
        assertEquals(".", map[1][1]);
        assertEquals(new Position(2, 1), hero.getPosition());
    }

    @Test
    void testMoveHeroMoveLeft() {
        // Arrange
        String[][] map = new String[20][35];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 35; j++) {
                map[i][j] = ".";
            }
        }
        Set<Position> freePositions = new HashSet<>();
        freePositions.add(new Position(1, 1));
        Hero hero = new Hero("HeroName", "HeroID", new Position(0, 0), "path/to/image.png");
        hero.setPosition(1, 1);
        map[1][1] = "H";
        MapGenerator mapGenerator = new MapGenerator(map, new ArrayList<>(), new ArrayList<>(), freePositions, hero);

        // Act
        mapGenerator.moveHero("MOVE LEFT");

        // Assert
        assertEquals("H", map[1][0]);
        assertEquals(".", map[1][1]);
        assertEquals(new Position(1, 0), hero.getPosition());
    }

    @Test
    void testMoveHeroMoveRight() {
        // Arrange
        String[][] map = new String[20][35];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 35; j++) {
                map[i][j] = ".";
            }
        }
        Set<Position> freePositions = new HashSet<>();
        freePositions.add(new Position(1, 1));
        Hero hero = new Hero("DefaultHero", "DefaultID", new Position(1, 1), "path/to/default_image.png");
        hero.setPosition(1, 1);
        map[1][1] = "H";
        MapGenerator mapGenerator = new MapGenerator(map, new ArrayList<>(), new ArrayList<>(), freePositions, hero);

        // Act
        mapGenerator.moveHero("MOVE RIGHT");

        // Assert
        assertEquals("H", map[1][2]);
        assertEquals(".", map[1][1]);
        assertEquals(new Position(1, 2), hero.getPosition());
    }

    @Test
    void testMoveHeroBlockedByWall() {
        // Arrange
        String[][] map = new String[20][35];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 35; j++) {
                map[i][j] = ".";
            }
        }
        map[0][1] = "#"; // Wall
        Set<Position> freePositions = new HashSet<>();
        freePositions.add(new Position(1, 1));
        Hero hero = new Hero("DefaultHero", "DefaultID", new Position(1, 1), "path/to/default_image.png");
        hero.setPosition(1, 1);
        map[1][1] = "H";
        MapGenerator mapGenerator = new MapGenerator(map, new ArrayList<>(), new ArrayList<>(), freePositions, hero);

        // Act
        mapGenerator.moveHero("MOVE UP");

        // Assert
        assertEquals("#", map[0][1]);
        assertEquals("H", map[1][1]);
        assertEquals(new Position(1, 1), hero.getPosition());
    }

    @Test
    void testMoveHeroOutOfBounds() {
        // Arrange
        String[][] map = new String[20][35];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 35; j++) {
                map[i][j] = ".";
            }
        }
        Set<Position> freePositions = new HashSet<>();
        freePositions.add(new Position(0, 0));
        Hero hero = new Hero("DefaultHero", "DefaultID", new Position(0, 0), "path/to/default_image.png");
        hero.setPosition(0, 0);
        map[0][0] = "H";
        MapGenerator mapGenerator = new MapGenerator(map, new ArrayList<>(), new ArrayList<>(), freePositions, hero);

        // Act
        mapGenerator.moveHero("MOVE UP");

        // Assert
        assertEquals("H", map[0][0]);
        assertEquals(new Position(0, 0), hero.getPosition());
    }
}