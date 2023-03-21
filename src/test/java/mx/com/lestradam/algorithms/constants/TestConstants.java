package mx.com.lestradam.algorithms.constants;

import java.util.Arrays;
import java.util.List;

import mx.com.lestradam.algorithms.elements.Edge;
import mx.com.lestradam.algorithms.elements.Node;

public class TestConstants {
	
	public static final int SET1_NUM_FLEETS = 3;
	public static final int SET2_NUM_FLEETS = 5;
	public static final int SET1_NUM_NODES = 5;
	
	public static final List<Node> SET1_NODES = Arrays.asList(
			new Node(1,"Sede 1",15),new Node(2,"Sede 2",7),new Node(3,"Sede 3",10),
			new Node(4,"Sede 4",5),new Node(5,"Sede 5",13),new Node(0,"Depot 1",0)
	);
	
	public static final List<Edge> SET1_EDGES = Arrays.asList(
			new Edge(0,1,5), new Edge(0,2,5), new Edge(0,3,5),
			new Edge(0,4,5), new Edge(0,5,5), new Edge(1,2,5),
			new Edge(1,3,5), new Edge(1,4,5), new Edge(1,5,5),
			new Edge(2,3,5), new Edge(2,4,5), new Edge(2,5,5),
			new Edge(3,4,5), new Edge(3,5,5), new Edge(4,5,5)
	);
	
	public static final List<Node> SET2_NODES = Arrays.asList(
		new Node(1,"Sede 1",15), new Node(2,"Sede 2",23), new Node(3,"Sede 3",38),
		new Node(4,"Sede 4",11), new Node(5,"Sede 5",10), new Node(0,"Depot 1",0)
	);
	
	public static final List<Edge> SET2_EDGES = Arrays.asList(
			new Edge(0,1,100), new Edge(0,2,159), new Edge(0,3,52), 
			new Edge(0,4,48), new Edge(0,5,23), new Edge(1,2,32), 
			new Edge(1,3,51), new Edge(1,4,15), new Edge(1,5,105), 
			new Edge(2,3,77), new Edge(2,4,102), new Edge(2,5,132), 
			new Edge(3,4,46), new Edge(3,5,84), new Edge(4,5,55)
	);
	
	private TestConstants() {
		throw new IllegalStateException("Constant class");
	}

}
