package game.players;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import base.Edge;
import base.Graph;
import base.Node;
import game.AI;
import game.Game;
import game.GameInterface;
import game.Joker;
import game.Player;
import game.jokers.DiceJoker;
import game.jokers.RevolutionJoker;
import game.jokers.TroopsJoker;
import game.map.Castle;
import gui.AttackThread;
import gui.views.GameView;

public class BasicAI extends AI {

    public BasicAI(String name, Color color) {
        super(name, color);
    }

    private Castle getCastleWithFewestTroops(List<Castle> castles) {
        Castle fewestTroops = castles.get(0);
        for(Castle castle : castles) {
            if(castle.getTroopCount() < fewestTroops.getTroopCount()) {
                fewestTroops = castle;
            }
        }

        return fewestTroops;
    }

    @Override
    protected void actions(Game game) throws InterruptedException {
        if(game.getRound() == 1) {
            List<Castle> availableCastles = game.getMap().getCastles().stream().filter(c -> c.getOwner() == null).collect(Collectors.toList());
            while(availableCastles.size() > 0 && getRemainingTroops() > 0) {

                sleep(1000);

                Castle randomCastle = availableCastles.remove(this.getRandom().nextInt(availableCastles.size()));
                game.chooseCastle(randomCastle, this);
            }
        } else {

            // 1. Distribute remaining troops
            Graph<Castle> graph = game.getMap().getGraph();
            List<Castle> castleNearEnemy = new ArrayList<>();
            for(Castle castle : this.getCastles(game)) {
                Node<Castle> node = graph.getNode(castle);
                for(Edge<Castle> edge : graph.getEdges(node)) {
                    Castle otherCastle = edge.getOtherNode(node).getValue();
                    if(otherCastle.getOwner() != this) {
                        castleNearEnemy.add(castle);
                        break;
                    }
                }
            }

            while(this.getRemainingTroops() > 0) {
                Castle fewestTroops = getCastleWithFewestTroops(castleNearEnemy);
                sleep(500);
                game.addTroops(this, fewestTroops, 1);
            }

            boolean attackWon;

            do {
                // 2. Move troops from inside to border
                for (Castle castle : this.getCastles(game)) {
                    if (!castleNearEnemy.contains(castle) && castle.getTroopCount() > 1) {
                        Castle fewestTroops = getCastleWithFewestTroops(castleNearEnemy);
                        game.moveTroops(castle, fewestTroops, castle.getTroopCount() - 1);
                    }
                }

                // 3. attack!
                attackWon = false;
                for (Castle castle : castleNearEnemy) {
                    if(castle.getTroopCount() < 2)
                        continue;

                    Node<Castle> node = graph.getNode(castle);
                    for (Edge<Castle> edge : graph.getEdges(node)) {
                        Castle otherCastle = edge.getOtherNode(node).getValue();
                        if (otherCastle.getOwner() != this && castle.getTroopCount() >= otherCastle.getTroopCount()) {
                            AttackThread attackThread = game.startAttack(castle, otherCastle, castle.getTroopCount());
                            if(fastForward)
                                attackThread.fastForward();

                            attackThread.join();
                            attackWon = attackThread.getWinner() == this;
                            break;
                        }
                    }

                    if(attackWon)
                        break;
                }
            } while(attackWon);
        }
    }

	@Override
	public boolean useJoker(Joker type) {
		System.out.println("joker used");
		return Math.random()<0.33;
		
	}

	@Override
	public Joker chooseJoker() {
		
		int rnd = (int) Math.round(Math.random()*2)+1;
		
		switch (rnd) {
		case 1: System.out.println("I use DiceJoker");this.setJoker(new DiceJoker()); return this.getJoker(); 
		case 2: System.out.println("I use TroopsJoker");this.setJoker(new TroopsJoker()); return this.getJoker(); 
		case 3: System.out.println("I use RevolutionJoker");this.setJoker(new RevolutionJoker()); return this.getJoker(); 
		}
		
		return null;
	}

	@Override
	public void useRevolution(Game game, GameInterface gameInterface) {
		List<Castle> castles = new LinkedList<>();
		for (Player opp:game.getPlayers()) {
			if(opp != game.getCurrentPlayer()) {
				castles.addAll(opp.getCastles(game));
			}
		}
		if (game.getGoal().gameModeID == 2) {
			for (Castle c:castles) {
				if (c.getMainCastle()) {
					castles.remove(c);
				}
			}
		} 
		Castle selectedCastle = castles.get((int) Math.round(Math.random()*castles.size()));
		((GameView) gameInterface).logLine("%PLAYER% hat den RevolutionJoker benutzt und hat die Herrschaft über "+ selectedCastle.getName() +" erhalten.", game.getCurrentPlayer());
		System.out.println(game.getCurrentPlayer().getName() + " revolution at "+selectedCastle.getName());
		game.getCurrentPlayer().getJoker().use();
		Player opp = selectedCastle.getOwner();
		opp.addTroops(selectedCastle.getTroopCount()-1);
		selectedCastle.removeTroops(selectedCastle.getTroopCount());
		selectedCastle.setOwner(game.getCurrentPlayer());
		selectedCastle.addTroops(1);
			
	}
		
	

}
