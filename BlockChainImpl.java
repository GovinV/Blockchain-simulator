import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;

public class BlockChainImpl 
  extends UnicastRemoteObject
    implements BlockChain
{

	public ArrayList<Block> chain;
	public ArrayList<Instruction> currentInstruc;
	//TODO timer for new block (create block after storing instruction)

	public BlockChainImpl() throws RemoteException
	{
		super();
		this.chain = new ArrayList<Block>();
		this.currentInstruc = new ArrayList<Instruction>();
	}


	public Boolean isBlockChainValid() throws RemoteException
	{
		Block currentBlock; 
		Block previousBlock;
		
		
		for(int i=1; i < this.chain.size(); i++) 
		{
			currentBlock = this.chain.get(i);
			previousBlock = this.chain.get(i-1);
			
			if(!currentBlock.hash.equals(currentBlock.genHash()) )
			{
				System.out.println("Current Hashes not equal");			
				return false;
			}
			
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) 
			{
				System.out.println("Previous Hashes not equal");
				return false;
			}
		}
		return true;
	}

	public String addInstruc(String senderId) throws RemoteException
	{
		currentInstruc.add(new Instruction(senderId));
		System.out.println("Received:"+this.currentInstruc.get(this.currentInstruc.size()-1).toString());
		return "Instruction added : inscription";
	}

	public String addInstruc(String senderId, String receiverId, String volume) 
	  throws RemoteException
	{
		currentInstruc.add(new Instruction(senderId,receiverId,volume));
		System.out.println("Received:"+this.currentInstruc.get(this.currentInstruc.size()-1).toString());
		return "Instruction added : transaction";
	}	

	private void addBlock() 
	{
		//todo
	}
}



/*
	public static void main(String[] args) {
		
		Instruction i1 = new Instruction("defr");
		Instruction i2 = new Instruction("defr","drfe","5");
		
		Instruction[] iTab = new Instruction[2];
		iTab[0]=i1;
		iTab[1]=i2;
		blockchain.add(new Block(iTab, "0"));
		
		blockchain.add(new Block(iTab,blockchain.get(0).hash));
		
		blockchain.add(new Block(iTab,blockchain.get(1).hash));

		Boolean test = BlockChain.isBlockChainValid();
		if(test)
			System.out.println("test cleared");
		else
			System.out.println("problem in hash");
		
	}
	*/