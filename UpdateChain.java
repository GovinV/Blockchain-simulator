import java.util.Timer;
import java.util.TimerTask;

public class UpdateChain 
{
    Timer t;

    public UpdateChain(BlockChainImpl bc) 
    {
        t = new Timer();
        t.schedule(new ActionOnChain(bc), 0, 1*1000);
    }

    class ActionOnChain extends TimerTask 
    {

        BlockChainImpl chain;
        public ActionOnChain(BlockChainImpl bc)
        {
            this.chain=bc;
        }

        public void run() 
        {
            this.chain.update();
        }  
    }
} 