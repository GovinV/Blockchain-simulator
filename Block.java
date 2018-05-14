import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.io.*;
import java.io.Serializable;

public class Block implements Serializable
{
	public String hash;
	public String previousHash;
	public long nonce;
	public long timeStamp; //millisecond since 1/1/1970
	public ArrayList<Instruction> ops; 
	public Boolean done;
	public Miner mThread;
	public int difficulty;
	public Boolean myBlock;


	//Block Constructor.
	public Block(ArrayList<Instruction> opsDone,String previousHash, int difficulty) 
	{
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.ops=new ArrayList<Instruction>(opsDone);
		this.done=false;
		this.myBlock=true;
		this.nonce=0;
		this.difficulty=difficulty;
		this.hash=genHash();
		this.mine();
	}

	public Block(ArrayList<Instruction> opsDone,String previousHash, long nonce, long timeStamp) 
	{
		this.previousHash = previousHash;
		this.timeStamp = timeStamp;
		this.ops=new ArrayList<Instruction>(opsDone);
		this.done=true;
		this.myBlock=false;
		this.nonce=nonce;
		this.hash=genHash();
	}

	public void mine()
	{
		this.mThread=new Miner(difficulty);
		String input = this.previousHash + Long.toString(this.timeStamp);
		for(int i =0; i < this.ops.size(); i++)
			input+=this.ops.get(i).toStringForHash();
		this.mThread.setParams(input);
		this.mThread.start();
	}

	public void validedBlock()
	{
		this.nonce=this.mThread.nonce;
		this.hash=genHash();
	}
/*	public boolean checkNonce(int difficulty,long nonce)
	{
		String target =new String(new char[difficulty]).replace('\0', '0');
		this.nonce=nonce;
		this.hash=genHash();
		if(hash.substring( 0, difficulty).equals(target))
		{
			this.done=true;
			return true;
		}
		else 
			return false;		
	}*/

	public void setPreviousHash(String previousHash)
	{
		this.previousHash=previousHash;
		this.mine();
	}

	public ArrayList<Instruction> dropBlock()
	{
		return this.ops;
	}

	public String genHash()
	{
		String input = this.previousHash + Long.toString(this.timeStamp);
		for(int i =0; i < this.ops.size(); i++)
			input+=this.ops.get(i).toStringForHash();
		input+= Long.toString(this.nonce);

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");	        
			byte[] hashByte = digest.digest(input.getBytes());	        
			StringBuffer hashHex = new StringBuffer();
			for (int i = 0; i < hashByte.length; i++) 
			{
				String hex = Integer.toHexString(0xff & hashByte[i]);
				if(hex.length() == 1) 
					hashHex.append('0');
				hashHex.append(hex);
			}
			return hashHex.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);	
		}
	}



	public String toStringForHash()
	{
		String input = this.previousHash + Long.toString(this.timeStamp);
		for(int i =0; i < this.ops.size(); i++)
			input+=this.ops.get(i).toStringForHash();
		return input;
	}	

	public String toString()
	{
		String opsStr = "";
		for(int i=0; i< this.ops.size(); i++)
			opsStr+=ops.get(i).toString()+",\n";
		String block = "->\033[1;34m##############################BLOCK##############################\033[0m\ntimeStamp: "
						+ this.timeStamp + "\nhash: " +this.hash +"\npreviousHash: "+this.previousHash
						+"\nnonce: " +this.nonce + "\nInstructions:(\n" + opsStr
						+")\n\033[1;34m############################END_BLOCK############################\033[0m]";
		return block;

	}

}