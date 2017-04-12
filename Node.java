import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import sun.misc.Queue;


public class Node 
{
	    private Integer selfPort = 0;
	    private Integer peerPort = 0;
	    private Integer winSize = 0;
	    private String mode = "";
	    private Integer nVal = 0;
	       	
		
		public Node(String args[])
	    {
			this.selfPort = Integer.getInteger(args[0]);
			this.peerPort = Integer.getInteger(args[1]);
			this.winSize = Integer.getInteger(args[2]);
			this.mode = args[3];
			this.nVal = Integer.getInteger(args[4]);
	        startInputThread();
	        while(true)
	        {
	        	listenForPacket();
	        	//listen for packets
	        }
	    }
		
		public void listenForPacket()
		{
			
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
	                
	                //if command line indicates to send, then cmdArray[1] will be the message
	                if (cmdArray[0].trim().toLowerCase().equals("send"))
	                {
	                	//function to handle seq and convert cmdArray[1] into msg
	                	
	                	queueMessage(cmdArray[1]);
	                	//Packet p1 = new Packet()
	                	//sendPacket(cmdArray);
	                }
	                BigDecimal currTime = new BigDecimal(System.currentTimeMillis()).scaleByPowerOfTen(-4);
	                System.out.println(currTime);	
	            }
	        }     
	    } //end ReadInput subclass
	    
	    /*
	     * Takes the message, and parses into characters
	    */
	    
	    Queue<Character> buffer = new Queue<Character>();
	    public void queueMessage(String msg) 
	    {
	    	char[] arr = msg.toCharArray();
	    	for (int i = 0; i < arr.length; i++)
	    	{
	    		buffer.enqueue(arr[i]);
	    	}
	    	
	    	while(!buffer.isEmpty())
	    	{
	    		try {System.out.print(buffer.dequeue() + " ");} 
	    		catch (InterruptedException e) {e.printStackTrace();}
	    	}
	    }
	    
	    
	    /*
	     * seq>=0 is a sequence number with data. seq<0 indicates ACK (no data)
	    */
	    public void sendPacket(Integer seq, Character data, InetAddress recIP, Integer recPort)
	    {
	    	seq = 0;
	    	data = null;
	    	
	    	
	    	/*DatagramSocket Send_Socket = new DatagramSocket();
			byte[] b0 = (seq.toString() + " " + data).getBytes();
			InetAddress ia = InetAddress.getLocalHost();
			DatagramPacket Client_Register = new DatagramPacket(b2,b2.length,ia,SERVER_PORT);
			Send_Socket.send(Client_Register);
			Send_Socket.close();*/
	    	    	
	    	
	    }
	    
}
