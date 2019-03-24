package game.jokers;

import game.Joker;

public class RevolutionJoker extends Joker{

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
	public void show() {
		
	}

	@Override
	public String toString() {
		return "RevolutionJoker";
	}

}
