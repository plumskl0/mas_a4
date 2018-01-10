package mas_a4;

import java.util.ArrayList;

import repast.simphony.engine.schedule.ScheduledMethod;

public class Koordinator {

	private ArrayList<FIPA_Message> messageList;
	private ArrayList<Ziel> zielList;
	private ArrayList<Bote> boteList;

	private int tage;
	private int aktTag;
	private int sender;

	private int aktProc = 0;
	
	public Koordinator(ArrayList<Ziel> ziele, int tage) {
		this.zielList = ziele;
		this.tage = tage;
		this.messageList = new ArrayList<FIPA_Message>();
		this.boteList = new ArrayList<Bote>();
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void run() {
		if (aktTag < tage) {
			switch (aktProc) {
			case 0:
				// Ziele an Boten mitteilen
				zieleAnBoten();
				break;
			case 1:
				// Lieferkosten sammeln
				break;
			case 2:
				// Geringste Lieferkosten bestimmen mit Trust
				break;
			case 3:
				// Lieferungen den Boten zuweisen
				break;
			case 4:
				// Auswertung von Kunden Feedback
				break;
			case 5:
				// Nachdem alle Feedbacks ausgwertet wurden, muss der Tag erhöhrt werden
				resetDay();
				break;
			default:
				break;
			}
		}
	}

	private void zieleAnBoten() {
		boteList.stream().forEach(b -> {
			send(sender, b.getSender(), FIPA_Performative.INFORM, Content.HOLE_LIEFERUNG);
		});
	}

	public void addBote(Bote b) {
		boteList.add(b);
	}

	public int getSender() {
		return sender;
	}
	
	private void resetDay() {
		aktTag++;
		aktProc = 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	/// Message Stuff
	public void addMessage(FIPA_Message message) {
		this.messageList.add(message);
	}

	// Prüft, ob noch eine Nachricht verfügbar ist.
	public boolean messagesAvailable(int receiver) {
		for (FIPA_Message message : this.messageList) {
			if (message.getReceiver() == receiver) {
				return true;
			}
		}
		return false;
	}

	// Fragt genau eine Nachricht ab.
	public FIPA_Message getMessage(int receiver) {
		for (FIPA_Message message : this.messageList) {
			if (message.getReceiver() == receiver) {
				this.messageList.remove(message);
				return message;
			}
		}
		return null;
	}

	public void send(int sender, int receiver, FIPA_Performative performative, String content) {
		this.addMessage(new FIPA_Message(sender, receiver, performative, content));
	}

}
