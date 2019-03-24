package game.jokers;

import game.Joker;
import game.Player;

public class TroopsJoker extends Joker{
	
	public int bonus;

	public TroopsJoker() {
		super();
	}

	@Override
	public void use() {
		this.used = true;
	}

	@Override
	public boolean isUsable() {
		return !used;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "TroopsJoker";
	}

}
