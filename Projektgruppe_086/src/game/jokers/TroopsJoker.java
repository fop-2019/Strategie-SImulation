package game.jokers;

import game.Joker;

public class TroopsJoker extends Joker{
	

	protected boolean used = false;
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
	public String toString() {
		return "TroopsJoker";
	}

}
