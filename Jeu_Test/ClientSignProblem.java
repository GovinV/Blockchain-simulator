import java.rmi.* ; 
import java.net.MalformedURLException ; 
import java.security.*;
import java.util.ArrayList;

public class ClientSignProblem
{

    public static MyKey genKey()
    {
        try
        {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(512);
            KeyPair kp = kpg.generateKeyPair();
            Key pub = kp.getPublic();
            Key pvt = kp.getPrivate();
            MyKey client = new MyKey(kp,pub.getEncoded(), pvt.getEncoded());
            return client;       
        }
        catch(NoSuchAlgorithmException re)
        {
            System.out.println(re) ;System.out.println(re);
            return null;
        }
        
    }

    public static byte[] getSignature(MyKey client, String[] args) throws InvalidKeyException, Exception
    {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(512);
        KeyPair kp = kpg.generateKeyPair();
        System.out.println("Original Private Key:" +client.mine.getPrivate() +"\nTry to Sign with this key: "+kp.getPrivate()+"\n");
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(kp.getPrivate());
        String input = client.toString(0);
        for(int i =0; i<args.length;i++)
            input+=args[i];
        sign.update(input.getBytes());
        return sign.sign();        
    }

    public static void main(String [] args) throws InvalidKeyException, Exception
    {

        if (args.length != 2)
        {
            System.out.println("Usage : java Client <machine du Serveur> <port du rmiregistry>") ;
            System.exit(0) ;
        }
        try
        {
            MyKey client = genKey();
            BlockChain b = (BlockChain) Naming.lookup("rmi://" + args[0] + ":" + args[1] + "/BlockChain") ;
            if(b.inscription(client.mine.getPublic(),client.toString(0)))//give server our public key
            {
                System.out.println("Inscription done");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println(e);
                }
                ArrayList<String> others = b.getOthersParticipants(client.toString(0));
                byte[] sign = getSignature(client,new String[]{others.get(0),"2"});
                System.out.println("non");
                System.out.println("Client receive : " + b.addInstruc(client.toString(0),others.get(0),"2",sign)) ; //add timestamps
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    System.out.println(e);
                }
                System.out.println("BlockChain:\n"+ b.dump());
            }
            else
                System.out.println("Inscription failed try another server.") ;
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
    }
}
