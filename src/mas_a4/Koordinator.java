package mas_a4;

import java.util.ArrayList;

import mas_a4.Ziel;
import repast.simphony.engine.schedule.ScheduledMethod;

public class Koordinator {

	private ArrayList<FIPA_Message> messageList;
	private ArrayList<Ziel> ziele;
	
	private int tage;

	public Koordinator(ArrayList<Ziel> ziele, int tage) {
		this.ziele = ziele;
		this.tage = tage;
		this.messageList = new ArrayList<FIPA_Message>();
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void run() {
	}

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
