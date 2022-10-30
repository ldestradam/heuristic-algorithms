package mx.com.lestradam.algorithms.elements;

import java.util.List;

import mx.com.lestradam.algorithms.exceptions.DataException;

public class DataSet {
	
	private List<Node> nodes;
	private List<Edge> edges;
	private Node depot;
	
	public DataSet(List<Node> nodes, List<Edge> edges) {
		this.nodes = nodes;
		this.edges = edges;
		this.depot = nodes.stream().filter( node -> node.getQuantity() == 0L)
				.findFirst().orElseThrow(()-> new DataException("No depot provided"));
	}
	public List<Node> getNodes() {
		return nodes;
	}
	public List<Edge> getEdges() {
		return edges;
	}
	public Node getDepot() {
		return depot;
	}

}
