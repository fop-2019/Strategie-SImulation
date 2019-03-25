package game.jokers;

import game.Joker;

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
		
	}

	@Override
	public String toString() {
		return "TroopsJoker";
	}

}
