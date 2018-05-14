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
	final public int maxParticipant=4;
	private int nbParticipant;
	private int depth;
	private int difficulty;
	

	public BlockChainImpl() throws RemoteException
	{
		super();
		this.chain = new ArrayList<Block>();
		this.currentInstruc = new ArrayList<Instruction>();
		this.otherServers = new ArrayList<Integer>();
		this.participants = new ArrayList<Participant>();
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
		System.out.println("Received:"+this.currentInstruc.get(this.currentInstruc.size()-1).toString());
		for(int i =0; i< this.otherServers.size(); i++)
		{
			BlockChain b = getServerChain(otherServers.get(i));
			if(b != null)
				b.addInstruc(senderId,timeStamp,myPort);
		}
		return "Instruction added : inscription";
	}

	public String addInstruc(String senderId, String receiverId, String volume, byte[] sign) 
	  throws InvalidKeyException, Exception, RemoteException
	{
		if(!this.containsParticipant(senderId))
    		return "You're are not my client";
		if(!this.verifySender(senderId,receiverId+volume,sign))
			return "False signature, this is not your real Public key";
		long timeStamp = new Date().getTime();
		Instruction ins=new Instruction(senderId,receiverId,volume,timeStamp);
		currentInstruc.add(ins);
		System.out.println("Received:"+this.currentInstruc.get(this.currentInstruc.size()-1).toString());
		for(int i =0; i< this.otherServers.size(); i++)
		{
			BlockChain b = getServerChain(otherServers.get(i));
			if(b != null)
				b.addInstruc(senderId,receiverId,volume,timeStamp,myPort);
		}
		return "Instruction added : transaction";
	}	

	public String addInstruc(String senderId, long timeStamp, int port) throws RemoteException
	{
		Instruction ins=new Instruction(senderId, timeStamp);
		if(!isInstrucValid(ins))
			return "Instruction not Valid";
		if(this.chainHasInstruc(this.chain,ins)!=-1 || this.currHasInstruc(ins) !=-1)
			return "Already have it";
		currentInstruc.add(ins);
		System.out.println("Received:"+this.currentInstruc.get(this.currentInstruc.size()-1).toString());
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
		System.out.println("Received:"+this.currentInstruc.get(this.currentInstruc.size()-1).toString());
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

	public String getWork(String participantId) throws RemoteException
	{
		if(!this.containsParticipant(participantId))
			return "None\n You are not my client\n";
		if(this.depth<this.chain.size()-1)
		{
			return this.chain.get(depth+1).toStringForHash();
		}
		else
		{
			return "None";
		}
	}

	private void checkCreateBlock() throws RemoteException
	{	

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
					this.sendLastBlock();
				}
		}
	}

	public void update() 
	{
		try
		{
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

	private Boolean isInstrucValid(Instruction i)
	{
		return true;
	}

	private void sendLastBlock() throws RemoteException
	{
		for(int i =0; i< this.otherServers.size(); i++)
		{
			BlockChain b = getServerChain(otherServers.get(i));
			if(b != null)
				b.newBlockFromOthers(this.chain.get(this.depth),this.depth,this.myPort);
		}
	}

	public void newBlockFromOthers(Block newBlock, int depth, int port) throws RemoteException
	{	
		System.out.println("Block from "+ port+ "\nTimeStamp: "+newBlock.timeStamp);
		if(this.depth >= depth)
			return;/*if is the size of is chain is lower or the same as our with is new block, we discard it*/
		if(depth > this.depth)
		{
			if(depth == this.depth+1)
			{
				if(this.depth == -1 || this.chain.get(this.depth).hash.equals(newBlock.previousHash))
				{
					if(this.chain.size() > this.depth+1)
					{
						this.chain.add(this.depth+1,newBlock);
						for(int i=0; i < newBlock.ops.size(); i++)
						{
							int found=currHasInstruc(newBlock.ops.get(i));
							if(found != -1)
								this.currentInstruc.remove(found);
							found=blockHasInstruc(this.chain.get(this.depth+2),newBlock.ops.get(i));
							if(found != -1)
								this.chain.get(this.depth+2).ops.remove(found);
						}
						if(this.chain.get(this.depth+2).ops.size()==0)
							this.chain.remove(this.depth+2);
						else
							this.chain.get(this.depth+2).setPreviousHash(newBlock.hash);
					}
					else
						this.chain.add(newBlock);
					this.depth++;
				}
				else
				{
					depth++;//if there is two differents block we try to see if the chain is valid and if we can take his one or if it's a fake
				}
			}
			if(depth>this.depth+1)
			{
				BlockChain serverBc = this.getServerChain(port);
				ArrayList<Block> betterChain = serverBc.getBlocksOfServer(0,depth);
				if(!this.isChainValid(betterChain))
					return;
				this.passInsToCurr();
				ArrayList<Integer> toRemoveFromCur= new ArrayList<Integer>();
				for(int i =0; i<this.currentInstruc.size();i++)
				{
					int found=chainHasInstruc(betterChain,this.currentInstruc.get(i));
					if(found != -1)
						toRemoveFromCur.add(i);
				}
				Collections.sort(toRemoveFromCur, Collections.reverseOrder());
				for(int i =0; i<toRemoveFromCur.size(); i++)
				{
					this.currentInstruc.remove(toRemoveFromCur.get(i));
				}
				this.chain.clear();
				this.chain.addAll(betterChain);
			}
		}
		for(int i =0; i< this.otherServers.size(); i++)
		{
			if(otherServers.get(i) == port)
				continue;
			BlockChain b = getServerChain(otherServers.get(i));
			if(b != null)
				b.newBlockFromOthers(newBlock,depth,this.myPort);
		}
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

}
