package game.players;

import java.awt.Color;

import javax.swing.JOptionPane;

import game.Joker;
import game.Player;
import game.jokers.DiceJoker;

public class Human extends Player {
    public Human(String name, Color color) {
        super(name, color);
    }

	@Override
	public boolean useJoker(Joker type) {
		
		Object[] options = {"Ja","Nein"};
		int n = JOptionPane.showOptionDialog(null,getName()+", möchtest du den "+type.toString()+" benutzen?",getName()+"s "+type.toString(), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,options,options[1]);
		if (n == 0) {
			return true;
		}
		return false;
		
	}
}
