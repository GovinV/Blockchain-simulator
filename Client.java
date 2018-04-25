import java.rmi.* ; 
import java.net.MalformedURLException ; 

public class Client
{
    public static void main(String [] args)
    {
        if (args.length != 2)
        {
            System.out.println("Usage : java Client <machine du Serveur> <port du rmiregistry>") ;
            System.exit(0) ;
        }
        try
        {
            BlockChain b = (BlockChain) Naming.lookup("rmi://" + args[0] + ":" + args[1] + "/BlockChain") ;
            System.out.println("Client receive : " + b.addInstruc("deaf48de")) ; 
            System.out.println("Client receive : " + b.addInstruc("deaf48de","dead48fe","2")) ; 
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
    }
}