package game;

import game.jokers.DiceJoker;
import game.jokers.RevolutionJoker;
import game.jokers.TroopsJoker;

public abstract class Joker {
	
	public Joker() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * returns a new instance of the wanted joker
	 * @param jokerClass class of the requested joker
	 * @return a new instance of the wanted joker
	 */ 
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
	
	/**
	 * returns the name of the joker
	 */
	public abstract String toString();
	
	/**
	 * sets the attribute used to true
	 */
	public abstract void use();
	
	/**
	 * returns usability of the joker 
	 * @return usability of the joker 
	 */
	public abstract boolean isUsable();
	
}
