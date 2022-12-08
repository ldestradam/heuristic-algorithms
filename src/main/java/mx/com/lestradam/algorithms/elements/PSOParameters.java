package mx.com.lestradam.algorithms.elements;

public class PSOParameters {
	
	private int numParticles;
	private int numIterations;
	private int inertia;
	private int accelerationC1;
	private int accelerationC2;
	
	public int getNumParticles() {
		return numParticles;
	}
	
	public void setNumParticles(int numParticles) {
		this.numParticles = numParticles;
	}
	
	public int getNumIterations() {
		return numIterations;
	}
	
	public void setNumIterations(int numIterations) {
		this.numIterations = numIterations;
	}
	
	public int getInertia() {
		return inertia;
	}
	
	public void setInertia(int inertia) {
		this.inertia = inertia;
	}
	
	public int getAccelerationC1() {
		return accelerationC1;
	}
	
	public void setAccelerationC1(int accelerationC1) {
		this.accelerationC1 = accelerationC1;
	}
	
	public int getAccelerationC2() {
		return accelerationC2;
	}
	
	public void setAccelerationC2(int accelerationC2) {
		this.accelerationC2 = accelerationC2;
	}

}
