package application;

import java.util.Comparator;

public class TableCompare implements Comparator<Node>{

	@Override
	public int compare(Node t1, Node t2) {

		return (int) (t1.getDistance() - t2.getDistance());
	}

}
