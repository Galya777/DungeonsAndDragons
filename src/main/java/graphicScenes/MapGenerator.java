package graphicScenes;

import Characters.*;
import Inventory.HealthPotion;
import Inventory.Spell;
import Inventory.Treasure;
import Inventory.Weapon;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


public class MapGenerator extends JPanel {
    private static final int ROWS = 20;
    private static final int COLS = 35;
    private static final int CELL_SIZE = 50;

    private String[][] map; // Dungeon map representation
    private Image wallImage; // Wall image
    private Image pathImage; // Path image
    private Image enemyImage, treasureImage;
    private Hero hero; // Hero to be displayed on the map
    private Image heroImage; // Hero image

    private ArrayList<Minion> enemies;
    private ArrayList<Treasure> treasures;
    private Set<Position> freePositions;

    public MapGenerator(String[][] map, ArrayList<Minion> enemies, ArrayList<Treasure> treasures, Set<Position> freePositions, Hero hero) {
        if (map == null || enemies == null || treasures == null || freePositions == null) {
            throw new IllegalArgumentException("Invalid arguments provided to MapGenerator.");
        }

        this.map = map;
        this.enemies = enemies;
        this.treasures = treasures;
        this.freePositions = freePositions;
        this.hero = hero; // Set the hero

        if (hero != null) {
            Position startPosition = getRandomFreePosition(new Random()); // Get a random free position
            if (startPosition != null) {
                hero.setPosition(startPosition.getRow(), startPosition.getCol()); // Update the hero's position
                map[startPosition.getRow()][startPosition.getCol()] = "H"; // Place the hero on the map
            }
        }
        initializeImages();
        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
    }

    public MapGenerator() {
        this.map = new String[ROWS][COLS];
        this.enemies = new ArrayList<>();
        this.treasures = new ArrayList<>();
        this.freePositions = new HashSet<>();
        this.hero=null;

        initializeImages();
        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));

        generateRandomMap();
    }

    private void initializeImages() {
        wallImage = new ImageIcon("images/obstacle.png").getImage();
        pathImage = new ImageIcon("images/empty.png").getImage();
        enemyImage = new ImageIcon("images/minion.png").getImage();
        treasureImage = new ImageIcon("images/treasure.png").getImage();
        heroImage = new ImageIcon("images/mainChar2.png").getImage();
    }

    public String[][] getMap() {
        return map;
    }

    public Set<Position> getFreePositions() {
        return freePositions;
    }

    public ArrayList<Minion> getEnemies() {
        return enemies;
    }

    public ArrayList<Treasure> getTreasures() {
        return treasures;
    }

    public Minion getMinionAtPosition(Position position) {
        return enemies.stream().filter(minion -> minion.getPosition().equals(position)).findFirst().orElse(null);
    }

    public Treasure getTreasureAtPosition(Position position) {
        return treasures.stream().filter(treasure -> treasure.getPosition().equals(position)).findFirst().orElse(null);
    }

    public int getmapCols() {
        return map[0].length;
    }

    public int getmapRows() {
        return map.length;
    }

    public String getAtPosition(int x, int y) {
        return map[x][y];
    }

    public void setContentAtPosition(Position position, String update) {
        map[position.getRow()][position.getCol()] = update;
    }

    public Position getFreePosition() {
        if (freePositions.isEmpty()) {
            return null;
        }
        Position freePosition = freePositions.iterator().next();
        freePositions.remove(freePosition);
        return freePosition;
    }

    public void removeFreePosition(Position position) {
        freePositions.remove(position);
    }

    public boolean mapHasMinionAtPosition(Position position) {
        return Objects.equals(map[position.getRow()][position.getCol()], "M");
    }

    public void updateDeadHeroAtPosition(Position position, String idToBeRemoved, Treasure treasure) {
        int row = position.getRow();
        int col = position.getCol();
        if (map[row][col].contains("M")) {
            map[row][col] = ".";
            insertTreasureAtFreePosition(treasure);
            return;
        }

        String stayingHeroId = getStayingHeroId(idToBeRemoved, row, col);
        if (treasure == null) {
            map[row][col] = "." + stayingHeroId + ".";
            return;
        }
        treasure.setPosition(position);
        updateNewTreasureAtPosition(treasure, row, col, stayingHeroId);

    }
    public void setHeroPosition(Position heroPosition) {
        if (heroPosition == null) {
            throw new IllegalArgumentException("Hero position cannot be null.");
        }
        int row = heroPosition.getRow();
        int col = heroPosition.getCol();
        if (map[row][col].equals("#")) {
            throw new IllegalArgumentException("Cannot place hero on a wall.");
        }
        map[row][col] = "H"; // Mark the position with the hero's identifier
        freePositions.remove(heroPosition); // Remove this position from free positions
        repaint(); // Refresh the map display
    }
    public void updateDeadMinionAtPosition(Position position, String update) {
        Position newPosition = getFreePosition();
        if (newPosition == null) {
            return;
        }
        for(Minion minion : enemies) {
            if (minion.getPosition().equals(position)) {
                minion.revive(newPosition);
                minion.setPosition(newPosition.getRow(), newPosition.getCol());
                map[newPosition.getRow()][newPosition.getCol()] = update;
            }
        }
    }

    public String getStayingHeroId(String idToBeRemoved, int row, int col) {
        char id = map[row][col].endsWith(idToBeRemoved) ? map[row][col].charAt(0)
                : map[row][col].charAt(2);
        return String.valueOf(id);
    }

    private void placeEnemies(int count, Random random) {
        enemies.clear();
        while (enemies.size() < count) {
            Position position = getRandomFreePosition(random);
            if (position != null) {
                Minion minion = (Minion) Factory.getActor(CharType.MINION, "minion" + enemies.size(), String.valueOf(enemies.size()), position);
                enemies.add(minion);
                map[position.getRow()][position.getCol()] = "M";
            }
        }
    }

    private void placeTreasure(int count, Random random) {
        treasures.clear();
        while (treasures.size() < count) {
            Position position = getRandomFreePosition(random);
            if (position != null) {
                Treasure treasure = createRandomTreasure(position, random);
                treasures.add(treasure);
                map[position.getRow()][position.getCol()] = "T";
            }
        }
    }

    private Treasure createRandomTreasure(Position position, Random random) {
        int type = random.nextInt(3);
        return switch (type) {
            case 0 -> HealthPotion.createHealthPotion("HealthPotion", 20, 10, position);
            case 1 -> Weapon.createWeapon("SilverSword", 1, 20, 10, position);
            case 2 -> Spell.createSpell("FireSpell", 1, 30, 10, 10, position);
            default -> null;
        };
    }

    public Position getRandomFreePosition(Random random) {
        if (freePositions.isEmpty()) {
            return null;
        }
        List<Position> freePositionList = new ArrayList<>(freePositions);
        Position randomPosition = freePositionList.get(random.nextInt(freePositionList.size()));
        freePositions.remove(randomPosition);
        return randomPosition;
    }

    public void generateRandomMap() {
        Random random = new Random();

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                map[i][j] = String.valueOf('#');
            }
        }

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (random.nextBoolean()) {
                    map[i][j] = String.valueOf('.');
                    freePositions.add(new Position(i, j));
                }
            }
        }

        placeEnemies(10, random);
        placeTreasure(20, random);


        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int x = j * CELL_SIZE;
                int y = i * CELL_SIZE;

                if (Objects.equals(map[i][j], "#")) {
                    g.drawImage(wallImage, x, y, CELL_SIZE, CELL_SIZE, this);
                } else if (Objects.equals(map[i][j], ".")) {
                    g.drawImage(pathImage, x, y, CELL_SIZE, CELL_SIZE, this);
                }
            }
        }

        for (Minion enemy : enemies) {
            g.drawImage(enemyImage, enemy.getPosition().getCol() * CELL_SIZE, enemy.getPosition().getRow() * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
        }

        for (Treasure treasure : treasures) {
            g.drawImage(treasureImage, treasure.getPosition().getCol() * CELL_SIZE, treasure.getPosition().getRow() * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
        }
        // Draw the hero
        if (hero != null) {
            g.drawImage(heroImage, hero.getPosition().getCol() * CELL_SIZE, hero.getPosition().getRow() * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
        }
    }

    public static MapGenerator createNewGameRepository(String[][] map, ArrayList<Minion> enemies, ArrayList<Treasure> treasures, Set<Position> freePositions, Hero hero) {
        if (map == null || enemies.isEmpty() || treasures.isEmpty() || freePositions == null) {
            throw new IllegalArgumentException("Invalid arguments provided to createNewGameRepository.");
        }
        return new MapGenerator(map, enemies, treasures, freePositions, hero);
    }

    public void updateNewTreasureAtPosition(Treasure treasure, int row, int col, String heroId) {
        if (treasure != null) {
            map[row][col] = heroId;
            treasures.add(treasure);
        }
    }

    public void updateTakenTreasureAtPosition(Position position, String update) {
        treasures.removeIf(treasure -> treasure.getPosition().equals(position));
        map[position.getRow()][position.getCol()] = update;
    }

    public void updateThrownTreasure(Position position, String heroId, Treasure treasure) {
        int row = position.getRow();
        int col = position.getCol();
        if (Objects.equals(map[row][col], ".")) {
            insertTreasureAtFreePosition(treasure);
            return;
        }

        updateNewTreasureAtPosition(treasure, row, col, heroId);

    }

    private void insertTreasureAtFreePosition(Treasure treasure) {
        if (treasure == null) {
            return;
        }

        Position newPosition = getFreePosition();
        if (newPosition == null) {
            return;
        }

        map[newPosition.getRow()][newPosition.getCol()] ="T";
        treasures.add(treasure);
    }
    public void updateMovingHeroFromPosition(Position position, String idToBeRemoved) {
        int row = position.getRow();
        int col = position.getCol();
        if (map[row][col].contains("T")) {
            map[row][col] = "T .";
        } else if (map[row][col].contains("M")) {
            map[row][col] = "M .";
        } else if (map[row][col].endsWith(".")) {
            map[row][col] = ". .";
            freePositions.add(Position.createNewPosition(row, col));
        } else {
            String otherHeroId = getStayingHeroId(idToBeRemoved, row, col);
            map[row][col] = "." + otherHeroId + ".";
        }

    }
    public void updatePlayerPosition(Position oldPosition, Position newPosition, Hero hero) {
        if (oldPosition == null || newPosition == null || hero == null) {
            throw new IllegalArgumentException("Invalid arguments for updating player position.");
        }

        int oldRow = oldPosition.getRow();
        int oldCol = oldPosition.getCol();
        int newRow = newPosition.getRow();
        int newCol = newPosition.getCol();

        // Ensure the new position is valid and not a wall
        if (newRow < 0 || newRow >= ROWS || newCol < 0 || newCol >= COLS || Objects.equals(map[newRow][newCol], "#")) {
            return; // Invalid move
        }

        // Update the map to reflect the player's movement
        map[oldRow][oldCol] = "."; // Mark the old position as empty
        map[newRow][newCol] = hero.getId(); // Mark the new position with the hero's ID

        // Update hero's position
        hero.setPosition(newRow, newCol);

        repaint(); // Refresh the map display
    }
    public void setHero(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("Hero cannot be null.");
        }

        this.hero = hero; // Set the new hero
        Position startPosition = getRandomFreePosition(new Random()); // Get a random free position
        if (startPosition != null) {
            hero.setPosition(startPosition.getRow(), startPosition.getCol()); // Update the hero's position
            map[startPosition.getRow()][startPosition.getCol()] = "H"; // Place the hero on the map
        }

        repaint(); // Redraw the map
    }
    public Position getRandomFreePosition() {
        List<Position> freePositions = new ArrayList<>();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                if (Objects.equals(map[row][col], ".")) { // Assuming null indicates a free spot
                    freePositions.add(new Position(row, col));
                }
            }
        }

        if (freePositions.isEmpty()) {
            return null; // No free positions available
        }

        // Pick a random free position
        Random random = new Random();
        return freePositions.get(random.nextInt(freePositions.size()));
    }
}
