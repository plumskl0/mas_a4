package mas_a4;

public class FIPA_Message {
	
	
	private int sender;
	private int receiver;
	private FIPA_Performative performative;
	private String content;

	
	private Lieferkosten lk;
	
	//Eine Nachricht besteht aus den folgenden 4 Parametern: 
	//Sender, Empf�nger, einem FIPA-Performative und einem Nachrichteninhalt.
	
	public FIPA_Message( int sender, int receiver, FIPA_Performative performative, String content ){
		this.setPerformative( performative );
		this.setSender( sender );
		this.setReceiver( receiver );
		this.setContent( content );
	}
	
	private void setPerformative(FIPA_Performative performative){
		this.performative = performative;
	}
	
	public FIPA_Performative getPerformative(){
		return this.performative;
	}
	
	public int getSender(){
		return this.sender;
	};
	
	private void setSender(int sender){
		this.sender = sender;
	};
	
	
	public int getReceiver(){
		return this.receiver;
	};
	
	private void setReceiver(int receiver){
		this.receiver = receiver;
	};
	
	public String getContent(){
		return this.content;
	};
	
	private void setContent(String content){
		this.content = content;
	};
	
	public void setLieferkosten(Lieferkosten lk) {
		this.lk = lk;
	}
	
	public Lieferkosten getLieferkosten() {
		return lk;
	}
}