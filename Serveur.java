import java.net.* ;
import java.rmi.* ;

public class Serveur
{
    public static void main(String [] args)
    {
        if (args.length == 0 )
        {
            System.out.println("Usage : java Serveur <rmiregistry port>\n Or : java Serveur <rmi port> <peer port> <peer port>....") ;
            System.exit(0) ;
        }
        try
        {
            BlockChainImpl blockchain = new BlockChainImpl() ;
            blockchain.setMyPort(Integer.parseInt(args[0]));
            Naming.rebind("rmi://localhost:" + args[0] + "/BlockChain" ,blockchain) ;
            System.out.println("Serveur pret") ;
            if(args.length >= 2)
            {
                for(int i=1; i<args.length; i++)
                {
                    blockchain.addServer(Integer.parseInt(args[i]));
                    System.out.println("Add Server at Port: "+args[i]+" as neighbor.");
                }
            } 
            UpdateChain t = new UpdateChain(blockchain);
        }
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }

          
    }
}