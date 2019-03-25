package game.goals;

import java.util.Optional;

import game.Game;
import game.Goal;
import game.Player;
import game.map.Castle;

public class SuddenDeath extends Goal {

    public SuddenDeath() {
        super("SuddenDeath", "Jedem Spieler wird eine Hauptburg zugewiesen. Wenn diese erobert wird, ist das Spiel vorbei!");
        this.gameModeID = 2;
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


    @Override
    public boolean hasLost(Player player) {
        if(getGame().getRound() < 2)
            return false;
        Optional<Castle> current = player.getCastles(getGame()).stream().filter(t -> t.getMainCastle()).findFirst();
      Castle currentC =  current.orElse(null);
      //System.out.println(currentC.getName());

  
        return currentC == null;
    }
}

