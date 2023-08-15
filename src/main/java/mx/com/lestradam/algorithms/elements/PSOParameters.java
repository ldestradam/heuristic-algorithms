package mx.com.lestradam.algorithms.elements;

public class PSOParameters {
	
	private int numParticles;
	private int numIterations;
	private float inertia;
	private float accelerationC1;
	private float accelerationC2;
	
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
	
	public float getInertia() {
		return inertia;
	}
	
	public void setInertia(float inertia) {
		this.inertia = inertia;
	}
	
	public float getAccelerationC1() {
		return accelerationC1;
	}
	
	public void setAccelerationC1(float accelerationC1) {
		this.accelerationC1 = accelerationC1;
	}
	
	public float getAccelerationC2() {
		return accelerationC2;
	}
	
	public void setAccelerationC2(float accelerationC2) {
		this.accelerationC2 = accelerationC2;
	}

}
