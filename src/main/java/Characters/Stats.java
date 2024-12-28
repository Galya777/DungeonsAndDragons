package Characters;


/**
 * The Stats class represents the core attributes of a character, such as health, mana, attack, and defense.
 * It provides functionality to manipulate these attributes based on various scenarios, such as taking damage,
 * using resources, leveling up, or healing.
 *
 * The Stats class uses initial constants to define the default values for attributes and handles
 * attribute scaling with level changes or specified increments. It ensures attributes stay within
 * defined limits and maintains the character's overall state, such as whether they are alive.
 *
 * This class uses a singleton factory design pattern allowing controlled instance creation through
 * a dedicated static method.
 */
public class Stats {

    private int health;
    private int mana;
    private int attack;
    private int defense;
    private double criticalHitChance;
    private double dodgeChance;
    private final static int INITIAL_HEALTH = 100;
    private final static int INITIAL_MANA = 100;
    private final static int INITIAL_ATTACK = 50;
    private final static int INITIAL_DEFENSE = 50;
    private final static int LEVEL_FACTOR_HEALTH = 50;
    private final static int LEVEL_FACTOR_MANA = 50;
    private final static int LEVEL_FACTOR_ATTACK = 30;
    private final static int LEVEL_FACTOR_DEFENSE = 20;
    private static final int INCREASE_POINTS_HEALTH = 10;
    private static final int INCREASE_POINTS_MANA = 10;
    private static final int INCREASE_POINTS_ATTACK = 5;
    private static final int INCREASE_POINTS_DEFENSE = 5;
    private static final int STATS_MINIMUM = 0;
    private static final double INITIAL_CRITICAL_HIT_CHANCE = 0.05; // 5% base crit chance
    private static final double INITIAL_DODGE_CHANCE = 0.05; // 5% base dodge chance

    private Stats() {
        this.health = INITIAL_HEALTH;
        this.mana = INITIAL_MANA;
        this.attack = INITIAL_ATTACK;
        this.defense = INITIAL_DEFENSE;
        this.criticalHitChance = INITIAL_CRITICAL_HIT_CHANCE;
        this.dodgeChance = INITIAL_DODGE_CHANCE;
    }

    public int getHealth() {
        return health;
    }

    public int getMana() {
        return mana;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public boolean useMana(int neededMana) {
        if (mana >= neededMana) {
            mana -= neededMana;
            return true;
        }

        return false;
    }

    public boolean increaseMana(int newMana) {
        mana =  (newMana > INITIAL_MANA - mana) ? INITIAL_MANA : mana + newMana;
        return true;
    }

    public boolean decreaseHealth(int damagePoints) {
        int damage = damagePoints - defense;
        health = (damage > STATS_MINIMUM && damage >= health) ? STATS_MINIMUM : health - damage;

        return true;
    }

    public boolean increaseHealth(int healthPoints) {
        health = (healthPoints > INITIAL_HEALTH - health) ? INITIAL_HEALTH : health + healthPoints;
        return true;
    }

    public void increaseForReachingNewLevel() {
        increaseHealth(INCREASE_POINTS_HEALTH);
        increaseMana(INCREASE_POINTS_MANA);
        attack += INCREASE_POINTS_ATTACK;
        defense += INCREASE_POINTS_DEFENSE;
    }

    public void modifyByLevel(int level) {
        health += level * LEVEL_FACTOR_HEALTH;
        mana += level * LEVEL_FACTOR_MANA;
        attack += level * LEVEL_FACTOR_ATTACK;
        defense += level * LEVEL_FACTOR_DEFENSE;
    }

    public static Stats getStatsInstance() {
        return new Stats();
    }


    public boolean isAlive() {
        return health > 0;
    }

    public double getCriticalHitChance() {
        return criticalHitChance;
    }

    public double getDodgeChance() {
        return dodgeChance;
    }
}
