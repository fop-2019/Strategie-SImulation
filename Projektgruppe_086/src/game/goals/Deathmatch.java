package game.goals;

import game.Game;
import game.Goal;
import game.Player;
import game.map.Castle;

public class Deathmatch extends Goal {

    public Deathmatch() {
        super("Deathmatch", "Starte mit einer Burg und 20 Truppen. Wer es schafft, als letzter zu überleben, gewinnt!");
        this.gameModeID = 3;
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
        
        int i = 0;
        Player currentPlayer = game.getCurrentPlayer();
        Object[] otherPlayers =  game.getPlayers().stream().filter(t -> t != currentPlayer).toArray();
        Player p = null;
        for(Object c : otherPlayers) {
            if(hasLost((Player)c) == true)
                i++;
        }
        if (i == otherPlayers.length) {
        	return currentPlayer;
        }
        else return null;

        }

//        return p;
        
    

    @Override
    public boolean hasLost(Player player) {
        if(getGame().getRound() < 2)
            return false;

        return player.getNumRegions(getGame()) == 0;
    }
}