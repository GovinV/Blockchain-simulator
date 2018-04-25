import java.rmi.Remote ; 
import java.rmi.RemoteException ; 

public interface BlockChain extends Remote
{
	public Boolean isBlockChainValid() 
	 	throws RemoteException;
	public String addInstruc(String senderId) 
		throws RemoteException;
	public String addInstruc(String senderId, String receiverId, String volume) 
	  	throws RemoteException;

}
