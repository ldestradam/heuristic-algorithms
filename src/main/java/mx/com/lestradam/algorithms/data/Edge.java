package mx.com.lestradam.algorithms.data;

public class Edge {
	
	private long source;
	private long target;
	private long weight;
	
	public Edge(long source, long target, long weight) {
		this.source = source;
		this.target = target;
		this.weight = weight;
	}
	
	public long getSource() {
		return source;
	}
	
	public void setSource(long source) {
		this.source = source;
	}
	
	public long getTarget() {
		return target;
	}
	
	public void setTarget(long target) {
		this.target = target;
	}
	
	public long getWeight() {
		return weight;
	}
	
	public void setWeight(long weight) {
		this.weight = weight;
	}

}
