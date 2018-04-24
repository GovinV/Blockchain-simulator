import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.io.*;

public class Block 
{
	public String hash;
	public String previousHash;
	private long timeStamp; //millisecond since 1/1/1970
	private Instruction[] ops; 

	//Block Constructor.
	public Block(Instruction[] opsDone,String previousHash ) 
	{
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.ops=new Instruction[opsDone.length];
		this.ops=Arrays.copyOf(opsDone,opsDone.length);
		this.hash = this.genHash();
	}

	public String genHash()
	{
		String input = this.previousHash + Long.toString(this.timeStamp);
		for(int i =0; i < this.ops.length; i++)
			input+=this.ops[i].toString();

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


}