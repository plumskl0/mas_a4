package mas_a4;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Bote {

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;

	private int sender;
	private Koordinator coordinator;

	private Ziel ziel;

	public Bote(ContinuousSpace<Object> space, Grid<Object> grid, int id) {
		this.space = space;
		this.grid = grid;
		this.sender = id;
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void run() {
		if (ziel != null) {
			GridPoint pt = grid.getLocation(ziel);
			moveTowards(pt);
		}
	}

	public void moveTowards(GridPoint pt) {
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			space.moveByVector(this, 1, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
		}
		checkArrived(pt);
	}

	private void checkArrived(GridPoint pt) {

		GridPoint myPos = grid.getLocation(this);

//		System.out.println("Bote arrived? " + grid.getDistance(myPos, pt));
		if (grid.getDistance(myPos, pt) <= 1) {
			// Nachricht an Koordinator lieferung abgeschlossen
			// An Ursprung beamen
		}
	}

	public int getSender() {
		return this.sender;
	}
	
	public void setCoordinator(Koordinator c) {
		this.coordinator = c;
	}

}
