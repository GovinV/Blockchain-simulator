import java.net.* ;
import java.rmi.* ;

public class Serveur
{
    public static void main(String [] args)
    {
        if (args.length == 0 || args.length > 2)
        {
            System.out.println("Usage : java Serveur <rmiregistry port>\n Or : java Serveur <rmi port> <peer port>") ;
            System.exit(0) ;
        }
        try
        {
            BlockChainImpl blockchain = new BlockChainImpl() ;
            blockchain.setMyPort(Integer.parseInt(args[0]));
            Naming.rebind("rmi://localhost:" + args[0] + "/BlockChain" ,blockchain) ;
            System.out.println("Serveur pret") ;
            if(args.length == 2)
            {
                blockchain.addServer(Integer.parseInt(args[1]));
                System.out.println("Add Server at Port: "+args[1]+" as neighbor.");
            } 
            UpdateChain t = new UpdateChain(blockchain);
        }
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }

          
    }
}