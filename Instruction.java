import java.io.Serializable;

public class Instruction implements Serializable
{
	public String sender;
	public String receiver;
	public String volume;
	public long timeStamp;
	public Boolean isTransaction;// transaction or inscription



	public Instruction(String sender, String receiver, String volume, long timeStamp)
	{
		this.sender=sender;
		this.receiver=receiver;
		this.volume=volume;
		this.timeStamp=timeStamp;
		this.isTransaction=true;
	}


	public Instruction(String sender, long timeStamp)
	{
		this.sender=sender;
		this.timeStamp=timeStamp;
		this.isTransaction=false;
	}	

	public String toStringForHash()
	{
		String input = this.sender + this.receiver + this.volume + Long.toString(this.timeStamp);
		return input;
	}

	public String toString()
	{
		if(isTransaction == true)
		{
			return "{\033[47m\033[1;31m####Transaction####\033[0m\n\tTimeStamp: "+ this.timeStamp + "\n\tSender: "+ this.sender +"\n\tReceiver: " + this.receiver +"\n\tVolume: "+this.volume+"\n}";
		}
		else
		{
			return "{\033[1;32m##Inscription##\033[0m\n\tTimeStamp: "+ this.timeStamp + "\n\tSender: "+ this.sender +"\n}";
		}
	}
}