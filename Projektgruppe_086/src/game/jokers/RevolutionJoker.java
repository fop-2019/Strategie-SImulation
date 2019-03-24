package game.jokers;

import game.Joker;
import game.Player;

public class RevolutionJoker extends Joker{

	public RevolutionJoker() {
		super();
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "RevolutionJoker";
	}

}
