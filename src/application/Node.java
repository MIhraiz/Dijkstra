package application;

public class Node {

	private City currentCity;
	private boolean known;
	private double distance;
	private City sourceCity;
	
	public Node(City currentCity, boolean known, double distance, City sourceCity) {
		super();
		this.currentCity = currentCity;
		this.known = known;
		this.distance = distance;
		this.sourceCity = sourceCity;
	}

	public City getCurrentCity() {
		return currentCity;
	}

	public void setCurrentCity(City currentCity) {
		this.currentCity = currentCity;
	}

	public boolean isKnown() {
		return known;
	}

	public void setKnown(boolean known) {
		this.known = known;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public City getSourceCity() {
		return sourceCity;
	}

	public void setSourceCity(City sourceCity) {
		this.sourceCity = sourceCity;
	}

	@Override
	public String toString() {
		return "Table [currentCity=" + currentCity + ", known=" + known + ", distance=" + distance + ", sourceCity="
				+ sourceCity + "]";
	}
}
