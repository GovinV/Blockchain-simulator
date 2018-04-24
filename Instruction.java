public class Instruction
{
	public String sender;
	public String receiver;
	public String volume;
	public boolean isTransaction;// transaction or inscription
	public boolean inscription;


	public Instruction(String sender, String receiver, String volume)
	{
		this.sender=sender;
		this.receiver=receiver;
		this.volume=volume;
		this.isTransaction=true;
	}


	public Instruction(String sender)
	{
		this.sender=sender;
		this.isTransaction=false;
	}

	public String toString()
	{
		if(isTransaction == true)
		{
			return "Transaction:::Sender:"+ this.sender +"::Receiver:" + this.receiver +"::Volume:"+this.volume+"\n";
		}
		else
		{
			return "Inscription:::Sender:"+ this.sender +"\n";
		}
	}
}