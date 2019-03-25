package base;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import game.map.Castle;

/**
 * Diese Klasse representiert einen generischen Graphen mit einer Liste aus Knoten und Kanten.
 *
 * @param <T> Die zugrundeliegende Datenstruktur, beispielsweise {@link game.map.Castle}
 */
public class Graph<T> {

    private List<Edge<T>> edges;
    private List<Node<T>> nodes;

    /**
     * Konstruktor für einen neuen, leeren Graphen
     */
    public Graph() {
        this.nodes = new ArrayList<>();
        this.edges = new LinkedList<>();
    }

    /**
     * Einen neuen Knoten zum Graphen hinzufügen
     * @param value Der Wert des Knotens
     * @return Der erstellte Knoten
     */
    public Node<T> addNode(T value) {
        Node<T> node = new Node<>(value);
        this.nodes.add(node);
        return node;
    }

    /**
     * Eine neue Kante zwischen zwei Knoten hinzufügen. Sollte die Kante schon existieren, wird die vorhandene Kante zurückgegeben.
     * @param nodeA Der erste Knoten
     * @param nodeB Der zweite Knoten
     * @return Die erstellte oder bereits vorhandene Kante zwischen beiden gegebenen Knoten
     */
    public Edge<T> addEdge(Node<T> nodeA, Node<T> nodeB) {
        Edge<T> edge = getEdge(nodeA, nodeB);
        if(edge != null) {
            return edge;
        }

        edge = new Edge<>(nodeA, nodeB);
        this.edges.add(edge);
        return edge;
    }

    /**
     * Gibt die Liste aller Knoten zurück
     * @return die Liste aller Knoten
     */
    public List<Node<T>> getNodes() {
        return this.nodes;
    }

    /**
     * Gibt die Liste aller Kanten zurück
     * @return die Liste aller Kanten
     */
    public List<Edge<T>> getEdges() {
        return this.edges;
    }

    /**
     * Diese Methode gibt alle Werte der Knoten in einer Liste mittels Streams zurück.
     * @see java.util.stream.Stream#map(Function)
     * @see java.util.stream.Stream#collect(Collector)
     * @return Eine Liste aller Knotenwerte
     */
    public List<T> getAllValues() {
        // TODO: Graph<T>#getAllValues()
         return  nodes.stream().map(n -> n.getValue()).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Diese Methode gibt alle Kanten eines Knotens als Liste mittels Streams zurück.
     * @param node Der Knoten für die dazugehörigen Kanten
     * @see java.util.stream.Stream#filter(Predicate)
     * @see java.util.stream.Stream#collect(Collector)
     * @return Die Liste aller zum Knoten zugehörigen Kanten
     */
    public List<Edge<T>> getEdges(Node<T> node) {
        // TODO: Graph<T>#getEdges(Node<T>)

        return edges.stream().filter(t->t.contains(node)).collect(Collectors.toCollection(ArrayList::new));	
    }

    /**
     * Diese Methode sucht eine Kante zwischen beiden angegebenen Knoten und gibt diese zurück
     * oder null, falls diese Kante nicht existiert
     * @param nodeA Der erste Knoten
     * @param nodeB Der zweite Knoten
     * @return Die Kante zwischen beiden Knoten oder null
     */
    public Edge<T> getEdge(Node<T> nodeA, Node<T> nodeB) {
		List<Edge<T>> temp = getEdges(nodeA);		
		return 	temp.stream().filter(t -> t.contains(nodeB)).findFirst().orElse(null);
    }

    /**
     * Gibt den ersten Knoten mit dem angegebenen Wert zurück oder null, falls dieser nicht gefunden wurde
     * @param value Der zu suchende Wert
     * @return Ein Knoten mit dem angegebenen Wert oder null
     */
    public Node<T> getNode(T value) {
        // TODO: Graph<T>#getNode(T)
         return nodes.stream().filter(n -> n.getValue().equals(value)).findFirst().orElse(null);
    }
   
    /**
     * Returns list of all connected (indirectly as well) nodes of given nodes.
     * @param c node 
     * @return list of nodes
     */
    public ArrayList<Node> findConnectedCastles (Node<T> c) {
    	boolean inMain = false;
		Node <T> currentCastle = c;
		ArrayList<Node> visitedCastle = new ArrayList<Node>();		
		int j = 0;
		while(!inMain) {
			if (getEdges(currentCastle).size() != 0) {
				for (int i = 0; i < getEdges(currentCastle).size() ; i++) {					
					int k = visitedCastle.size() - 1;
					
					if (!visitedCastle.contains(getEdges(currentCastle).get(i).getOtherNode(currentCastle))) {						
						
						if(!visitedCastle.contains(currentCastle)) {
							visitedCastle.add(currentCastle);
							j = 0;
						}							
						
						currentCastle =  getEdges(currentCastle).get(i).getOtherNode(currentCastle);
						
						if(!visitedCastle.contains(currentCastle)) {
							 visitedCastle.add(currentCastle);
							 j = 0;
						}							
						
						i = -1;
					}
					else if ( j > k &&  i == getEdges(currentCastle).size() - 1) {
							
						return visitedCastle;
					}
					else if (visitedCastle.contains(getEdges(currentCastle).get(i).getOtherNode(currentCastle)) && i == getEdges(currentCastle).size() - 1) {
							
						if(!visitedCastle.contains(currentCastle)) {							 	
							visitedCastle.add(currentCastle);							 	
							j = 0;							 
						}							 
							 
						currentCastle =  visitedCastle.get(k - j);
							
						if(!visitedCastle.contains(currentCastle)) {						 	
							visitedCastle.add(currentCastle);							 
							j = 0;							
						}							
						i = 0;							
						j++;									
					}	
					else if (visitedCastle.size() == nodes.size()) {							
						return visitedCastle;							 
					}
				}	
			}	
		}   	  
		return null;
    }
        
        
        /**
         * Überprüft, ob alle Knoten in dem Graphen erreichbar sind.
         * @return true, wenn alle Knoten erreichbar sind
         */
        public boolean allNodesConnected() {	
 
        	if(findConnectedCastles(nodes.get(0)).size() == nodes.size()) {
        		return true;
        	}

        	else {
        		ArrayList<Node> forWhile =  findConnectedCastles(getNodes().get(0));

        		while(forWhile.size() != nodes.size()) {       	
	    			ArrayList<Node> visitedCastle = findConnectedCastles(getNodes().get(0));
	    			ArrayList<Node> toConnect = null;

	    			for (int k = 0; k < getNodes().size(); k++) {
	    	    		if (!visitedCastle.contains(getNodes().get(k))) {
	    	    			 toConnect =	findConnectedCastles(getNodes().get(k));
	    	    		}	    	    	
	    	    	}
	    	    	Node <T> Castle1 = visitedCastle.get(0);
	    	    	Node <T> Castle2 = toConnect.get(0);

	    	    	for (int l = 0; l < visitedCastle.size(); l++) {	    	    		
	    	    		Node <T> cCastle = visitedCastle.get(l);

	    	    		for (int h = 0; h < toConnect.size(); h++) {
	    	    			Node <T> fCastle = toConnect.get(h);	    	    			

	    	    			if (((Castle) cCastle.getValue()).distance((Castle) fCastle.getValue()) <= ((Castle) Castle1.getValue()).distance((Castle) Castle2.getValue()) && !Castle1.equals(Castle2) && !cCastle.equals(fCastle)) {
	    	    				Castle1 = cCastle;
	    	    				Castle2 = fCastle;
	    	    			}	    	    			
	    		    	}
	    	    	}

	    	    	addEdge(Castle1, Castle2);
	    	    	forWhile =  findConnectedCastles(getNodes().get(0));
        		}
        		return true;
        	}
        }
}
