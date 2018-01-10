package mas_a4;

import java.util.ArrayList;

import mas_a4.Ziel;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;

public class Koordinator {

	private ArrayList<FIPA_Message> messageList;
	private ArrayList<Ziel> ziele;
	
	private int tage;
	
	Parameters params = RunEnvironment.getInstance().getParameters();

	

	public Koordinator(ArrayList<Ziel> ziele) {
		this.ziele = ziele;
		this.tage = params.getInteger("tage");
//		System.out.println(this.tage);
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
