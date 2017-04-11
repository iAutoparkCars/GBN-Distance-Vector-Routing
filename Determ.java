import java.net.DatagramPacket;
import java.util.Scanner;

public class Determ
{
    private String selfPort = "";
    private String peerPort = "";
    private Integer winSize = 0;
    private String mode = "";
    private Integer nVal = 0;
       	
	
	public Determ(String args[])
    {
		this.selfPort = args[0];
		this.peerPort = args[1];
		this.winSize = Integer.getInteger(args[2]);
		this.mode = args[3];
		this.nVal = Integer.getInteger(args[4]);
        startInputThread();
    }
    
    
    private void startInputThread()
    {
        ReadInput input = new ReadInput();
        Thread thread1 = new Thread(input);
        thread1.start();
    }
    
    
    class ReadInput implements Runnable
    {
        public void run()
        {
            Scanner reader = new Scanner(System.in);
            String[] cmdArray;
            while (true)
            {
            	System.out.print("node> ");
                cmdArray = reader.nextLine().trim().replaceAll(" +"," ").split(" ");
                if (cmdArray[0].trim().toLowerCase().equals("send"))
                {
                	sendPacket(cmdArray);
                }
            }
        }     
    } //end ReadInput subclass
    
    
    public void sendPacket(String[] cmd)
    {
    	
    }
    
    
}