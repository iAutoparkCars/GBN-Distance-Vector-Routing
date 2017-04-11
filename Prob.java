import java.net.DatagramPacket;
import java.util.Scanner;

public class Prob
{
	public static String mode = "";

    public Prob(String args[])
    {
    	this.mode = args[3];
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
            String cmd;
            while (true)
            {
                cmd = reader.nextLine();
                System.out.println(cmd);
            }
        }     
    }
    
}