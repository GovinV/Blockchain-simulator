import java.rmi.* ; 
import java.net.MalformedURLException ; 
import java.security.*;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;


public class ClientDump
{
    public static void main(String [] args) throws InvalidKeyException, Exception
    {
        if (args.length != 2)
        {
            System.out.println("Usage : java Client <machine du Serveur> <port du rmiregistry>") ;
            System.exit(0) ;
        }
        try
        {
            
            BlockChain b = (BlockChain) Naming.lookup("rmi://" + args[0] + ":" + args[1] + "/BlockChain") ;
            String bloch= b.dump();
            System.out.println("BlockChain:\n"+ bloch);
            byte data[] = b.dump().getBytes();
            Path file = Paths.get("BlockChain_State_"+args[1]+"_:"+ Long.toString(new Date().getTime()) + ".dump");
            Files.write(file, data);
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
    }
}
