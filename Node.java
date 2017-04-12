import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

//the trick would be to have EACH packet create its own thread to send AND wait for ack.
// ALl these threads would access the global queue. 

public class Node 
{
	    private Integer selfPort = 0;
	    private Integer peerPort = 0;
	    private Integer winSize = 0;
	    private String mode = "";
	    private Integer nVal = 0;
	       	
		
		public Node(String args[]) throws IOException
	    {
			this.selfPort = Integer.valueOf(args[0]);
			this.peerPort = Integer.valueOf(args[1]);
			this.winSize = Integer.valueOf(args[2]);
			this.mode = args[3];
			this.nVal = Integer.valueOf((args[4]));
	        startInputThread();
	        while(true)
	        {
	        	listenForPacket(selfPort);
	        	//sequenceIsCorrect();
	        	//sendAck();
	        }
	    }
		
		//may want to return of array of String in future
		public void listenForPacket(Integer recPort) throws IOException
		{
			DatagramSocket Listen_Socket = new DatagramSocket(recPort);
			byte[] b1 = new byte[1024];
			DatagramPacket pc = new DatagramPacket(b1,b1.length);
			Listen_Socket.receive(pc);
			pc.getSocketAddress();
			String getMsg = new String(pc.getData());
			Listen_Socket.close();
			
			System.out.println(pc.getSocketAddress());
			System.out.println(getMsg);
		}

		public Boolean sequenceIsCorrect()
		{
			return false;
		}
	    
		public void sendAck()
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
	                
	                /*
	                 * This thread will create threads for each packet and wait for ALL packet threads to finish.
	                 * Probably have to receive ACK on port + X
	                */
	                if (cmdArray[0].trim().toLowerCase().equals("send"))
	                {
	                	//loads message of chars into a queue
	                	queueMessage(cmdArray[1]);
	                	
	                	InetAddress ia;
						try
						{
							ia = InetAddress.getLocalHost();
							sendPacket(1, buffer.peek(), ia, peerPort);
						} 
						//listen for ack somewhere
						catch (UnknownHostException e) {e.printStackTrace();} 
						catch (IOException e) {e.printStackTrace();}
	                	
	                	//Packet p1 = new Packet()
	                	//sendPacket(cmdArray);
	                }
	                //BigDecimal currTime = new BigDecimal(System.currentTimeMillis()).scaleByPowerOfTen(-4);
	                //System.out.println(currTime);	
	            }
	        }     
	    } //end ReadInput subclass
	    
	    
	    
	    /*
	     * puts all data into queue of characters
	    */
	    Queue<Character> buffer = new LinkedList<Character>();
	    
	    public void queueMessage(String msg) 
	    {
	    	char[] arr = msg.toCharArray();
	    	for (int i = 0; i < arr.length; i++)
	    	{
	    		buffer.add(arr[i]);
	    	}
	    	
	    	/*while(!buffer.isEmpty())
	    	{
	    		System.out.print(buffer.remove() + " ");
	    	}*/
	    }
	    
	    
	    /*
	     * 
	    */
	    public void sendPacket(Integer seq, Character data, InetAddress recIP, Integer recPort) throws IOException
	    {
	    	//seq = 0;
	    	//data = null;
	    	byte[] b0 = (seq.toString() + " " + data).getBytes();
	    	
	    	DatagramSocket Send_Socket = new DatagramSocket();
			DatagramPacket Client_Register = new DatagramPacket(b0,b0.length,recIP,recPort);
			Send_Socket.send(Client_Register);
			Send_Socket.close();
	    	    	
	    	
	    }
	    
}
