package application;

public class Edge {

	private City city1;
	private City city2;
	private double distance;
	
	public Edge(City city1, City city2, double distance) {
		super();
		this.city1 = city1;
		this.city2 = city2;
		this.distance = distance;
	}

	public City getSourceCity() {
		return city1;
	}

	public void setSourceCity(City sourceCity) {
		this.city1 = sourceCity;
	}

	public City getTargetCity() {
		return city2;
	}

	public void setTargetCity(City targetCity) {
		this.city2 = targetCity;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "Edge [sourceCity=" + city1 + ", targetCity=" + city2 + ", distance=" + distance + "]";
	}
	
}
