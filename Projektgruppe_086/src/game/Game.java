package game;

import java.util.*;

import base.Edge;
import game.jokers.RevolutionJoker;
import game.jokers.TroopsJoker;
import game.map.Castle;
import game.map.Kingdom;
import game.map.GameMap;
import game.map.MapSize;
import gui.AttackThread;
import gui.Resources;
import gui.views.GameView;

public class Game {

    private Goal goal;
    private List<Player> players;
    private boolean isOver;
    private boolean hasStarted;
    private int round;
    private MapSize mapSize;
    private GameMap gameMap;
    private Queue<Player> playerQueue;
    private Player startingPlayer;
    private Player currentPlayer;
    private GameInterface gameInterface;
    private AttackThread attackThread;

    public Game() {
        this.isOver = false;
        this.hasStarted = false;
        this.mapSize = MapSize.MEDIUM;
        this.players = new LinkedList<>();
    }

    public void addPlayer(Player p) {
        if(players.contains(p))
            throw new IllegalArgumentException("Spieler wurde bereits hinzugefügt");

        this.players.add(p);
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
        this.goal.setGame(this);
    }

    public int getRound() {
        return round;
    }

    public void setMapSize(MapSize mapSize) {
        this.mapSize = mapSize;
    }

  //created - Dominance : Sander
    public Castle getFlagCastle() {
    	return getMap().getCastles().stream().filter(c -> c.flag == true).findFirst().get();
    }
    
    private void generateMap() {

        int mapSizeMultiplier = this.mapSize.ordinal() + 1;
        int playerCount = players.size();
        int numRegions = playerCount * GameConstants.CASTLES_NUMBER_MULTIPLIER * mapSizeMultiplier;
        double tileMultiplier = 1.0 + (mapSizeMultiplier * 0.3);

        // We set up space for 2 times the region count
        int numTiles = (int) Math.ceil(numRegions * tileMultiplier);

        // Our map should be 3:2
        int width = (int) Math.ceil(0.6 * numTiles);
        int height = (int) Math.ceil(0.4 * numTiles);

        int continents = Math.min(3, playerCount + this.mapSize.ordinal());

        this.gameMap = GameMap.generateRandomMap(width, height, 40, numRegions, continents);
    }

    public void start(GameInterface gameInterface) {

        if(hasStarted)
            throw new IllegalArgumentException("Spiel wurde bereits gestartet");

        if(players.size() < 2)
            throw new IllegalArgumentException("Nicht genug Spieler");

        if(goal == null)
            throw new IllegalArgumentException("Kein Spielziel gesetzt");

        this.generateMap();

        // Create random player order
        this.gameInterface = gameInterface;
        List<Player> tempList = new ArrayList<>(players);
        playerQueue = new ArrayDeque<>();
        while(!tempList.isEmpty()) {
            Player player = tempList.remove((int) (Math.random() * tempList.size()));
            player.reset();
            playerQueue.add(player);
        }

        startingPlayer = playerQueue.peek();
        hasStarted = true;
        isOver = false;
        round = 0;

        gameInterface.onGameStarted(this);
        nextTurn();
    }

    public AttackThread startAttack(Castle source, Castle target, int troopCount) {
        if(attackThread != null)
            return attackThread;

        if(source.getOwner() == target.getOwner() || troopCount < 1 ||  /* changed */ target.getOwner() == null)
            return null;

        attackThread = new AttackThread(this, source, target, troopCount);
        attackThread.start();
        gameInterface.onAttackStarted(source, target, troopCount);
        return attackThread;
    }

    public void doAttack(Castle attackerCastle, Castle defenderCastle, int[] rollAttacker, int[] rollDefender) {

        Integer[] rollAttackerSorted = Arrays.stream(rollAttacker).boxed().sorted(Comparator.reverseOrder()).toArray(Integer[]::new);
        Integer[] rollDefenderSorted = Arrays.stream(rollDefender).boxed().sorted(Comparator.reverseOrder()).toArray(Integer[]::new);

        Player attacker = attackerCastle.getOwner();
        Player defender = defenderCastle.getOwner();

        for(int i = 0; i < Math.min(rollAttacker.length, rollDefender.length); i++) {
            if(rollAttackerSorted[i] > rollDefenderSorted[i]) {
                defenderCastle.removeTroops(1);
                if(defenderCastle.getTroopCount() == 0) {
                    attackerCastle.removeTroops(1);
                    if(defenderCastle.getMainCastle()) {   //changed for deathmatch mode : Luca
                    	defenderCastle.setMainCastle(false);
                    }
                    defenderCastle.setOwner(attacker);
                    defenderCastle.setPreviousOwner(defender);   //changed for deathmatch mode : Luca
                    attacker.hasGained = true;   //changed for deathmatch mode : Luca
                    defenderCastle.addTroops(1);
                    gameInterface.onConquer(defenderCastle, attacker);
                    addScore(attacker, 50);
                    //changed for deathmatch mode : Luca
                    if (getGoal().gameModeID == 3 ) {       	                    	
                    	Optional<Castle> main = currentPlayer.getCastles(this).stream().filter(t -> t.getMainCastle()).findFirst();
                    	if(goal.hasLost(defender)) {
                    		main.get().addTroops(35);
                    		gameInterface.onLosing(defender , attacker);
                    		gameInterface.onGainingCastle(getCurrentPlayer(), 35);
                    	}
                    	//------
                    	else {
                    		main.get().addTroops(10);
                    		gameInterface.onGainingCastle(getCurrentPlayer(), 10);
                    	}

                		
                    }
                    break;
                } else {
                    addScore(attacker, 20);
                }
            } else {
                attackerCastle.removeTroops(1);
                addScore(defender, 30);
            }
        }

        gameInterface.onUpdate();
    }

    public void moveTroops(Castle source, Castle destination, int troopCount) {
        if(troopCount >= source.getTroopCount() || source.getOwner() != destination.getOwner())
            return;

        source.moveTroops(destination, troopCount, currentPlayer, this);
        gameInterface.onUpdate();
    }

    public void stopAttack() {
        this.attackThread = null;
        this.gameInterface.onAttackStopped();
    }

    public int[] roll(Player player, int dices, boolean fastForward) {
        return gameInterface.onRoll(player, dices, fastForward);
    }

    private boolean allCastlesChosen() {
        return gameMap.getCastles().stream().noneMatch(c -> c.getOwner() == null);
    }
    
  //changed
    private boolean eachPlayerOneCastl( ) {
    	return players.stream().allMatch(t -> t.getCastles(this).size() == 1);
    }

    public AttackThread getAttackThread() {
        return this.attackThread;
    }

    public void chooseCastle(Castle castle, Player player) {
        if(castle.getOwner() != null || player.getRemainingTroops() == 0)
            return;

        gameInterface.onCastleChosen(castle, player);
        player.removeTroops(1);
        castle.setOwner(currentPlayer);
        //changed
        if (goal.gameModeID == 3 && player.getCastles(this).size() == 1) {
        	castle.addTroops(20);
        	castle.setMainCastle(true);
        }
        else  {
            castle.addTroops(1);	
        }
        addScore(player, 5);

        if(player.getRemainingTroops() == 0 || allCastlesChosen()) {
            player.removeTroops(player.getRemainingTroops());
            nextTurn();
        }
    }

    public void addTroops(Player player, Castle castle, int count) {
        if(count < 1 || castle.getOwner() != player)
            return;

        count = Math.min(count, player.getRemainingTroops());
        castle.addTroops(count);
        player.removeTroops(count);
    }

    public void addScore(Player player, int score) {
        player.addPoints(score);
        gameInterface.onAddScore(player, score);
    }

    public void endGame() {
        isOver = true;
        Player winner = goal.getWinner();

        if(winner != null)
            addScore(goal.getWinner(), 150);

        Resources resources = Resources.getInstance();
        for(Player player : players) {
            resources.addScoreEntry(new ScoreEntry(player, goal));
        }

        gameInterface.onGameOver(winner);
    }

    public void nextTurn() {
        if(goal.isCompleted()) {
            endGame();
            return;
        }

        // Choose next player
        Player nextPlayer;
        do {
            nextPlayer = playerQueue.remove();

            // if player has already lost, remove him from queue
            if(goal.hasLost(nextPlayer)) {
                if(startingPlayer == nextPlayer) {
                    startingPlayer = playerQueue.peek();
                }
                nextPlayer = null;
            }
        } while(nextPlayer == null && !playerQueue.isEmpty());

        if(nextPlayer == null) {
            isOver = true;
            gameInterface.onGameOver(goal.getWinner());
            return;
        }

        currentPlayer = nextPlayer;
        
      //changed for suddendeath mode : Luca -  choose mainCastle for each player
        if (goal.gameModeID == 2 && startingPlayer.getCastles(this).size() == 0) {
        		ArrayList<Integer> used = new ArrayList<Integer>();
        	for (Player p : players) {

        		int random = (int) Math.floor(Math.random()*gameMap.getCastles().size());
        		while (used.contains(random)) {
        			random = (int) Math.floor(Math.random()*gameMap.getCastles().size());
        		}
            	gameMap.getCastles().get(random).setMainCastle(true);
            	gameMap.getCastles().get(random).setOwner(p);
            	gameMap.getCastles().get(random).addTroops(50);
            	used.add(random);
        	}          
        }

        currentPlayer.setGainedCastleId3(false); //changed
        
        if(round == 0 || (round == 1 && allCastlesChosen()) || (round > 1 && currentPlayer == startingPlayer) || /*changed*/ (round == 1 && goal.gameModeID == 3 && eachPlayerOneCastl())) {
        	// changed - Dominance : Sander
        	if(goal.gameModeID == 1 && round >= 2) {
        		this.getMap().getCastles().stream().filter(c -> c.flag == true).forEach(t -> t.getOwner().addFlagRound());
             		
        	}
        	round++;
            gameInterface.onNewRound(round);
        }

        int numRegions = currentPlayer.getNumRegions(this);

        int addTroops;
        boolean anyNeighbourCastle = false;
        if(round == 1)
        	//changed for deathmatch mode : Luca - players can only choose one castle instead of 3
	    	if ( goal.gameModeID == 3) {
	    		addTroops = GameConstants.CASTLES_AT_BEGINNING_3;
	    	}
	    	else {
	    		addTroops = GameConstants.CASTLES_AT_BEGINNING;		
	    	}
        
        
        else {
        	
        	//changed for deatmatch mode : Luca - player get no troops per round
        	if (goal.gameModeID == 3) {
        		addTroops = 0;
        	
        	}
        	else {
        		
            addTroops = Math.max(3, numRegions / GameConstants.TROOPS_PER_ROUND_DIVISOR);
            addScore(currentPlayer, addTroops * 5);

            // changed - Dominance : Sander
            if(goal.gameModeID == 1) {
    	        for(Castle c: currentPlayer.getCastles(this)) {
    	        	if (c.flag == true) {
    	        		currentPlayer.playerwithflag = true;
    	        	}
    	        }
    	        if (currentPlayer.playerwithflag == false && round > 1) {
    	        	if (mapSize == MapSize.SMALL) {
    	        	      addTroops++;
    	        	}
    	        	else if (mapSize == MapSize.MEDIUM) {
    		        	  addTroops = addTroops + 2;
    		        }
    	        	else if (mapSize == MapSize.LARGE) {
    	        		  addTroops = addTroops + 3;		      
    	            }
    	        }
            }
            
         // changed - SuddenDeath : Luca
            // adds troops to surrounding castles of main castle
            else if (goal.gameModeID == 2 && round > 1) {
            	Optional<Castle> cCastle = currentPlayer.getCastles(this).stream().filter(t -> t.getMainCastle()).findFirst();
            	
            	List<Edge<Castle>> neighbourCastle = gameMap.getGraph().getEdges(gameMap.getGraph().getNode(cCastle.get()));

            	for (Edge c: neighbourCastle) {
            		
            		Castle current = (Castle) c.getOtherNode(gameMap.getGraph().getNode(cCastle.get())).getValue();
            		if(current.getOwner() == currentPlayer) {
            			current.addTroops(5);
                		anyNeighbourCastle = true;	
            		}
            	
            		
            		
            	};
            	
            }
    	}
            
            for(Kingdom kingdom : gameMap.getKingdoms()) {
                if(kingdom.getOwner() == currentPlayer) {
                	//changed for deathmatch mode : Luca - gives a player that owns a kingdom five troops in every castle he owns and prints it in the log
                	if (goal.gameModeID == 3 && currentPlayer.hasConqueredKingdom(kingdom)) {
                		addScore(currentPlayer, 10);
                		currentPlayer.addToConquered(kingdom);
                		for (Castle c : currentPlayer.getCastles(this)) {
                			c.addTroops(5);
                		}
                		gameInterface.onGainingKingdom(currentPlayer);
                	}
                	else if (goal.gameModeID != 3){
                    addScore(currentPlayer, 10);
                    addTroops++;
                	}
                }
            }
        }
        
        if (currentPlayer.getJoker().getClass() == TroopsJoker.class && round>1 && currentPlayer.getJoker().isUsable()) {
        	if (currentPlayer.useJoker(currentPlayer.getJoker())) {
        		currentPlayer.getJoker().use();
        		addTroops+=Math.round(-0.0292*Math.pow((gameMap.getWidth()/120), 2)+1.1591*(gameMap.getWidth()/120)-2.0649);
        		((GameView) gameInterface).logLine("%PLAYER% hat den TroopsJoker benutzt und "+ Math.round(-0.0292*Math.pow((gameMap.getWidth()/120), 2)+1.1591*(gameMap.getWidth()/120)-2.0649) +" Truppen zusätzlich bekommen.", currentPlayer);
        		
        	}
        }
        
        currentPlayer.addTroops(addTroops);
        boolean isAI = (currentPlayer instanceof AI);
        gameInterface.onNextTurn(currentPlayer, addTroops, !isAI);
        
        // changed for suddendeath mode : Luca 
        //if main castle has neighbours prints this out in the log
        if (round > 1 && anyNeighbourCastle) {
            gameInterface.onSurroundingCastle(currentPlayer , 5);	
        }
        
        if(isAI) {
        	if (currentPlayer.getJoker().getClass() == RevolutionJoker.class && currentPlayer.getJoker().isUsable() && ((this.goal.gameModeID != 3 && this.round>1)||(this.goal.gameModeID == 3 &&this.round>2))) {
        		if (currentPlayer.useJoker(currentPlayer.getJoker())) {
        			((AI) currentPlayer).useRevolution(this, gameInterface);
        		}
        	}
            ((AI)currentPlayer).doNextTurn(this);
        }

        currentPlayer.hasGained = false;
        anyNeighbourCastle = false; //changed for suddendeath mode : Luca
        playerQueue.add(currentPlayer);
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public GameMap getMap() {
        return this.gameMap;
    }

    public boolean isOver() {
        return this.isOver;
    }

	public Goal getGoal() {
		return goal;
	}
	
}
