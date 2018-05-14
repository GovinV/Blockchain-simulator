import java.rmi.Remote ; 
import java.rmi.RemoteException ; 
import java.util.ArrayList;
import java.security.*;

public interface BlockChain extends Remote
{
	public Boolean inscription(PublicKey pub, String participantId) 
		throws RemoteException;
	public String addInstruc(String senderId, String receiverId, String volume, byte[] sign) 
	  	throws InvalidKeyException, Exception, RemoteException;
	public String addInstruc(String senderId, long timeStamp, int port) 
		throws RemoteException;
	public String addInstruc(String senderId, String receiverId,
							 String volume, long timeStamp, int port) 
	  	throws RemoteException;
	public void addNewServer(int port) 
		throws RemoteException;
	public Boolean newBlockFromOthers(Block newBlock, int depth, int port) 
		throws RemoteException;
	public ArrayList<Block> getBlocksOfServer(int d, int f)
		throws RemoteException;
	public int getWork(String participantId) 
		throws RemoteException;
	public Boolean checkWork(int number, String participantId) 
		throws RemoteException;
	public ArrayList<String> getOthersParticipants(String participantId) 
		throws RemoteException;
	public String getParticipantState(String participantId) 
		throws RemoteException;
	public String dump() 
		throws RemoteException;
}

/*TODO:
TIMESTAMP ou LIST SERVEUR
pour les instructions*/
