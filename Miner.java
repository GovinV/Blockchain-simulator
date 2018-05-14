import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.io.*;
import java.io.Serializable;

public class Miner extends Thread implements Serializable
{
	public String input;
	public int difficulty;
	public String hash;
	public boolean done;
	public long nonce;

	public Miner(int difficulty) {
		this.hash="1";
		this.done=false;
		this.nonce=0;
		this.input="";
		this.difficulty=difficulty;
	}

	public void setParams(String input)
	{
		this.input=input;
		this.difficulty=difficulty;
		this.done=false;
		this.nonce=0;
	}

	public String genHash(String input)
	{
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

	public void run() 
	{
		String possible = input + Long.toString(this.nonce);
		hash=genHash(possible);
		String target =new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0" 
		while(!hash.substring( 0, difficulty).equals(target)) 
		{
			nonce ++;
			possible = input + Long.toString(this.nonce);
			hash = genHash(possible);
		}
		System.out.println("Block Mined!!! : " + hash);
		this.done=true;
	}

}
