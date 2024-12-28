package graphicScenes;

import Characters.*;
import Inventory.HealthPotion;
import Inventory.Spell;
import Inventory.Treasure;
import Inventory.Weapon;
import actions.PlayerRepository;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The MapGenerator class is responsible for generating and managing the game map,
 * including its components such as enemies, treasures, the hero, and free positions.
 * This class provides an interface to interact with the map, retrieve and manage
 * its content, and handle the movement of entities within the game world.
 * It also supports dynamic updates to the map based on the game's state.
 *
 * Fields:
 * - ROWS: The number of rows in the game map grid.
 * - COLS: The number of columns in the game map grid.
 * - CELL_SIZE: The size of each individual cell in the grid.
 * - map: A 2D string array representing the game map.
 * - wallImage: The image path or reference used for representing wall elements in the map.
 * - pathImage: The image path or reference used for representing paths in the map.
 * - enemyImage: The image path or reference for representing enemies on the map.
 * - treasureImage: The image path or reference for representing treasures on the map.
 * - heroImage: The image path or reference for representing the hero on the map.
 * - hero: The hero currently on the map.
 * - enemies: A collection of Minion objects currently present on the map.
 * - treasures: A collection of Treasure objects currently present on the map.
 * - freePositions: A set of positions that are currently unoccupied and available for movement or placement.
 * - enemyTimer: A timer used for managing enemy movements.
 * - ENEMY_MOVE_INTERVAL: The interval used to control the timing of enemy movements.
 */
public class MapGenerator extends JPanel {
    private static final int ROWS = 16;
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
    private Timer enemyTimer; // Timer to handle enemy movement
    private static final int ENEMY_MOVE_INTERVAL = 2000; // 2 seconds between moves

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

        startEnemyMovement(); // Start enemy movement
    }

    public MapGenerator() {
        this.map = new String[ROWS][COLS];
        this.enemies = new ArrayList<>();
        this.treasures = new ArrayList<>();
        this.freePositions = new HashSet<>();
        this.hero = null;

        initializeImages();
        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));

        generateRandomMap();
        startEnemyMovement(); // Start enemy movement

    }
    public Hero getHeroBySocketChannel(SocketChannel socketChannel) {
        // Assuming you have a PlayerRepository instance in the MapGenerator class
        PlayerRepository playerRepository = PlayerRepository.getInstance();
        return playerRepository.getHeroByGivenSocketChannel(socketChannel);
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
    public List<Position> getEnemyPositions() {
        //Return a list of enemy positions
        List<Position> enemyPositions = new ArrayList<>();
        for (Minion enemy : enemies) {
            enemyPositions.add(enemy.getPosition());
        }
        return enemyPositions;
    }
    public Treasure getTreasureAtPosition(Position position) {
        return treasures.stream().filter(treasure -> treasure.getPosition().equals(position)).findFirst().orElse(null);
    }
    public void updateEnemyPositions() {
        for (Minion minion : enemies) {
            int newX = (int) (Math.random() * ROWS);
            int newY = (int) (Math.random() * COLS);
            minion.setPosition(newX, newY);
        }
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
    
        // New method to move a single enemy randomly
        private void moveEnemyRandomly(Minion enemy) {
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Up, Down, Left, Right
            Random random = new Random();
            
            synchronized(this) {
            for (int i = 0; i < 4; i++) { // Try all directions
                int[] direction = directions[random.nextInt(directions.length)];
                int newRow = enemy.getPosition().getRow() + direction[0];
                int newCol = enemy.getPosition().getCol() + direction[1];
                
                if (newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS 
                        && Objects.equals(map[newRow][newCol], ".")) {
                    // Move the enemy
                    map[enemy.getPosition().getRow()][enemy.getPosition().getCol()] = ".";
                    enemy.setPosition(newRow, newCol);
                    map[newRow][newCol] = "M";
                    break;
                }
            }
            }
        }
    
        // New method to move all enemies
        public void moveAllEnemies() {
            synchronized(this) {
            for (Minion enemy : enemies) {
                moveEnemyRandomly(enemy);
            }
            repaint(); // Redraw the map after moving enemies
            }
        }
        public synchronized void updateMap(String fullMap) {
            // Parse the map rows from the `fullMap` string
            String[] rows = fullMap.split("\n");
            for (int i = 0; i < ROWS && i < rows.length; i++) {
                String row = rows[i];
                for (int j = 0; j < COLS && j < row.length(); j++) {
                    char cell = row.charAt(j);
                    map[i][j] = String.valueOf(cell); // Update the map cell
                }
            }
    
            repaint(); // Redraw the map
        }
        public void removeFreePosition(Position position) {
            freePositions.remove(position);
        }
    
        public boolean mapHasMinionAtPosition(Position position) {
            String cellContent = map[position.getRow()][position.getCol()];
            return cellContent != null && cellContent.contains("M");
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
            for (Minion minion : enemies) {
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
            // Initialize map with walls
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    map[i][j] = "#"; // Fill the map with walls
                }
            }
    
            // Use recursive backtracking to carve out the labyrinth
            Random random = new Random();
            carvePath(1, 1, random); // Start from (1, 1)
    
            // Ensure there is an entrance and an exit
            map[1][0] = "."; // Entrance
            freePositions.add(new Position(1, 0));
            map[ROWS - 2][COLS - 1] = "."; // Exit
            freePositions.add(new Position(ROWS - 2, COLS - 1));
    
            // Place enemies and treasures
            placeEnemies(10, random); // Adjust the enemy count as needed
            placeTreasure(20, random); // Adjust the treasure count as needed
    
            // Redraw the map
            repaint();
        }
    
        // Helper to recursively carve the path
        private void carvePath(int row, int col, Random random) {
            // Possible directions to move: up, down, left, right
            int[][] directions = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
            Collections.shuffle(Arrays.asList(directions), random); // Randomize directions
    
            for (int[] direction : directions) {
                int newRow = row + direction[0];
                int newCol = col + direction[1];
    
                // Ensure the new cell is within bounds and unvisited (still a wall)
                if (newRow > 0 && newRow < ROWS - 1 && newCol > 0 && newCol < COLS - 1 && Objects.equals(map[newRow][newCol], "#")) {
                    // Carve the path
                    map[newRow][newCol] = ".";
                    map[row + direction[0] / 2][col + direction[1] / 2] = "."; // Carve the connecting cell
                    freePositions.add(new Position(newRow, newCol)); // Mark as free position
                    freePositions.add(new Position(row + direction[0] / 2, col + direction[1] / 2));
    
                    // Recursively carve paths from the new cell
                    carvePath(newRow, newCol, random);
                }
            }
        }
    
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
    
            // Draw the map (existing logic)
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
    
            // Draw enemies
            for (Minion enemy : enemies) {
                g.drawImage(enemyImage, enemy.getPosition().getCol() * CELL_SIZE, enemy.getPosition().getRow() * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
            }
    
            // Draw treasures
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
            MapGenerator mapGenerator = new MapGenerator(map, enemies, treasures, freePositions, hero);
            //mapGenerator.startEnemyMovement(); // Start the enemy movement timer for the new game
            return mapGenerator;
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
    
            map[newPosition.getRow()][newPosition.getCol()] = "T";
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
    
            stopEnemyMovement(); // Stop enemy movement if a new hero is set
            repaint(); // Redraw the map
        }
    
        public Hero getHero() {
            return hero;
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
    
        public void moveHero(String command) {
            if (hero == null) {
                throw new IllegalStateException("Hero is not set in the MapGenerator.");
            }
    
            Position currentPos = hero.getPosition(); // Get the current position of the hero
            Position newPos = null;
    
            switch (command) {
                case "MOVE UP":
                    newPos = Position.createNewPosition(currentPos.getRow() - 1, currentPos.getCol());
                    break;
                case "MOVE DOWN":
                    newPos = Position.createNewPosition(currentPos.getRow() + 1, currentPos.getCol());
                    break;
                case "MOVE LEFT":
                    newPos = Position.createNewPosition(currentPos.getRow(), currentPos.getCol() - 1);
                    break;
                case "MOVE RIGHT":
                    newPos = Position.createNewPosition(currentPos.getRow(), currentPos.getCol() + 1);
                    break;
                default:
                    System.out.println("Invalid movement command: " + command);
                    return;
            }
    
            // Ensure the new position is valid (within bounds and not on a wall)
            if (newPos != null) {
                int newRow = newPos.getRow();
                int newCol = newPos.getCol();
    
                if (newRow < 0 || newRow >= map.length || newCol < 0 || newCol >= map[0].length || Objects.equals(map[newRow][newCol], "#")) {
                    System.out.println("Invalid movement: Cannot move to a wall or out of bounds.");
                    return;
                }
                updatePlayerPosition(currentPos, newPos, hero); // Update the hero's position
                checkGameOver(); // Verify if the game is over after moving the hero
            }
            // Removed direct enemy movement calls from `PlayerMoving`
        }
        public synchronized void moveEnemies(DataOutputStream out) {
            Random random = new Random();
            List<Minion> toRemove = new ArrayList<>(); // Track minions to remove
            int visionRange = 5; // How far enemies can "see" the hero
    
            for (Minion enemy : enemies) {
                Position currentPos = enemy.getPosition();
                Position newPos = null;
                int maxTries = 4; // Give up after 4 failed attempts
                int tries = 0;
    
                System.out.println("Processing enemy at position: " + currentPos); // Debug message
    
                // Check if hero is within vision range
                if (hero != null && isWithinRange(enemy.getPosition(), hero.getPosition(), visionRange)) {
                    newPos = moveTowardsHero(enemy, hero.getPosition());
                } else {
                    int newRow = currentPos.getRow();
                    int newCol = currentPos.getCol();
                    // Random movement if hero is not in range
                    while (tries < maxTries && newPos == null) {
                        int direction = random.nextInt(4); // Randomly pick a direction
                        switch (direction) {
                            case 0 -> newRow -= 1; // UP
                            case 1 -> newRow += 1; // DOWN
                            case 2 -> newCol -= 1; // LEFT
                            case 3 -> newCol += 1; // RIGHT
                        }
    
                        // Validate new position is within bounds
                        if (newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS) {
                            System.out.println("Checking position (" + newRow + ", " + newCol + ")"); // Debug message
    
                            // Check if the hero is at the new position
                            if ("H".equals(map[newRow][newCol])) {
                                hero.decreaseHealth(10); // Reduce hero's health
                                System.out.println("Hero was attacked! Health: " + hero.getHealth());
                                toRemove.add(enemy); // Minion dies after attacking
                                newPos = currentPos; // Minion doesn't move after dying
                            } else if (".".equals(map[newRow][newCol])) {
                                newPos = Position.createNewPosition(newRow, newCol); // Valid move
                            }
                        }
    
                        tries++;
                    }
                }
    
                // If a valid position was found, move the enemy
                if (newPos != null && !toRemove.contains(enemy)) {
                    map[currentPos.getRow()][currentPos.getCol()] = "."; // Clear old position
                    map[newPos.getRow()][newPos.getCol()] = "M"; // Mark new position as enemy
                    enemy.setPosition(newPos.getRow(), newPos.getCol()); // Update enemy position
                    System.out.println("Enemy moved to: " + newPos); // Debug message
                }
            }
    
            // Remove enemies that collided with the hero
            for (Minion deadEnemy : toRemove) {
                enemies.remove(deadEnemy); // Remove from the list
                Position pos = deadEnemy.getPosition();
                map[pos.getRow()][pos.getCol()] = "."; // Clear map position
                System.out.println("Minion killed at position: " + pos);
            }
    
            // **NEW CODE: Broadcast the updated enemy positions to clients**
            sendEnemyPositionsToClients(out);
    
            repaint(); // Refresh the map display to show enemy movements
        }
        public JScrollPane getScrollableMap() {
            JScrollPane scrollPane = new JScrollPane(this);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            return scrollPane;
        }
        private void sendEnemyPositionsToClients(DataOutputStream out) {
            try {
                StringBuilder enemyPositions = new StringBuilder();
    
                // Gather all enemy positions
                for (Minion enemy : enemies) {
                    Position pos = enemy.getPosition();
                    enemyPositions.append(pos.getRow()).append(",").append(pos.getCol()).append(";");
                }
    
                // Send the positions as a single message
                out.writeUTF("ENEMY_POSITIONS " + enemyPositions.toString());
                out.flush();
    
                System.out.println("Broadcast enemy positions: " + enemyPositions);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to send enemy positions to client.");
            }
        }
        // Method to start enemy movement
        public void startEnemyMovement() {
            enemyTimer = new Timer();
            enemyTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    moveAllEnemies();
                }
            }, 0, ENEMY_MOVE_INTERVAL);
        }
    
        // Method to stop enemy movement
        public void stopEnemyMovement() {
            if (enemyTimer != null) {
                enemyTimer.cancel();
                enemyTimer = null;
            }
        }


        // Method to check if the game is over
        public void checkGameOver() {
            if (!hero.isAlive()) {
                System.out.println("Game Over! Hero has died.");
                stopEnemyMovement(); // Stop enemy movement timer
                // Trigger additional game over logic/dialogs
                System.exit(0); // Exit the game (optional)
            }
        }

        private boolean isWithinRange(Position pos1, Position pos2, int range) {
            int rowDiff = Math.abs(pos1.getRow() - pos2.getRow());
            int colDiff = Math.abs(pos1.getCol() - pos2.getCol());
            return rowDiff <= range && colDiff <= range;
        }
    
        private Position moveTowardsHero(Minion enemy, Position heroPos) {
            Position enemyPos = enemy.getPosition();
            int rowDiff = heroPos.getRow() - enemyPos.getRow();
            int colDiff = heroPos.getCol() - enemyPos.getCol();
            
            // Determine the direction to move
            int newRow = enemyPos.getRow();
            int newCol = enemyPos.getCol();
            
            if (Math.abs(rowDiff) > Math.abs(colDiff)) {
                newRow += Integer.compare(rowDiff, 0);
            } else {
                newCol += Integer.compare(colDiff, 0);
            }
            
            // Check if the new position is valid
            if (isValidMove(newRow, newCol)) {
                return new Position(newRow, newCol);
            }
            return null;
        }
    
        private boolean isValidMove(int row, int col) {
            // Check bounds
            if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
                return false;
            }
            
            // Check if the position is walkable (not a wall or other obstacle)
            return map[row][col].equals(".");
        }

}
