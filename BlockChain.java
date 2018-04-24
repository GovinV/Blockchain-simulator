import java.security.MessageDigest;
import java.util.Arrays;
import java.io.*;


public class BlockChain 
{
	private BlockChain prev_block;
	private byte[] hash;
	private int[] ops;


	public BlockChain(BlockChain prev, int[] opsDone)
	{
		this.prev_block=prev;
		this.ops=new int[opsDone.length];
		this.ops=Arrays.copyOf(opsDone,opsDone.length);
		this.hash=getHash(prev);
	}

	public void showHash()
	{
		StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.hash.length; i++) {
         sb.append(Integer.toString((this.hash[i] & 0xff) + 0x100, 16).substring(1));
        }

        System.out.println("En format hexa : " + sb.toString());
	}

	private byte[] getHash(BlockChain prev)
	{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(convertToBytes(prev));

        return md.digest();
	}
	private byte[] convertToBytes(Object object) throws IOException {
	    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
	         ObjectOutput out = new ObjectOutputStream(bos)) {
	        out.writeObject(object);
	        return bos.toByteArray();
	    } 
	}

}