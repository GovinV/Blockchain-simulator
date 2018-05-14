import java.security.*;
import java.util.ArrayList;
import java.io.Serializable;

public class Participant implements Serializable
{
	public PublicKey pub;
	public String id;
	public float amount;
	public int merit;//every ten point of activity gets a merit points
	public int activity;//Activity since last created block and reward

	public Participant(PublicKey pub, String id)
	{
		this.pub=pub;
		this.id=id;
		this.amount=10;
		this.merit=1;
		this.activity=0;
	}

	public void creditTrans()
	{
		this.activity++;
	}

	public void creditPOW()
	{
		this.activity+=5;
	}

	public void calculatedMerit()
	{
		int add = this.activity/10;
		int remains = this.activity%10;
	}

    public String getId()
    {
    	return this.id;
    }

    public String toString()
    {
    	String ret = "{Participant Id: "+ this.id +"\namount" + this.amount +"\n merit:"+ this.merit+"}\n";
    	return ret;
    }

}