package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Scanner;

import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

public class Controller implements Initializable {

	private Label sourceCity;
	private Label targetCity;
	@FXML
	private Label coordinates;
	@FXML
	private Label showCity;
	@FXML
	private AnchorPane pane = new AnchorPane();

	@FXML
	private ScrollPane parent = new ScrollPane();
	@FXML
	private ChoiceBox<String> source;
	@FXML
	private ChoiceBox<String> target;
	@FXML
	private ListView<String> pathListView;
	@FXML
	private TextField distanceView;
	private ObservableList<String> pathCities = FXCollections.observableArrayList();
	private ObservableList<String> citiesList = FXCollections.observableArrayList();
	private ArrayList<City> cities = new ArrayList<>();
	private ArrayList<Edge> edges = new ArrayList<>();
	private ArrayList<Line> allLines = new ArrayList<>();
	private ArrayList<Line> lines = new ArrayList<>();
	private HashMap<String, Node> table = new HashMap<String, Node>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {
			getData();

			coordinates.setText("X : , Y : ");

			sourceCity = new Label("A");
			targetCity = new Label("B");
			sourceCity.setLayoutX(-20);
			targetCity.setLayoutX(-20);
			final double MAX_FONT_SIZE = 20.0;
			sourceCity.setFont(new Font(MAX_FONT_SIZE));
			sourceCity.setTextFill(Color.BLUE);
			targetCity.setFont(new Font(MAX_FONT_SIZE));
			targetCity.setTextFill(Color.BLUE);
			pane.getChildren().addAll(sourceCity, targetCity);

			citiesList.add("none");
			source.setValue("none");
			target.setValue("none");
			for (int i = 0; i < cities.size(); i++) {
				citiesList.add(cities.get(i).getName());
			}
			source.setItems(citiesList);
			target.setItems(citiesList);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		initalizeMap();
	}

	

	/*
	 * Reset the table
	 */

	/*
	 * fill the map with cities by putting each city on its right coordinates. each
	 * city with its name and a red circle that represents it
	 */
	public void initalizeMap() {
		for (int i = 0; i < cities.size(); i++) {
			// the circle that represents the city on the map
			Circle point = new Circle(5);

			// a label that hold the city name
			Label cityName = new Label(cities.get(i).getName());

			final double MAX_FONT_SIZE = 9.0;
			cityName.setFont(new Font(MAX_FONT_SIZE));

			// set circle coordinates
			point.setCenterX(cities.get(i).getCoordinateX());
			point.setCenterY(cities.get(i).getCoordinateY());

			// set label beside the circle
			cityName.setLayoutX(cities.get(i).getCoordinateX() - 10);
			cityName.setLayoutY(cities.get(i).getCoordinateY());

			point.setFill(Color.RED);
			Tooltip tooltip = new Tooltip(cities.get(i).toString());
			tooltip.setAutoFix(true);
			Tooltip.install(point, tooltip);

			// setting city circle to the circle above
			cities.get(i).setCircle(point);

			// add the circle and the label to the scene
			pane.getChildren().addAll(cities.get(i).getCircle(), cityName);
			String temp = cities.get(i).toString();

			/*
			 * Get city name and coordinates, and choose it in the choice box(if it is null)
			 * when clicking on the circle
			 */
			point.setOnMouseClicked(e -> {
				String cityInfo = temp.replaceAll("_", " ");
				showCity.setText(cityInfo);
				String[] sp = temp.split("[-]");
				if (source.getValue() == "none") {
					source.setValue(sp[0].trim());
				} else if (target.getValue() == "none") {
					target.setValue(sp[0].trim());
				}
			});

			point.setOnMouseEntered(e -> {
				point.setFill(Color.BLUE);
			});
			point.setOnMouseExited(e -> {
				point.setFill(Color.RED);
			});
		}
	}

	/*
	 * Run the program by clicking on the (Run) button and get the shortest path
	 */
	@FXML
	void run(ActionEvent event) {
		// if all choice boxes are not null
		if (source.getValue() != "none" && target.getValue() != "none") {
			String[] sourceXY = getCityCoordinates(source.getValue()).trim().split("[,]");
			String[] targetXY = getCityCoordinates(target.getValue()).trim().split("[,]");

			sourceCity.setLayoutX(Double.parseDouble(sourceXY[0]) - 10);
			sourceCity.setLayoutY(Double.parseDouble(sourceXY[1]) - 28);
			targetCity.setLayoutX(Double.parseDouble(targetXY[0]));
			targetCity.setLayoutY(Double.parseDouble(targetXY[1]) - 28);

			/*
			 * ----> run "shortest path"
			 */
			
			if(!getCity(source.getValue()).equals( getCity(target.getValue()))) {
				getShortestPath(getCity(source.getValue()), getCity(target.getValue()));
				for (int i = 0; i < lines.size(); i++) {
					lines.get(i).setStrokeWidth(2);
					pane.getChildren().add(lines.get(i));
				}
			}else {
				JOptionPane.showMessageDialog(null, "You are already in the city", "heeeey!", JOptionPane.PLAIN_MESSAGE);
			}
			

			

		} else {
			JOptionPane.showMessageDialog(null, "You have to choose two cities", "Worning!", JOptionPane.PLAIN_MESSAGE);
		}
		source.setValue("none");
		target.setValue("none");
	}

	/*
	 * Get shortest path between to cities
	 */
	private void getShortestPath(City sourceCity, City targetCity) {
		// reset the table to start new one

		buildTable(sourceCity, targetCity);

		// remove previous lines
		for (int i = 0; i < lines.size(); i++) {
			pane.getChildren().remove(lines.get(i));
		}
		// clear lines Array List
		lines.clear();

		// clear the Observable List that holds all cities between the target and source
		// cities
		pathCities.clear();
		// clear the list view that shows all cities between the target and source
		// cities
		pathListView.getItems().clear();
		// reset the total distance Text Field
		distanceView.setText("0.0");

		// check if there is path
		if(table.get(targetCity.getName()).getDistance() != Double.POSITIVE_INFINITY && table.get(targetCity.getName()).getDistance() != 0 ) {
			shortestPath(sourceCity, targetCity);
			DecimalFormat df = new DecimalFormat("###.##");
			Node t = table.get(targetCity.getName()); 
			distanceView.setText(df.format(t.getDistance()));

			/*
			 * Add all the cities that were found between the source and target cities
			 */
			pathListView.getItems().add(sourceCity.getName() + " (start) --->");
			for (int i = pathCities.size() - 1; i >= 0; i--) {
				if (i == 0) {
					pathListView.getItems().add(pathCities.get(i) + " (end)");
				} else {
					pathListView.getItems().add(pathCities.get(i) + " --->");
				}
			}
		}else {
			
			JOptionPane.showMessageDialog(null, "Ther is no path!", "Missing Edges!", JOptionPane.ERROR_MESSAGE);
		}

		
	}

	/*
	 * A recursive method to trace the path between two cities
	 */
	private void shortestPath(City sourceCity, City targetCity) {
		pathCities.add(targetCity.getName());

		Node t = table.get(targetCity.getName());
		
		if(t.getSourceCity() == null) {
			return;
		}
		if (t.getSourceCity() == sourceCity) {

			if (sourceCity != targetCity) { 
				lines.add(new Line(t.getSourceCity().getCoordinateX(),t.getSourceCity().getCoordinateY(), targetCity.getCoordinateX(), targetCity.getCoordinateY()));
			}
			return;
		}
	
		lines.add(new Line(t.getSourceCity().getCoordinateX(),t.getSourceCity().getCoordinateY(), targetCity.getCoordinateX(), targetCity.getCoordinateY()));
		shortestPath(sourceCity, t.getSourceCity());
	}


	/*
	 * Build the hash table by applying the dijkstra algorithm
	 */
	private void buildTable(City source, City targetCity) {
		
		table.clear();
		for (City i : cities) {
			table.put(i.getName(),new Node(i, false, Double.POSITIVE_INFINITY, null));
		}
		
		TableCompare comp = new TableCompare();
		Queue<Node> q = new PriorityQueue<Node>(10, comp);
		
		Node node = table.get(source.getName());
		node.setDistance(0.0);
		node.setKnown(true);
		q.add(node);

		while (!q.isEmpty()) {
			Node temp = q.poll();
			temp.setKnown(true);
			if(temp.getCurrentCity() == targetCity) {
				break;
			}
			ArrayList<Adjacent> adj = temp.getCurrentCity().getAdjacent();
			
			for(Adjacent c: adj) {
				Node t = table.get(c.getCity().getName());
				if(t.isKnown()) {
					continue;
				}
				
				// && its distance >= the distance between it and the current source city + all previous path distance
				// && are adjacent
				double newDis = c.getDistance() + temp.getDistance();
				if(newDis < t.getDistance()) {
					t.setSourceCity(temp.getCurrentCity());
					t.setDistance(newDis);
				}
				q.add(t);
			}
			
		}
	}

	/*
	 * Get City coordinates by its name from the cities Array List
	 */
	private String getCityCoordinates(String cityName) {
		for (int i = 0; i < citiesList.size(); i++) {
			if (cities.get(i).getName() == cityName) {
				return cities.get(i).getCoordinateX() + "," + cities.get(i).getCoordinateY();
			}
		}
		return null;
	}

	/*
	 * Get the City by its name from the cities Array List
	 */
	private City getCity(String cityName) {

		for (int i = 0; i < cities.size(); i++) {
			if (cities.get(i).getName().equalsIgnoreCase(cityName)) {
				return cities.get(i);
			}
		}
		return null;
	}
	
	private void getData() throws FileNotFoundException {

		getCities();
		getDistances();
		addAdjacents();

	}

	/*
	 * Read Cities file and store its content to the cities Array List
	 */
	private void getCities() throws FileNotFoundException {
		File file = new File("cities1");
		Scanner input = new Scanner(file);
		while (input.hasNextLine()) {
			String str = input.nextLine();
			String[] spStr = str.trim().replaceAll("\\s", " ").split("[ ]");

			cities.add(new City(Double.parseDouble(spStr[1]), Double.parseDouble(spStr[2]), spStr[0], new Circle()));

		}
		input.close();

	}

	/*
	 * Read Distances file and store its content to the Edges Array List
	 */
	private void getDistances() throws FileNotFoundException {
		File file = new File("distance");
		Scanner input = new Scanner(file);

		while (input.hasNextLine()) {
			String[] spStr = input.nextLine().trim().replaceAll("\\s", " ").split("[ ]");
			edges.add(new Edge(getCity(spStr[0]), getCity(spStr[1]), Double.parseDouble(spStr[2])));
		}

		input.close();
	}

	/*
	 * Get each city and set all its adjacent cities
	 */
	private void addAdjacents() {
		for (int i = 0; i < cities.size(); i++) {
			for (int j = 0; j < edges.size(); j++) {
				if (cities.get(i).getName().equalsIgnoreCase(edges.get(j).getSourceCity().getName())) {
					City c = edges.get(j).getTargetCity();
					Adjacent n = new Adjacent(c, edges.get(j).getDistance());
					cities.get(i).getAdjacent().add(n);
				} else if (cities.get(i).getName().equalsIgnoreCase(edges.get(j).getTargetCity().getName())) {
					City c = edges.get(j).getSourceCity();
					Adjacent n = new Adjacent(c, edges.get(j).getDistance());
					cities.get(i).getAdjacent().add(n);
				}
			}
		}
	}


	/*
	 * Remove all the paths on the map
	 */
	@FXML
	void hidePaths(ActionEvent event) {
		try {
			for (int i = 0; i < edges.size(); i++) {
				pane.getChildren().remove(allLines.get(i));
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Ther are no paths!", "Worning!", JOptionPane.PLAIN_MESSAGE);
		}
	}

	/*
	 * Show all the paths on the map
	 */
	@FXML
	void showPaths(ActionEvent event) throws FileNotFoundException {
		try {
			for (int i = 0; i < edges.size(); i++) {
				double startX = edges.get(i).getSourceCity().getCoordinateX();
				double startY = edges.get(i).getSourceCity().getCoordinateY();
				double endX = edges.get(i).getTargetCity().getCoordinateX();
				double endY = edges.get(i).getTargetCity().getCoordinateY();
				allLines.add(new Line(startX, startY, endX, endY));
				pane.getChildren().add(allLines.get(i));
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Paths already shown!", "Worning!", JOptionPane.PLAIN_MESSAGE);
		}
	}

	/*
	 * Display mouse coordinates
	 */
	@FXML
	void mouseCoordinates(MouseEvent event) {
		coordinates.setText("X : " + event.getX() + " * Y : " + event.getY());
	}

	/*
	 * Exit the Window
	 */
	@FXML
	void close(ActionEvent event) {
		System.exit(0);
	}

	@FXML
	void help(ActionEvent event) {
		String aboutString = "Palestine Map";
		JOptionPane.showMessageDialog(null, aboutString, "About...", JOptionPane.PLAIN_MESSAGE);
	}

}
