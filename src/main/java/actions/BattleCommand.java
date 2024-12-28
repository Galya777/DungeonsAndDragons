package actions;

import Characters.Character;
import Characters.Hero;
import Characters.Minion;
import Characters.Position;
import graphicScenes.MapGenerator;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * The BattleCommand class represents a command that initiates a battle between a hero and an enemy.
 * It determines the outcome of the battle based on the hero's and enemy's stats.
 */

public class BattleCommand extends CommandImpl {
    private static final String NO_MINION_AT_POSITION_MESSAGE = "There is no minion to battle here.\n";
    static final String NO_PLAYER_AT_POSITION_MESSAGE = "There is no player to battle here.\n";
    public static final String HERO_WON_MESSAGE = "The enemy is dead. You won. \n";
    static final String HERO_LOST_MESSAGE = "You died. Enemy wins. \n";
    static final String KILLED_YOU_MESSAGE = " killed you. \n";
    static final String BATTLE_DRAW_MESSAGE = "'s battle with you ended with a draw.\n";
    public static final String YOU_KILLED_MESSAGE = "You killed ";
    static final String DRAW_MESSAGE = "Battle ended with a draw.\n";

    private static final int MAX_BATTLE_ROUNDS = 10;

    private PlayerRepository playerRepository;
    private MapGenerator gameRepository;
    boolean isDraw;

    public BattleCommand(Hero hero, String[] splitCommand, PlayerRepository playerRepositroy, MapGenerator gameRepository) {
        super(hero, splitCommand);
        this.playerRepository = playerRepositroy;
        this.gameRepository = gameRepository;
    }

    public boolean isEven(int number) {
        return number % 2 == 0;
    }

    public boolean battle(Character enemy) {
        int counter = 0;
        while (hero.isAlive() && enemy.isAlive() && counter <= MAX_BATTLE_ROUNDS) {
            if (isEven(counter)) {
                if (!enemy.attemptDodge()) {
                    int damage = hero.calculateDamage();
                    enemy.takeDamage(damage);
                    System.out.println(hero.getName() + " deals " + damage + " damage to " + enemy.getName());
                } else {
                    System.out.println(enemy.getName() + " dodged the attack!");
                }
            } else {
                if (!hero.attemptDodge()) {
                    int damage = enemy.calculateDamage();
                    hero.takeDamage(damage);
                    System.out.println(enemy.getName() + " deals " + damage + " damage to " + hero.getName());
                } else {
                    System.out.println(hero.getName() + " dodged the attack!");
                }
            }

            counter++;
        }

        if (hero.isAlive()) {
            hero.gainExperience(enemy.giveExperiencePointsAfterBattle());
            isDraw = enemy.isAlive();
            return true;
        }
        return false;
    }

    public String executeBattleWithMinion() {
        Position heroPosition = hero.getPosition();
        if (!gameRepository.mapHasMinionAtPosition(heroPosition)) {
            return NO_MINION_AT_POSITION_MESSAGE;
        }
        Minion minion = gameRepository.getMinionAtPosition(heroPosition);
        if (minion == null) {
            return NO_MINION_AT_POSITION_MESSAGE;
        }

        return battleOutcomeWithMinion(minion);
    }

    public String battleOutcomeWithMinion(Minion minion) {
        if (battle(minion)) {
            if (!isDraw) {
                gameRepository.updateDeadMinionAtPosition(new Position(hero.getPosition()), " " + hero.getId() + " ");
                return HERO_WON_MESSAGE;
            }

            return DRAW_MESSAGE;
        }

        return HERO_LOST_MESSAGE;
    }

    public String executeBattleWithPlayer(UserRecipient userRecipient) {
        Map<SocketChannel, Hero> heroBySocketChannel = playerRepository.getHeroBySocketChannel();
        Position heroPosition = hero.getPosition();
        Optional<Entry<SocketChannel, Hero>> entryPlayer = heroBySocketChannel.entrySet().stream()
                .filter(entry -> entry.getValue().getPosition().equals(heroPosition)).findFirst();

        if (entryPlayer.isPresent()) {
            SocketChannel socketChannelRecipient = entryPlayer.get().getKey();
            Hero player = heroBySocketChannel.get(socketChannelRecipient);
            userRecipient.setSocketChannel(socketChannelRecipient);

            return battleOutcomeWithPlayer(player, userRecipient);

        }

        return NO_PLAYER_AT_POSITION_MESSAGE;
    }

    public String battleOutcomeWithPlayer(Hero player, UserRecipient userRecipient) {
        if (battle(player)) {
            if (isDraw) {
                player.gainExperience(hero.giveExperiencePointsAfterBattle());
                userRecipient.setMessage(hero.getFormattedName() + BATTLE_DRAW_MESSAGE);
                return DRAW_MESSAGE;
            }

            userRecipient.setMessage(hero.getFormattedName() + KILLED_YOU_MESSAGE);
            return HERO_WON_MESSAGE;
        }

        userRecipient.setMessage(YOU_KILLED_MESSAGE + hero.getFormattedName());
        player.gainExperience(hero.giveExperiencePointsAfterBattle());
        return HERO_LOST_MESSAGE;
    }

    @Override
    public String execute(UserRecipient userRecipient) {
        if (gameRepository.mapHasMinionAtPosition(hero.getPosition())) {
            return executeBattleWithMinion();
        }
        else return executeBattleWithPlayer(userRecipient);
    }

}
