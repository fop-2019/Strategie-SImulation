package game.jokers;

import game.Joker;

public class RevolutionJoker extends Joker{


	protected boolean used = false;
	public RevolutionJoker() {
		super();
	}

	@Override
	public void use() {
		this.used = true;
		
	}

	@Override
	public boolean isUsable() {
		return !this.used;
	}

	@Override
	public String toString() {
		return "RevolutionJoker";
	}

}
