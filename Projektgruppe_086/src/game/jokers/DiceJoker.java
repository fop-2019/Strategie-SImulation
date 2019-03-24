package game.jokers;

import game.Joker;
import game.Player;

public class DiceJoker extends Joker{

	public DiceJoker() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void use() {
		this.used = true;
	}

	@Override
	public boolean isUsable() {
		// TODO Auto-generated method stub
		return !this.used;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "DiceJoker";
	}

}
