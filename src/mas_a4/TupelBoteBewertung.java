package mas_a4;

public class TupelBoteBewertung {
	private int bote;
	private String bewertung;
	
	public int getBote() {
		return bote;
	}

	public void setBote(int bote) {
		this.bote = bote;
	}

	public String getBewertung() {
		return bewertung;
	}

	public void setBewertung(String bewertung) {
		this.bewertung = bewertung;
	}

	public TupelBoteBewertung(int bote, String bewertung){
		this.bote = bote;
		this.bewertung = bewertung;
	}
}
