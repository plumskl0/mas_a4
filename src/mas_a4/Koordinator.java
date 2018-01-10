package mas_a4;

import java.util.ArrayList;

import repast.simphony.engine.schedule.ScheduledMethod;

public class Koordinator {

	private ArrayList<FIPA_Message> messageList;
	private ArrayList<Ziel> zielList;
	private ArrayList<Bote> boteList;
	
	private int tage;

	private int sender;
	
	public Koordinator(ArrayList<Ziel> ziele, int tage) {
		this.zielList = ziele;
		this.tage = tage;
		this.messageList = new ArrayList<FIPA_Message>();
		this.boteList = new ArrayList<Bote>();
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void run() {
		for (int aktTag = 0; aktTag < tage; aktTag++) {
			// Ziele an Boten mitteilen
			
			
			// Lieferkosten sammeln

			// Geringste Lieferkosten bestimmen mit Trust

			// Lieferungen den Boten zuweisen

			// Auswertung von Kunden Feedback

			//  
		}
	}
	
	public void addBote(Bote b) {
		boteList.add(b);
	}
	
	
	public int getSender() {
		return sender;
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
