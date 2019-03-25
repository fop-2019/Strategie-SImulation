package game.players;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import base.Edge;
import base.Graph;
import base.Node;
import game.AI;
import game.Game;
import game.map.Castle;
import game.map.GameMap;
import gui.AttackThread;

public class Vodka extends AI {

	public Vodka(String name, Color color) {
		super(name, color);
	}

	private int temp, temp1, currentIndex, targetKingdom;

	private Castle getCastleWithFewestTroops(List<Castle> castles) {
		Castle fewestTroops = castles.get(0);
		for (Castle castle : castles) {
			if (castle.getTroopCount() < fewestTroops.getTroopCount()) {
				fewestTroops = castle;
			}
		}

		return fewestTroops;
	}

	private Castle getCastleWithFewestEnemyConnections(List<Castle> myCastles, Graph<Castle> graph) {
		List<Integer> enemyCastlesNearby = new ArrayList<>();
		for (Castle myCastle : myCastles) {
			int enemyCastlesConnectedCount = 0;
			for (Edge<Castle> edge : graph.getEdges(graph.getNode(myCastle))) {
				if (edge.getOtherNode(graph.getNode(myCastle)).getValue().getOwner() != this) {
					enemyCastlesConnectedCount++;
				}
			}
			enemyCastlesNearby.add(enemyCastlesConnectedCount);
		}
		return myCastles.get(enemyCastlesNearby.indexOf(Collections.min(enemyCastlesNearby)));
	}

	private List<Castle> getCastlesNearEnemy(Graph<Castle> graph, Game game) {
		List<Castle> castleNearEnemy = new ArrayList<>();
		for (Castle castle : this.getCastles(game)) {
			Node<Castle> node = graph.getNode(castle);
			for (Edge<Castle> edge : graph.getEdges(node)) {
				Castle otherCastle = edge.getOtherNode(node).getValue();
				if (otherCastle.getOwner() != this) {
					castleNearEnemy.add(otherCastle);
					break;
				}
			}
		}
		return castleNearEnemy;
	}

	private List<Integer> getConnectedKingdoms(Game game) {
		List<Integer> connectedKingdoms = new ArrayList<Integer>();
		for (int i = 0; i < getCastlesNearEnemy(game.getMap().getGraph(), game).size(); i++) {
			if (!connectedKingdoms
					.contains(getCastlesNearEnemy(game.getMap().getGraph(), game).get(i).getKingdom().getType())) {
				connectedKingdoms
						.add(getCastlesNearEnemy(game.getMap().getGraph(), game).get(i).getKingdom().getType());
			}
		}
		return connectedKingdoms;
	}

	private List<ArrayList<Castle>> getEnemyCastlesByKingdom(Game game) {
		List<ArrayList<Castle>> eCBK = new ArrayList<>();
		for (int i = 0; i < game.getMap().getKingdoms().size(); i++) {
			currentIndex = i;
			eCBK.add(new ArrayList<Castle>());
			game.getMap().getCastles().stream()
					.filter(c -> c.getKingdom().getType() == currentIndex && c.getOwner() != this)
					.forEach(c -> eCBK.get(currentIndex).add(c));
		}
		return eCBK;
	}

	private List<Castle> getCastlesNearTargetKingdom(Game game, int targetKingdom) {
		Graph<Castle> graph = game.getMap().getGraph();
		List<Castle> castleNearTargetKingdom = new ArrayList<>();
		for (Castle castle : this.getCastles(game)) {
			Node<Castle> node = graph.getNode(castle);
			for (Edge<Castle> edge : graph.getEdges(node)) {
				Castle otherCastle = edge.getOtherNode(node).getValue();
				if (getEnemyCastlesByKingdom(game).get(targetKingdom).contains(otherCastle)) {
					castleNearTargetKingdom.add(castle);
					break;
				}
			}
		}
		return castleNearTargetKingdom;
	}
	
	private int getTargetKingdom (Game game) {
		targetKingdom = getConnectedKingdoms(game).get(0);
		for (int i = 0; i < getEnemyCastlesByKingdom(game).size(); i++) {
			if (game.getMap().getKingdoms().get(i).getOwner() == this || !getConnectedKingdoms(game).contains(i)) {
				continue;
			}
			if (getEnemyCastlesByKingdom(game).get(i)
					.size() < getEnemyCastlesByKingdom(game).get(targetKingdom).size() + 1) {
				targetKingdom = i;
			}
		}
		return targetKingdom;
	}
	
//	private double getWinProbability (Castle castleA, Castle castleD) {
//		if (castleA.getTroopCount() > castleD.getTroopCount()) {
//			return 1;
//		}
//		else {
//			return 0;
//		}
//	}

	@Override
	protected void actions(Game game) throws InterruptedException {
		if (game.getRound() == 1) {

			int availableCastles = game.getMap().getCastles().size();
			int check = 0;
			while (availableCastles > 0 && getRemainingTroops() > 0) {
				check++;
//				System.out.println("Auswahlprozess beginnt");
				Thread.sleep(500);
				List<List<Integer>> castlesInKingdoms = new ArrayList<List<Integer>>();
				List<Integer> strategicValue = new ArrayList<Integer>();

				for (int i = 0; i < game.getMap().getKingdoms().size(); i++) {
					castlesInKingdoms.add(new ArrayList<Integer>());
				}

				for (int i = 0; i < game.getMap().getKingdoms().size(); i++) {
					for (int j = 0; j < game.getMap().getCastles().size(); j++) {
						if (game.getMap().getCastles().get(j).getOwner() == null
								&& game.getMap().getCastles().get(j).getKingdom().getType() == i) {
							castlesInKingdoms.get(i).add(j);
						}
					}
				}
				for (int i = 0; i < game.getMap().getKingdoms().size(); i++) {
					temp = 0;
					temp1 = i;
					game.getMap().getCastles().stream().filter(c -> c.getKingdom().getType() == temp1).forEach(c1 -> {
						if (c1.getOwner() != this || c1.getOwner() == null) {
							temp++;
						}
						if (c1.getOwner() == this) {
							temp--;
						}
					});
					if (castlesInKingdoms.get(i).size() == 0) {
						strategicValue.add(1000);
					} else {
						strategicValue.add(temp);
					}
				}

				int mostAttractiveKingdom = strategicValue.indexOf(Collections.min(strategicValue));

				List<Castle> castlesToChooseFrom = castlesInKingdoms.get(mostAttractiveKingdom).stream()
						.map(c -> game.getMap().getCastles().get(c)).collect(Collectors.toCollection(ArrayList::new));
				availableCastles--;
				game.chooseCastle(getCastleWithFewestEnemyConnections(castlesToChooseFrom, game.getMap().getGraph()),
						this);
				if (check > 1) {
					System.out.println("Not optimal");
				}
			}
		} else {

			// Looks for attackable Kingdoms

			targetKingdom = getTargetKingdom(game);

			// 1. Distribute remaining troops
			while (this.getRemainingTroops() > 0) {
				Castle fewestTroops = getCastleWithFewestTroops(getCastlesNearTargetKingdom(game, targetKingdom));
				sleep(500);
				game.addTroops(this, fewestTroops, 1);
			}

			boolean attackWon;

			do {
				if (getConnectedKingdoms(game).isEmpty()) {
					return;
				}
				targetKingdom = getTargetKingdom(game);
				List<Castle> cNTK = getCastlesNearTargetKingdom(game, targetKingdom);
				// 2. Move troops from inside to border
				for (Castle castle : this.getCastles(game)) {
					if (!cNTK.contains(castle) && castle.getTroopCount() > 1) {
						Castle fewestTroops = getCastleWithFewestTroops(cNTK);
						game.moveTroops(castle, fewestTroops, castle.getTroopCount() - 1);
					}
				}

				// 3. attack!
				attackWon = false;
				for (Castle castle : cNTK) {
					if (castle.getTroopCount() < 2)
						continue;

					Node<Castle> node = game.getMap().getGraph().getNode(castle);
					for (Edge<Castle> edge : game.getMap().getGraph().getEdges(node)) {
						Castle otherCastle = edge.getOtherNode(node).getValue();
						if (otherCastle.getOwner() != this && castle.getTroopCount() - 1 >= otherCastle.getTroopCount()) {
							AttackThread attackThread = game.startAttack(castle, otherCastle, castle.getTroopCount());
							if (fastForward)
								attackThread.fastForward();

							attackThread.join();
							attackWon = attackThread.getWinner() == this;
							break;
						}
					}

					if (attackWon)
						break;
				}
			} while (attackWon);
		}
	}

}
