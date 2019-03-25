package game;

import game.goals.*;
import game.players.*;

import java.awt.*;

public class GameConstants {

    public static final int MAX_PLAYERS = 4;

    // Determines how many regions are generated per player,
    // e.g. PlayerCount * 7 for Small, PlayerCount * 14 for Medium and PlayerCount * 21 for Large Maps
    public static final int CASTLES_NUMBER_MULTIPLIER = 7;
    public static final int CASTLES_AT_BEGINNING = 1;
    public static final int TROOPS_PER_ROUND_DIVISOR = 3;

    public static final Color COLOR_WATER = new Color(184, 184, 188);
    public static final Color COLOR_SAND  = new Color(134, 134, 137);
    public static final Color COLOR_GRASS = new Color(104, 104, 107);
    public static final Color COLOR_STONE = new Color(59, 59, 61);
    public static final Color COLOR_SNOW  = Color.WHITE;

    public static final Color PLAYER_COLORS[] = {
        new Color (98, 175, 234),
        new Color (201, 50, 30),
        Color.GREEN,
        Color.ORANGE
    };

    public static final Goal GAME_GOALS[] = {
        new ConquerGoal(),
        // TODO: Add more Goals
    };

    public static final Class<?> PLAYER_TYPES[] = {
        Human.class,
        BasicAI.class,
        Vodka.class,
        // TODO: Add more Player types, like different AIs
    };
}
