package application;


public class Adjacent{
	private City city;
	private double distance;

	public Adjacent(City city, double distance) {
		this.setCity(city);
		this.distance = distance;
		
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}
	
	

}
