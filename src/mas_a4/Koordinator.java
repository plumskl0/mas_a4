package mas_a4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import repast.simphony.engine.schedule.ScheduledMethod;

public class Koordinator {

	private ArrayList<FIPA_Message> messageList;
	private ArrayList<Ziel> zielList;
	private ArrayList<Bote> boteList;
	private ArrayList<TupelBoteBewertung> tupelBoteBewertungList;
	private ArrayList<Lieferkosten[]> lieferKostenList;
	private HashMap<Ziel, Lieferkosten> lieferListe;
	private Double[] allBoteBewertungenList = {0.0d,0.0d,0.0d,0.0d};

	private int tage;
	private int aktTag;
	private int sender;
	public String name = "Koordinator";

	private int aktProc = 0;
	private int cntFb = 0;

	public Koordinator(ArrayList<Ziel> ziele, int tage) {
		this.zielList = ziele;
		this.tage = tage;
		this.messageList = new ArrayList<FIPA_Message>();
		this.boteList = new ArrayList<Bote>();
		this.tupelBoteBewertungList = new ArrayList<TupelBoteBewertung>();
		sender = -1;
		aktTag = 0;
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
				lieferKosten();
				break;
			case 2:
				// Geringste Lieferkosten bestimmen mit Trust
				calculateCosts();
				break;
			case 3:
				// Lieferungen den Boten zuweisen
				lieferungAnBote();
				break;
			case 4:
				// Auswertung von Kunden Feedback
				auswertungFeedback();
				break;
			case 5:
				// Nachdem alle Feedbacks ausgwertet wurden, muss der Tag
				// erhöhrt werden
				resetDay();
				break;
			default:
				break;
			}
		}
	}

	private void auswertungFeedback() {
		System.out.println("auswertungFeedback()");
		if (messagesAvailable(sender)) {
			FIPA_Message m = getMessage(sender);

			System.out.println("Feedback erhalten");

			if (m.getContent() == Content.POSITIV || m.getContent() == Content.NEGATIV) {
				// TupelBoteBewertung an TupelBoteBewertungList anhängen
				System.out.println("Feedback ist " + m.getContent());
				tupelBoteBewertungList.add(new TupelBoteBewertung(m.getSender(), m.getContent()));
				cntFb++;
			}
		}

		if (cntFb >= 5) {
			aktualisiereBewertungen();
			aktProc++;
		}
	}

	private void lieferungAnBote() {
		System.out.println("lieferungAnBote()");
		for (Map.Entry<Ziel, Lieferkosten> entry : lieferListe.entrySet()) {
			Ziel z = entry.getKey();
			Lieferkosten lk = entry.getValue();

			Bote b = boteList.get(lk.idBote);
			b.addLieferung(z);
		}
		aktProc++;
	}

	private void calculateCosts() {
		System.out.println("calculateCosts()");
		if (lieferListe == null)
			lieferListe = new HashMap<Ziel, Lieferkosten>();

		for (Lieferkosten[] lkl : lieferKostenList) {
			for (Lieferkosten lk : lkl) {
				if (!lieferListe.containsKey(lk.ziel)) {
					lieferListe.put(lk.ziel, lk);
				} else {
					Lieferkosten aktMin = lieferListe.get(lk.ziel);

					if (aktMin.kosten * getLkFaktor(aktMin.idBote) > lk.kosten * getLkFaktor(lk.idBote)) {
						lieferListe.put(lk.ziel, lk);
					}

				}
			}
		}

		aktProc++;
	}

	private double getLkFaktor(int boteId) {
		return 1 - getTrustValueBote(boteId);
	}

	private double getTrustValueBote(int idBote) {
		double summePositiv = 0;
		// long summeAnzahl = 0;
		double summeNegativ = 0;

		for (TupelBoteBewertung tupel : tupelBoteBewertungList) {
			if (tupel.getBote() == idBote) {
				// summeAnzahl = summeAnzahl +1;
				if (tupel.getBewertung() == Content.POSITIV) {
					summePositiv = summePositiv + 1;
				} else if (tupel.getBewertung() == Content.NEGATIV) {
					summeNegativ = summeNegativ + 1;
				}
			}
		}

		return (summePositiv - summeNegativ) / (summePositiv + summeNegativ);
	}

	private void aktualisiereBewertungen() {

		for (int i = 0; i < boteList.size(); i++) {
			System.out.println(getTrustValueBote(i));
			allBoteBewertungenList[i] = getTrustValueBote(i);
		}
	}

	private void lieferKosten() {
		if (lieferKostenList == null)
			lieferKostenList = new ArrayList<Lieferkosten[]>();

		if (messagesAvailable(sender)) {
			if (lieferKostenList.size() < 4) {
				FIPA_Message m = getMessage(sender);

				if (Content.HOLE_LIEFERKOSTEN == m.getContent()) {
					Bote b = getBoteWithId(m.getSender());
					Lieferkosten[] lk = b.getLieferKosten();

					lieferKostenList.add(lk);
				}
			}
		}

		if (lieferKostenList.size() >= 4) {
			aktProc++;
		}
	}

	private void zieleAnBoten() {
		boteList.stream().forEach(b -> {
			send(sender, b.getSender(), FIPA_Performative.INFORM, Content.HOLE_LIEFERUNG);
		});
		System.out.println("Ziele an boten gesendet");
		aktProc++;
	}

	public void addBote(Bote b) {
		boteList.add(b);
		tupelBoteBewertungList.add(new TupelBoteBewertung(b.getSender(), Content.POSITIV));
		tupelBoteBewertungList.add(new TupelBoteBewertung(b.getSender(), Content.NEGATIV));
	}

	public int getSender() {
		return sender;
	}

	private void resetDay() {
		aktTag++;
		aktProc = 0;
		cntFb = 0;
		lieferKostenList.clear();
	}

	public ArrayList<Ziel> getLieferungen() {
		return zielList;
	}

	private Bote getBoteWithId(int senderId) {
		return boteList.stream().filter(b -> b.getSender() == senderId).findFirst().get();
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

	public double trustVal1() {
		return allBoteBewertungenList[0];
	}
	
	public double trustVal2() {
		return allBoteBewertungenList[1];
	}
	
	public double trustVal3() {
		return allBoteBewertungenList[2];
	}
	
	public double trustVal4() {
		return allBoteBewertungenList[3];
	}
	
	public int getAktTag() {
		return aktTag;
	}
}
