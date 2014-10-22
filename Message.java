import java.util.Date;


public class Message {
	private String text;
	private int message_ID;
	private Date in_date;
	
	public Message(String text, int message_ID, Date in_date){
		this.text=text;
		this.message_ID=message_ID;
		this.in_date=in_date;
	}
	
	public String getText(){
		return text;
	}
	
	public int getMessage_ID(){
		return message_ID;
	}	
	
	public Date getIn_date(){
		return in_date;
	}


	
}
