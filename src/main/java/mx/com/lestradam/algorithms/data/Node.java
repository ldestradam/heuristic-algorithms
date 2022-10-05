package mx.com.lestradam.algorithms.data;

public class Node {
	
	private long id;
	private String label;
	private long quantity;
	
	public Node(int id, String label, long quantity) {
		this.id = id;
		this.label = label;
		this.quantity = quantity;
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "Node [id=" + id + ", label=" + label + ", quantity=" + quantity + "]";
	}

}
