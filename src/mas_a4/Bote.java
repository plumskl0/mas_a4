package mas_a4;

import java.util.ArrayList;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Bote {

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;

	private Lieferkosten[] lieferKosten;

	private int sender;
	private Koordinator coordinator;

	private ArrayList<Ziel> ziele;
	private Ziel aktZiel;

	private GridPoint startPoint;

	public Bote(ContinuousSpace<Object> space, Grid<Object> grid, int id) {
		this.space = space;
		this.grid = grid;
		this.sender = id;
		ziele = new ArrayList<Ziel>();
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void run() {
		if (startPoint == null)
			startPoint = grid.getLocation(this);

		if (ziele.size() > 0 || aktZiel != null) {

			if (aktZiel == null)
				aktZiel = ziele.remove(0);

			if (aktZiel != null) {
				GridPoint pt = grid.getLocation(aktZiel);
				moveTowards(pt);
			}
		} else {
			if (coordinator.messagesAvailable(sender)) {
				FIPA_Message m = coordinator.getMessage(sender);

				switch (m.getContent()) {
				case Content.HOLE_LIEFERUNG:
					calculateLieferKosten();
					break;

				default:
					break;
				}
			}
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

		if (grid.getDistance(myPos, pt) <= 1) {
			String bewertung = null;

			// Nachricht an Koordinator über erhaltene Bewertung
			// bewertung = bewertungErhalten();
			if (bewertungErhalten()) {
				bewertung = Content.POSITIV;
			} else {
				bewertung = Content.NEGATIV;
			}

			// Nachricht, dass eine Lieferung beendet ist.
			coordinator.send(sender, coordinator.getSender(), FIPA_Performative.INFORM, bewertung);

			// An Ursprung beamen
			aktZiel = null;
			beamToStart();
		}
	}

	private void calculateLieferKosten() {
		ArrayList<Ziel> lieferungen = coordinator.getLieferungen();
		int anzLieferungen = lieferungen.size();
		lieferKosten = new Lieferkosten[anzLieferungen];

		GridPoint pt = grid.getLocation(this);

		for (int i = 0; i < anzLieferungen; i++) {

			Lieferkosten lk = new Lieferkosten();

			lk.idBote = sender;
			lk.ziel = lieferungen.get(i);

			GridPoint zielPt = grid.getLocation(lieferungen.get(i));
			lk.kosten = (int) grid.getDistance(pt, zielPt);

			lieferKosten[i] = lk;
		}

		coordinator.send(sender, coordinator.getSender(), FIPA_Performative.INFORM, Content.HOLE_LIEFERKOSTEN);
	}

	public void addLieferung(Ziel z) {
		ziele.add(z);
	}

	public Lieferkosten[] getLieferKosten() {
		return lieferKosten;
	}

	public int getSender() {
		return this.sender;
	}

	public void setCoordinator(Koordinator c) {
		this.coordinator = c;
	}

	private boolean bewertungErhalten() {
		int num = (int) ((Math.random()) * 100 + 1);
		switch (this.sender) {
		case 0:
			if (num <= 89) {
				return true;
			} else {
				return false;
			}
		case 1:
			if (num <= 74) {
				return true;
			} else {
				return false;
			}
		case 2:
			if (num <= 94) {
				return true;
			} else {
				return false;
			}
		case 3:
			if (num <= 71) {
				return true;
			} else {
				return false;
			}
		default:
			System.out.println("Fehler in bewertungErhalten1");
			break;
		}
		System.out.println("Fehler in bewertungErhalten2");
		return false;
	}

	private void beamToStart() {
		int x = startPoint.getX();
		int y = startPoint.getY();
		space.moveTo(this, (int) x, (int) y);
		grid.moveTo(this, (int) x, (int) y);
	}

}
