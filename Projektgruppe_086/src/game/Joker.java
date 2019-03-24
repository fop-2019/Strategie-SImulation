package game;

import game.jokers.DiceJoker;
import game.jokers.RevolutionJoker;
import game.jokers.TroopsJoker;

public abstract class Joker {
	protected boolean used = false;
	
	public Joker() {
		// TODO Auto-generated constructor stub
	}

	public static Joker getInstance (Class<?> jokerClass) {
		if (jokerClass == DiceJoker.class) {
			return new DiceJoker();
		} else if (jokerClass == RevolutionJoker.class) {
			return new RevolutionJoker();
		} else if (jokerClass == TroopsJoker.class) {
			return new TroopsJoker();
		} else {
			return null;
		}
	}
	
	public abstract String toString();
	
	public abstract void use();
	
	public abstract boolean isUsable();
	
	public abstract void show();
	
}
