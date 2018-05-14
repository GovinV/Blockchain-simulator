import java.util.*;
import java.security.*;

public class MyKey
{
	public KeyPair mine;
	public byte[] pub;
	public byte[] pvt;

	public MyKey(KeyPair mine, byte[] pub, byte[] pvt)
	{
		this.mine=mine;
		this.pvt=Arrays.copyOf(pvt, pvt.length);
		this.pub=Arrays.copyOf(pub, pub.length);
	}

	public static String keyToString(byte[] hashByte)
    {
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

	public String toString(int choix)
	{
		if(choix == 0)
			return keyToString(this.pub);
		else
			return keyToString(this.pvt);
	}
}