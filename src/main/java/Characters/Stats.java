package Characters;


public class Stats {

    private int health;
    private int mana;
    private int attack;
    private int deffense;
    private final static int INITIAL_HEALTH = 100;
    private final static int INITIAL_MANA = 100;
    private final static int INITIAL_ATTACK = 50;
    private final static int INITITAL_DEFFENSE = 50;
    private final static int LEVEL_FACTOR_HEALTH = 50;
    private final static int LEVEL_FACTOR_MANA = 50;
    private final static int LEVEL_FACTOR_ATTACK = 30;
    private final static int LEVEL_FACTOR_DEFFENSE = 20;
    private static final int INCREASE_POINTS_HEALTH = 10;
    private static final int INCREASE_POINTS_MANA = 10;
    private static final int INCREASE_POINTS_ATTACK = 5;
    private static final int INCREASE_POINTS_DEFFENSE = 5;
    private static final int STATS_MINIMUM = 0;

    private Stats() {
        this.health = INITIAL_HEALTH;
        this.mana = INITIAL_MANA;
        this.attack = INITIAL_ATTACK;
        this.deffense = INITITAL_DEFFENSE;
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

    public int getDeffense() {
        return deffense;
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
        int damage = damagePoints - deffense;
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
        deffense += INCREASE_POINTS_DEFFENSE;
    }

    public void modifyByLevel(int level) {
        health += level * LEVEL_FACTOR_HEALTH;
        mana += level * LEVEL_FACTOR_MANA;
        attack += level * LEVEL_FACTOR_ATTACK;
        deffense += level * LEVEL_FACTOR_DEFFENSE;
    }

    public static Stats getStatsInstance() {
        return new Stats();
    }


    public boolean isAlive() {
        return health > 0;
    }
}
