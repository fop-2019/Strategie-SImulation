package tests.student;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;

import org.junit.Test;

import base.Graph;
import base.Node;
import game.map.Castle;

public class GraphConnectionTest {

		
		
		
		@Test
		public void testAllNodesConnected1 () {
			Graph graph1 = new Graph();
			
			Point one1 = new Point(2,2);
			Castle one = new Castle(one1 , "one" , false);
			
			Point two2 = new Point(10,2);
			Castle two = new Castle(two2 , "two", false);
			
			Point three3 = new Point(12,32);
			Castle three = new Castle(three3 , "three", false);
			
			Point four4 = new Point(0,20);
			Castle four = new Castle(four4 , "four", false);
			
			Point five5 = new Point(6,78);
			Castle five = new Castle(five5 , "five", false);
			
			Point six6 = new Point(34,54);
			Castle six = new Castle(six6 , "six", false);
			
			Point seven7 = new Point(11,67);
			Castle seven = new Castle(seven7 , "seven", false);
			
			Point eight8 = new Point(19,13);
			Castle eight = new Castle(eight8 , "eight", false);
			
			Point nine9 = new Point(34,23);
			Castle nine = new Castle(nine9 , "nine", false);
			
			Point ten10 = new Point(99,1);
			Castle ten = new Castle(ten10 , "ten", false);
			

			
			graph1.addNode(one);
			graph1.addNode(two);
			graph1.addNode(three);
			graph1.addNode(four);
			graph1.addNode(five);
			graph1.addNode(six);
			graph1.addNode(seven);
			graph1.addNode(eight);
			graph1.addNode(nine);
			graph1.addNode(ten);
			

			graph1.addEdge(graph1.getNode(one), graph1.getNode(two));
			graph1.addEdge(graph1.getNode(two), graph1.getNode(three));
			graph1.addEdge(graph1.getNode(three), graph1.getNode(four));
			graph1.addEdge(graph1.getNode(four), graph1.getNode(five));
			graph1.addEdge(graph1.getNode(five), graph1.getNode(six));
			graph1.addEdge(graph1.getNode(six), graph1.getNode(seven));
			graph1.addEdge(graph1.getNode(seven), graph1.getNode(eight));
			graph1.addEdge(graph1.getNode(eight), graph1.getNode(nine));
			graph1.addEdge(graph1.getNode(nine), graph1.getNode(ten));
			
			assertTrue(graph1.allNodesConnected() , "true");
			
			
			
		}
		@Test
		public void testAllNodesConnected2 () {
			Graph graph1 = new Graph();
			
			Point one1 = new Point(2,2);
			Castle one = new Castle(one1 , "one", false);
			
			Point two2 = new Point(10,2);
			Castle two = new Castle(two2 , "two", false);
			
			Point three3 = new Point(12,32);
			Castle three = new Castle(three3 , "three", false);
			
			Point four4 = new Point(0,20);
			Castle four = new Castle(four4 , "four", false);
			
			Point five5 = new Point(6,78);
			Castle five = new Castle(five5 , "five", false);
			
			Point six6 = new Point(34,54);
			Castle six = new Castle(six6 , "six", false);
			
			Point seven7 = new Point(11,67);
			Castle seven = new Castle(seven7 , "seven", false);
			
			Point eight8 = new Point(19,13);
			Castle eight = new Castle(eight8 , "eight", false);
			
			Point nine9 = new Point(34,23);
			Castle nine = new Castle(nine9 , "nine", false);
			
			Point ten10 = new Point(99,1);
			Castle ten = new Castle(ten10 , "ten", false);
			

			
			graph1.addNode(one);
			graph1.addNode(two);
			graph1.addNode(three);
			graph1.addNode(four);
			graph1.addNode(five);
			graph1.addNode(six);
			graph1.addNode(seven);
			graph1.addNode(eight);
			graph1.addNode(nine);
			graph1.addNode(ten);
			

			graph1.addEdge(graph1.getNode(one), graph1.getNode(two));
			graph1.addEdge(graph1.getNode(two), graph1.getNode(three));
			graph1.addEdge(graph1.getNode(three), graph1.getNode(four));
			graph1.addEdge(graph1.getNode(four), graph1.getNode(five));
			graph1.addEdge(graph1.getNode(five), graph1.getNode(one));
		
			graph1.addEdge(graph1.getNode(six), graph1.getNode(seven));
			graph1.addEdge(graph1.getNode(seven), graph1.getNode(eight));
			graph1.addEdge(graph1.getNode(eight), graph1.getNode(nine));
			graph1.addEdge(graph1.getNode(nine), graph1.getNode(ten));
			graph1.addEdge(graph1.getNode(ten), graph1.getNode(six));
			
			assertTrue(graph1.allNodesConnected() , "true");
			
			
			
		}
	}

