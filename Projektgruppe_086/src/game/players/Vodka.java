package game.players;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
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
import game.map.MapSize;
import game.map.PathFinding;
import gui.AttackThread;
import gui.components.MapPanel;
import gui.views.GameView;

public class Vodka extends AI {

	public Vodka(String name, Color color) {
		super(name, color);
	}

	private Game game;
	private int negativeStrategicValue, currentIndex, targetKingdom;

	/**
	 * checks if two castles are connected
	 * 
	 * @param castleA
	 * @param castleB
	 * @param game    current game
	 * @return true if connected, false if not
	 */
	private boolean areConnected(Castle castleA, Castle castleB, Game game) {
		PathFinding pF = new PathFinding(game.getMap().getGraph(), castleA, MapPanel.Action.MOVING,
				game.getCurrentPlayer());
		pF.run();
		if (pF.getPath(castleB) == null)
			return false;
		else {
			return true;
		}
	}

	/**
	 * gets all the Castles of given list that are connected to myCastle
	 * 
	 * @param myCastle
	 * @param castles  list of castles
	 * @param game     current game
	 * @return castles filtered by connection to myCastle
	 */
	private List<Castle> getConnectedCastles(Castle myCastle, List<Castle> castles, Game game) {
		return castles.stream().filter(c -> areConnected(c, myCastle, game))
				.collect((Collectors.toCollection(ArrayList::new)));
	}

	/**
	 * gets the castle from a given collection of castles with the fewest troopCount
	 * 
	 * @param castles you want to get the castle with the fewest troops from
	 * @return castle with smallest troopCount out of castles
	 */
	private Castle getCastleWithFewestTroops(List<Castle> castles) {
		Castle fewestTroops = castles.get(0);
		for (Castle castle : castles) {
			if (castle.getTroopCount() < fewestTroops.getTroopCount()) {
				fewestTroops = castle;
			}
		}

		return fewestTroops;
	}

	/**
	 * gets the castle from a given collection of castles with the highest
	 * troopCount
	 * 
	 * @param castles you want to get the castle with the most troops from
	 * @return castle with highest troopCount out of castles
	 */
	@SuppressWarnings("unused")
	private Castle getCastleWithMostTroops(List<Castle> castles) {
		Castle maxTroops = castles.get(0);
		for (Castle castle : castles) {
			if (castle.getTroopCount() > maxTroops.getTroopCount()) {
				maxTroops = castle;
			}
		}
		return maxTroops;
	}

	/**
	 * gets the castle from the given castles list with the fewest enemy castles
	 * connected to
	 * 
	 * @param myCastles castles the player owns
	 * @param graph     containing all castles and edges to check the connections
	 * @return castle with fewest enemy castles connected
	 */
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

	/**
	 * gets the castle from the given castles list with the most enemy castles
	 * connected to
	 * 
	 * @param myCastles castles the player owns
	 * @param graph     containing all castles and edges to check the connections
	 * @return castle with most enemy castles connected
	 */
	private Castle getCastleWithMostEnemyConnections(List<Castle> myCastles, Graph<Castle> graph) {
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
		return myCastles.get(enemyCastlesNearby.indexOf(Collections.max(enemyCastlesNearby)));
	}

	/**
	 * gets all the castles with an enemy castle connected to them
	 * 
	 * @param graph graph containing all castles and edges to check the connections
	 * @param game  current Game
	 * @return castles with at least one enemy castle connected to them
	 */
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

	/**
	 * gets all Kingdoms which can be reached from the castles of the AI
	 * 
	 * @param game current game
	 * @return Kingdoms that have a connection to the AIs castles
	 */
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

	/**
	 * gets all enemy Castles sorted by their kingdom
	 * 
	 * @param game current Game
	 * @return enemy Castles sorted by Kingdom
	 */
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

	/**
	 * gets all AI owned castles that are connected to a castle of the target
	 * Kingdom
	 * 
	 * @param game          current Game
	 * @param targetKingdom type of the targeted Kingdom
	 * @return all AI owned castles connected to the target kingdom
	 */
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

	/**
	 * gets the kingdom not owned by the AI with the smallest amount of enemy
	 * castles
	 * 
	 * @param game current Game
	 * @return type of the targeted Kingdom
	 */
	private int getTargetKingdom(Game game) {
		targetKingdom = getConnectedKingdoms(game).get(0);
		for (int i = 0; i < getEnemyCastlesByKingdom(game).size(); i++) {
			if (game.getMap().getKingdoms().get(i).getOwner() == this || !getConnectedKingdoms(game).contains(i)) {
				continue;
			}
			if (getEnemyCastlesByKingdom(game).get(i).size() < getEnemyCastlesByKingdom(game).get(targetKingdom).size()
					+ 1) {
				targetKingdom = i;
			}
		}
		return targetKingdom;
	}

	@Override
	protected void actions(Game game) throws InterruptedException {
		if (game.getRound() == 1) {
			this.game = game;

			// Chooses the castle with the best strategic value
			int availableCastles = game.getMap().getCastles().size();
			while (availableCastles > 0 && getRemainingTroops() > 0) {
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
				// 1. Calculates the strategic Values of the Kingdoms, the smaller the value the
				// more attractive it is to be get chosen
				// The value is calculated by adding one for each enemy castle or each castle
				// with no owner and subtracting one for each castle owned by the AI
				for (int i = 0; i < game.getMap().getKingdoms().size(); i++) {
					currentIndex = i;
					negativeStrategicValue = 0;
					game.getMap().getCastles().stream().filter(c -> c.getKingdom().getType() == currentIndex)
							.forEach(c1 -> {
								if (c1.getOwner() != this || c1.getOwner() == null) {
									negativeStrategicValue++;
								}
								if (c1.getOwner() == this) {
									negativeStrategicValue--;
								}
							});
					if (castlesInKingdoms.get(i).size() == 0) {
						strategicValue.add(game.getMap().getCastles().size() + 1);
					} else {
						strategicValue.add(negativeStrategicValue);
					}
				}

				int mostAttractiveKingdom = strategicValue.indexOf(Collections.min(strategicValue));

				// 2. chooses the most attractive Castle in the most attractive Kingdom

				List<Castle> castlesToChooseFrom = castlesInKingdoms.get(mostAttractiveKingdom).stream()
						.map(c -> game.getMap().getCastles().get(c)).collect(Collectors.toCollection(ArrayList::new));
				availableCastles--;
				List<Castle> cACL = castlesToChooseFrom.stream()
						.filter(c -> !getConnectedCastles(c, this.getCastles(game), game).isEmpty())
						.collect(Collectors.toCollection(ArrayList::new));
				if (cACL.isEmpty()) {
					game.chooseCastle(
							getCastleWithFewestEnemyConnections(castlesToChooseFrom, game.getMap().getGraph()), this);
				} else {
					game.chooseCastle(
							getCastleWithFewestEnemyConnections(
									(castlesToChooseFrom.stream()
											.filter(c -> !getConnectedCastles(c, this.getCastles(game), game).isEmpty())
											.collect(Collectors.toCollection(ArrayList::new))),
									game.getMap().getGraph()),
							this);
				}
			}
		} else {
			int attackTrigger = 0;
			
			if (game.getMapSize() == MapSize.SMALL) {
				attackTrigger = 2;
			} else {
				attackTrigger = 1;
			}

			targetKingdom = getTargetKingdom(game);

			// Distribute remaining troops

			while (this.getRemainingTroops() > 0) {
				Castle fewestTroops = getCastleWithFewestTroops(getCastlesNearTargetKingdom(game, targetKingdom));
				sleep(500);
				game.addTroops(this, fewestTroops, 1);
			}

			boolean primaryAttackWon;
			boolean secundaryAttackWon;

			do {
				// 1. Move Troops
				if (getConnectedKingdoms(game).isEmpty()) {
					return;
				}
				targetKingdom = getTargetKingdom(game);
				List<Castle> cNTK = getCastlesNearTargetKingdom(game, targetKingdom);
				List<Castle> castleOnNotAttractiveBorder = new ArrayList<Castle>();
				for (Castle castle : this.getCastles(game)) {
					if (!cNTK.contains(castle) && castle.getTroopCount() > 1
							&& !getConnectedCastles(castle, cNTK, game).isEmpty()) {
						Castle fewestTroops = getCastleWithFewestTroops(getConnectedCastles(castle, cNTK, game));
						game.moveTroops(castle, fewestTroops, castle.getTroopCount() - 1);
					}
					if (!getConnectedCastles(castle, cNTK, game).isEmpty()) {
						castleOnNotAttractiveBorder.add(castle);
					}
				}

				for (Castle castle : this.getCastles(game)) {
					if (!castleOnNotAttractiveBorder.contains(castle) && castle.getTroopCount() > 1
							&& !getConnectedCastles(castle, castleOnNotAttractiveBorder, game).isEmpty()) {
						Castle fewestTroops = getCastleWithFewestTroops(
								getConnectedCastles(castle, castleOnNotAttractiveBorder, game));
						game.moveTroops(castle, fewestTroops, castle.getTroopCount());
					}
				}

				// 2. attack targetKingdom
				primaryAttackWon = false;
				for (Castle castle : cNTK) {
					if (castle.getTroopCount() < 2)
						continue;

					Node<Castle> node = game.getMap().getGraph().getNode(castle);
					for (Edge<Castle> edge : game.getMap().getGraph().getEdges(node)) {
						Castle otherCastle = edge.getOtherNode(node).getValue();
						if (otherCastle.getOwner() != this
								&& castle.getTroopCount() - attackTrigger >= otherCastle.getTroopCount()) {
							AttackThread attackThread = game.startAttack(castle, otherCastle, castle.getTroopCount());
							if (fastForward)
								attackThread.fastForward();

							attackThread.join();
							primaryAttackWon = attackThread.getWinner() == this;
							break;
						}
					}

					if (primaryAttackWon)
						break;
				}

				// 3. attack on different borders
				secundaryAttackWon = false;
				for (Castle castle : castleOnNotAttractiveBorder) {
					if (castle.getTroopCount() < 2)
						continue;

					Node<Castle> node = game.getMap().getGraph().getNode(castle);
					for (Edge<Castle> edge : game.getMap().getGraph().getEdges(node)) {
						Castle otherCastle = edge.getOtherNode(node).getValue();
						if (otherCastle.getOwner() != this
								&& castle.getTroopCount() - attackTrigger >= otherCastle.getTroopCount()) {
							AttackThread attackThread = game.startAttack(castle, otherCastle, castle.getTroopCount());
							if (fastForward)
								attackThread.fastForward();

							attackThread.join();
							secundaryAttackWon = attackThread.getWinner() == this;
							break;
						}
					}

					if (secundaryAttackWon)
						break;
				}

			} while (secundaryAttackWon || primaryAttackWon);

		}
	}

	@Override
	public boolean useJoker(Joker type) {

		int tK = this.getTargetKingdom(game);
		if (type.getClass() == RevolutionJoker.class) {
			if (game.getMap().getCastles().stream().filter(c -> c.getKingdom().getType() == tK)
					.filter(kC -> kC.getOwner() != this).collect(Collectors.toCollection(ArrayList::new)).size() < 3) {
				return Math.random() < 0.9;
			}
			return false;
		} else if (type.getClass() == DiceJoker.class) {
			if (game.getMap().getCastles().stream().filter(c -> c.getKingdom().getType() == tK)
					.filter(kC -> kC.getOwner() != this).collect(Collectors.toCollection(ArrayList::new)).size() < 3) {
				return Math.random() < 0.9;
			}
		} else {
			if (game.getMap().getCastles().stream().filter(c -> c.getKingdom().getType() == tK)
					.filter(kC -> kC.getOwner() != this).collect(Collectors.toCollection(ArrayList::new)).size() < 3) {
				return Math.random() < 0.8;
			}
		}
		return false;
	}

	@Override
	public Joker chooseJoker(Game game) {
		if (game.getMapSize() == MapSize.SMALL) {
			this.setJoker(new TroopsJoker());
			return this.getJoker();
		} else if (game.getMapSize() == MapSize.MEDIUM) {
			double rnd = Math.random();
			if (rnd <= 0.2) {
				this.setJoker(new RevolutionJoker());
				return this.getJoker();
			} else if (rnd <= 0.3) {
				this.setJoker(new DiceJoker());
				return this.getJoker();
			} else {
				this.setJoker(new TroopsJoker());
				return this.getJoker();
			}
		} else {
			double rnd = Math.random();
			if (rnd <= 0.1) {
				this.setJoker(new RevolutionJoker());
				return this.getJoker();
			} else if (rnd <= 0.2) {
				this.setJoker(new DiceJoker());
				return this.getJoker();
			} else {
				this.setJoker(new TroopsJoker());
				return this.getJoker();
			}
		}
	}

	@Override
	public void useRevolution(Game game, GameInterface gameInterface) {

//		for (Castle castle : game.getMap().getCastles()) {
//			
//		}
		int tK = this.getTargetKingdom(game);
		Castle selectedCastle = this.getCastleWithMostEnemyConnections(game.getMap().getCastles().stream()
				.filter(c -> c.getKingdom().getType() == tK).filter(kC -> kC.getOwner() != this).collect(Collectors.toCollection(ArrayList::new)),
				game.getMap().getGraph());

		((GameView) gameInterface).logLine("%PLAYER% hat den RevolutionJoker benutzt und hat die Herrschaft über "
				+ selectedCastle.getName() + " erhalten.", game.getCurrentPlayer());
//		System.out.println(game.getCurrentPlayer().getName() + " revolution at " + selectedCastle.getName() + " VODKA");
		game.getCurrentPlayer().getJoker().use();
		Player opp = selectedCastle.getOwner();
		opp.addTroops(selectedCastle.getTroopCount() - 1);
		selectedCastle.removeTroops(selectedCastle.getTroopCount());
		selectedCastle.setOwner(game.getCurrentPlayer());
		selectedCastle.addTroops(1);

	}

}