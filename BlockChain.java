import java.util.ArrayList;


public class BlockChain 
{

	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	//TODO timer for new block (create block after storing instruction)
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

	public static Boolean isBlockChainValid() {
		Block currentBlock; 
		Block previousBlock;
		
		
		for(int i=1; i < blockchain.size(); i++) 
		{
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			
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


}