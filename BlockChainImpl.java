import java.util.*;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.rmi.* ; 
import java.net.MalformedURLException ; 
import java.security.*;
import java.util.Collections;
import java.util.Date;

public class BlockChainImpl 
  extends UnicastRemoteObject
    implements BlockChain
{
	private int myPort;
	public ArrayList<Block> chain;
	public ArrayList<Instruction> currentInstruc;
	public ArrayList<Integer> otherServers;
	public ArrayList<Participant> participants;
	public ArrayList<String> othersParticipants;
	final public int maxParticipant=4;
	private int nbParticipant;
	private int depth;
	private int difficulty;
	private int lock;
	

	public BlockChainImpl() throws RemoteException
	{
		super();
		this.chain = new ArrayList<Block>();
		this.currentInstruc = new ArrayList<Instruction>();
		this.otherServers = new ArrayList<Integer>();
		this.participants = new ArrayList<Participant>();
		this.othersParticipants = new ArrayList<String>();
		this.nbParticipant=0;
		this.depth=-1;
		this.difficulty=4;
	}

	public  Boolean isChainValid(ArrayList<Block> chain) 
	{
		Block currentBlock; 
		Block previousBlock;
		
		
		for(int i=1; i < chain.size(); i++) 
		{
			currentBlock = chain.get(i);
			previousBlock = chain.get(i-1);
			
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

    public Boolean inscription(PublicKey pub, String participantId) throws RemoteException
    {
    	if(this.containsParticipant(participantId))
    		return true;

        if(this.nbParticipant < 4)
        {
            this.participants.add(new Participant(pub,participantId));
            System.out.println(this.participants.size());
            nbParticipant++;
            long timeStamp = new Date().getTime();
            this.addInstruc(participantId,timeStamp);
            return true;
        }
        else 
            return false;
    }

	public String addInstruc(String senderId, long timeStamp) throws RemoteException
	{
		Instruction ins=new Instruction(senderId,timeStamp);
		currentInstruc.add(ins);
		//System.out.println("Received:"+this.currentInstruc.get(this.currentInstruc.size()-1).toString());
		for(int i =0; i< this.otherServers.size(); i++)
		{
			BlockChain b = getServerChain(otherServers.get(i));
			if(b != null)
				b.addInstruc(senderId,timeStamp,myPort);
		}
		this.getParticipant(senderId).creditTrans();
		return "Instruction added : inscription";
	}

	public String addInstruc(String senderId, String receiverId, String volume, byte[] sign) 
	  throws InvalidKeyException, Exception, RemoteException
	{
		if(!this.containsParticipant(senderId))
    		return "You're are not my client";
    	if(this.getParticipant(senderId).amount-Integer.parseInt(volume) < 0)
    		return "You don't have the money";
		if(!this.verifySender(senderId,receiverId+volume,sign))
			return "False signature, this is not your real Public key";
		long timeStamp = new Date().getTime();
		Instruction ins=new Instruction(senderId,receiverId,volume,timeStamp);
		currentInstruc.add(ins);
		//System.out.println("Received:"+this.currentInstruc.get(this.currentInstruc.size()-1).toString());
		for(int i =0; i< this.otherServers.size(); i++)
		{
			BlockChain b = getServerChain(otherServers.get(i));
			if(b != null)
				b.addInstruc(senderId,receiverId,volume,timeStamp,myPort);
		}
		this.getParticipant(senderId).creditTrans();
		return "Instruction added : transaction";
	}	

	public String addInstruc(String senderId, long timeStamp, int port) throws RemoteException
	{
		if(this.containsParticipant(senderId))
    		return null;
    	this.othersParticipants.add(senderId);
		Instruction ins=new Instruction(senderId, timeStamp);
		if(this.chainHasInstruc(this.chain,ins)!=-1 || this.currHasInstruc(ins) !=-1)
			return "Already have it";
		currentInstruc.add(ins);
		//System.out.println("Received:"+this.currentInstruc.get(this.currentInstruc.size()-1).toString());
		for(int i =0; i< this.otherServers.size(); i++)
		{
			if(otherServers.get(i) == port)
				continue;
			BlockChain b = getServerChain(otherServers.get(i));
			if(b != null)
				b.addInstruc(senderId,timeStamp,myPort);
		}
		return "Instruction added : inscription";
	}

	public String addInstruc(String senderId, String receiverId, String volume, long timeStamp, int port) 
	  throws RemoteException
	{
		Instruction ins=new Instruction(senderId,receiverId,volume,timeStamp);
		if(this.chainHasInstruc(this.chain,ins)!=-1 || this.currHasInstruc(ins) !=-1)
			return "Already have it";
		currentInstruc.add(ins);
		//System.out.println("Received:"+this.currentInstruc.get(this.currentInstruc.size()-1).toString());
		for(int i =0; i< this.otherServers.size(); i++)
		{
			if(otherServers.get(i) == port)
				continue;
			BlockChain b = getServerChain(otherServers.get(i));
			if(b != null)
				b.addInstruc(senderId,receiverId,volume,timeStamp,myPort);
		}
		return "Instruction added : transaction";
	}	

	public void addNewServer(int port) throws RemoteException
	{
		otherServers.add(port);
		System.out.println("New Serveur, Port: "+port);
	}
	
	public void addServer(int port) throws RemoteException
	{
		otherServers.add(port);
		BlockChain b = getServerChain(port);
		if(b != null)
			b.addNewServer(myPort);
	}

	public void setMyPort(int port)
	{
		this.myPort=port;
	}

	public int getWork(String participantId) throws RemoteException
	{
		if(!this.containsParticipant(participantId))
			return -1;
		int min = this.chain.size()*100;
		return min;
	}

	public Boolean checkWork(int number, String participantId) throws RemoteException
	{
		Boolean found =false;
        if (number % 42 == 0) 
                found = true;
        if(found)
        {
        	this.getParticipant(participantId).creditPOW();
        }
        return found;		
	}

	public String getParticipantState(String participantId) throws RemoteException
	{
		return this.getParticipant(participantId).toString();
	}

	private void checkCreateBlock() throws RemoteException
	{	
		this.difficulty=4 + (this.chain.size()/5);
		if(this.depth==-1)
		{
			if(this.currentInstruc.size()>=2 && this.chain.size()==0)
			{
				this.addBlock();
			}
			if(this.chain.size()!=0)
			{
				if(this.chain.get(0).mThread.done)
				{
					this.chain.get(0).validedBlock();
					this.depth++;
					this.sendLastBlock();
				}
			}
		}
		else
		{
			if(this.depth==this.chain.size()-1 && this.currentInstruc.size()!=0)
				this.addBlock();
			if(this.depth<=this.chain.size()-2)
				if(this.chain.get(this.depth+1).mThread.done)
				{
					this.chain.get(this.depth+1).validedBlock();
					this.depth++;
					this.giveReward();
					this.sendLastBlock();
				}
		}
	}

	public void update() 
	{
		try
		{
			if(this.lock==1)
				return;
			this.checkCreateBlock();
		}
		catch (RemoteException re) { System.out.println(re) ;}
	}

	private void addBlock() 
	{
		if(this.chain.size() == 0)
		{
			Block current = new Block(this.currentInstruc,"0",this.difficulty);
			this.currentInstruc.clear();
			this.chain.add(current);
		}
		else
		{
			Block current = new Block(this.currentInstruc,this.chain.get(this.chain.size()-1).hash,this.difficulty);
			this.currentInstruc.clear();
			this.chain.add(current);
		}
	}

	public ArrayList<String> getOthersParticipants(String participantId) throws RemoteException
	{
		if(!this.containsParticipant(participantId))
			return null;
		ArrayList<String> others = new ArrayList<String>();
		for(int i=0; i<this.participants.size(); i++)
			others.add(this.participants.get(i).id);
		for(int i=0; i<this.othersParticipants.size(); i++)
			others.add(this.othersParticipants.get(i));		
		others.remove(participantId);
		return others;
	}

	private Boolean verifySender(String pId, String input, byte[] signature) throws InvalidKeyException, Exception
	{
		Participant p = this.getParticipant(pId);
		Signature sign = Signature.getInstance("SHA256withRSA");
		sign.initVerify(p.pub);
		String data=pId+input;
		sign.update(data.getBytes());
		if(sign.verify(signature))
			return true;
		else
			return false;
	}

	private void sendLastBlock() throws RemoteException
	{
		for(int i =0; i< this.otherServers.size(); i++)
		{
			BlockChain b = getServerChain(otherServers.get(i));
			Boolean thereIsBetter=true;
			if(b != null)
				thereIsBetter=b.newBlockFromOthers(this.chain.get(this.depth),this.depth,this.myPort);
			if(thereIsBetter=false)
				return;
		}
	}


	public Boolean newBlockFromOthers(Block newBlock, int depth, int port) throws RemoteException
	{	
		int thereIsBetter=0;
		System.out.println("Block from "+ port+ "\nTimeStamp: "+newBlock.timeStamp);
		if(this.depth > depth)
			return false;/*if is the size of is chain is lower or the same as our with is new block, we discard it*/
		if(this.depth==-1)
		{
			//System.out.println("0");
			if(newBlock.previousHash.equals("0"))
			{
				//System.out.println("1");
				if(this.chain.size()>this.depth+1)
				{
					//System.out.println("2");
					for(int i= this.depth+1; i<this.chain.size();i++)
					{
						this.passBlockInsToCurr(this.chain.get(i));
					}
					for(int i= this.depth+1; i<this.chain.size();i++)
					{
						this.chain.remove(i);
					}
				}
				for(int i=0; i<newBlock.ops.size();i++)
				{
					int found=currHasInstruc(newBlock.ops.get(i));
					if(found != -1)
						this.currentInstruc.remove(found);
				}		
				this.chain.add(newBlock);
				this.depth++;		
			}
			else
				thereIsBetter=1;
		}
		else
		{
			//System.out.println("3");
			int olH = oldHash(this.chain,newBlock.previousHash);
			if(olH != -1)
			{
				//System.out.println("4");
				if(this.chain.get(olH).timeStamp <= newBlock.timeStamp)
					return false;
				else
				{	
					//System.out.println("5");
					if(this.depth<depth)
						thereIsBetter=1;
					else
						return false;
				}
			}	
			else
			{
				//System.out.println("6");
				if(this.chain.get(this.depth).hash.equals(newBlock.previousHash))
				{
					//System.out.println("7");
					if(this.chain.size()>this.depth+1)
					{
						//System.out.println("8");
						for(int i= this.depth+1; i<this.chain.size();i++)
						{
							this.passBlockInsToCurr(this.chain.get(i));
						}
						for(int i= this.depth+1; i<this.chain.size();i++)
						{
							this.chain.remove(i);
						}
					}
					for(int i=0; i<newBlock.ops.size();i++)
					{
						int found=currHasInstruc(newBlock.ops.get(i));
						if(found != -1)
							this.currentInstruc.remove(found);
					}		
					this.chain.add(newBlock);
					this.depth++;		
				}
				else
					thereIsBetter=1;
			}		
		}
		if(thereIsBetter == 1)
		{//on peut nettement améliorer en prenant à partir du dernier block identique
			//System.out.println("9");
			BlockChain serverBc = this.getServerChain(port);
			ArrayList<Block> betterChain = serverBc.getBlocksOfServer(0,depth);
			if(!this.isChainValid(betterChain))
				return false;
			this.passInsToCurr();
			for(int i=0; i<betterChain.size();i++)
			{
				for(int j=0; j<betterChain.get(i).ops.size();j++)
				{
					int found=currHasInstruc(betterChain.get(i).ops.get(j));
					if(found != -1)
						this.currentInstruc.remove(found);
				}
			}
			this.chain.clear();
			this.chain.addAll(betterChain);
		}
		//System.out.println("10");
		Boolean pass=true;
		for(int i =0; i< this.otherServers.size(); i++)
		{
			if(otherServers.get(i) == port)
				continue;
			BlockChain b = getServerChain(otherServers.get(i));
			if(b != null)
				pass =b.newBlockFromOthers(newBlock,depth,this.myPort);
		}
		return pass;
	}

	public ArrayList<Block> getBlocksOfServer(int d, int f) throws RemoteException
	{
		if(f>this.chain.size()-1)
			f=this.chain.size()-1;
		if(d<0)
			d=0;
		if(f<d)
		{
			int a=d;
			d=f;
			f=a;
		}
		ArrayList<Block> toSent =new ArrayList<Block>();
		for(int i=d;i<f;i++)
			toSent.add(this.chain.get(i));
		return toSent;
	}

	private BlockChain getServerChain(int port) 
	{
		try
        {
            BlockChain b = (BlockChain) Naming.lookup("rmi://localhost:" + port + "/BlockChain") ;
            return b;
        }
        catch (NotBoundException re) { System.out.println(re) ; return null;}
        catch (RemoteException re) { System.out.println(re) ; return null;}
        catch (MalformedURLException e) { System.out.println(e) ; return null;}
	}

	public String dump() throws RemoteException
	{
		String input= "";
		for(int i=0; i<this.chain.size();i++)
			input+=this.chain.get(i).toString()+"\n";
		return input;
	}

	private Participant getParticipant(String id)
	{
		for(int i=0; i<this.participants.size(); i++) 
		{
			if(this.participants.get(i).id.equals(id))
				return this.participants.get(i);
	    }
	    return null;
	}

	private Boolean containsParticipant(String id)
	{
		for(int i=0; i<this.participants.size(); i++) 
		{
			if(this.participants.get(i).id.equals(id))
				return true;
	    }
	    for(int i= 0; i<this.othersParticipants.size();i++)
		{
			if(this.othersParticipants.get(i).equals(id))
				return true;
	    }	    	
	    return false;
	}
	
	private int currHasInstruc(Instruction instruc)
	{
		for(int i=0; i<this.currentInstruc.size(); i++) 
		{
			if(this.currentInstruc.get(i).toStringForHash().equals(instruc.toStringForHash()))
				return i;
	    }
	    return -1;
	}

	private int blockHasInstruc(Block bl, Instruction instruc)
	{
		for(int i=0; i<bl.ops.size(); i++) 
		{
			if(bl.ops.get(i).toStringForHash().equals(instruc.toStringForHash()))
				return i;
	    }
	    return -1;		
	}

	private int chainHasInstruc(ArrayList<Block> bc, Instruction instruc)
	{
		for(int i=0; i<bc.size(); i++) 
		{
			if(blockHasInstruc(bc.get(i),instruc) != -1)
				return i;
	    }
	    return -1;		
	}

	private void passInsToCurr()
	{
		for(int i=0; i<this.chain.size();i++)
		{
			for(int j=0; j<this.chain.get(i).ops.size();j++)
			{
				this.currentInstruc.add(this.chain.get(i).ops.get(j));
			}
		}
	}

	private void passBlockInsToCurr(Block bl)
	{
		for(int j=0; j<bl.ops.size();j++)
		{
			this.currentInstruc.add(bl.ops.get(j));

		}	
	}

	private int oldHash(ArrayList<Block> bc, String hash)
	{
		for(int i=0; i<bc.size(); i++) 
		{
			if(bc.get(i).previousHash.equals(hash))
				return i;
	    }
	    return -1;	
	}

	private void giveReward()
	{
		float merit=0;
		for(int i=0; i< this.participants.size();i++)
			merit+=this.participants.get(i).merit;
		for(int i=0; i< this.participants.size();i++)
			this.participants.get(i).amount+=10.0/merit;
	}
}
