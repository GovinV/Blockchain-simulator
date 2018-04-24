import java.util.ArrayList;


public class BlockChain 
{

	public static ArrayList<Block> blockchain = new ArrayList<Block>();

	public static void main(String[] args) {
		
		
		
		blockchain.add(new Block({"1","2"}, "0"));
		System.out.println("Hash for block 1 : " + firstBlock.hash);
		
		blockchain.add(new Block({"3","4"},firstBlock.hash));
		System.out.println("Hash for block 2 : " + secondBlock.hash);
		
		blockchain.add(new Block({"5","6"},secondBlock.hash));
		System.out.println("Hash for block 3 : " + thirdBlock.hash);

		
	}

	public static Boolean isBlockChainValid() {
		Block currentBlock; 
		Block previousBlock;
		
		
		for(int i=1; i < this.blockchain.size(); i++) {
			currentBlock = this.blockchain.get(i);
			previousBlock = this.blockchain.get(i-1);
			
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("Current Hashes not equal");			
				return false;
			}
			
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
		}
		return true;
	}


}