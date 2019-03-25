package game.goals;

import game.Game;
import game.Goal;
import game.Player;
import game.map.Castle;

public class Dominance extends Goal {
	
	public Player flagOwner;

    public Dominance() {
        super("Herrschaft", "Derjenige Spieler gewinnt, der die Fahnenburg 7 Runden lang erobert hält.");
        this.gameModeID = 1;
    }

    @Override
    public boolean isCompleted() {
    		return this.getWinner() != null;
    }

    @Override
    public Player getWinner() {
        Game game = this.getGame();
        if(game.getRound() < 2)
            return null;

        for(Player p : game.getPlayers()) {
            if(p.flagRounds >= 7)
                return p;
        }

        return null;
    }

    @Override
    public boolean hasLost(Player player) {
    	 if(getGame().getRound() < 2)
             return false;

         return player.getNumRegions(getGame()) == 0;
    }     
}
