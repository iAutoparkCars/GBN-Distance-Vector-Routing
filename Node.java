import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

//the trick would be to have EACH packet create its own thread to send AND wait for ack.
// ALl these threads would access the global queue. 

public class Node 
{
	    private Integer selfPort = 0;
	    private Integer peerPort = 0;
	    private Integer winSize = 0;
	    
	    //n or p depending on mode
	    private String mode = "";
	    private Integer nVal = 0;
	    private Double pVal = 0.0;
	    
	    //global instances used by receiver
	    private Integer expected = 0;
	    private Integer sequence = 0;
	    private Integer msgLength = 0;
	    
	    DatagramSocket s1 = null;
	    
	    
	    class CloseSocket implements Runnable
	    {
	        public void run()
	        {
	        	/*while(true)
	        	{
	        		if (sequence == (msgLength-1))
	        		{
	        			s1.disconnect();
	        			s1.close();
	        			
	        			
	        			try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	        		}
	        	}*/
	        }
	    }
	    
		public Node(String args[]) throws IOException
	    {
			String type = args[3];
			if (type.equals("-d"))
			{
				this.nVal = Integer.valueOf((args[4]));
			}
			else if (type.equals("-p"))
			{
				this.pVal = Double.valueOf((args[4]));
	        }    
			
			this.selfPort = Integer.valueOf(args[0]);
			this.peerPort = Integer.valueOf(args[1]);
			this.winSize = Integer.valueOf(args[2]);
			this.mode = args[3];
			
			
			startInputThread();
	        
	        /*CloseSocket close = new CloseSocket();
	        Thread t1 = new Thread(close);
	        t1.start();*/
	        
	        
	        while(true)
	        {
	        	
	        	listenPacketSendACK(s1,selfPort);
	        	
	        	
	        	
	        	
	        }
	    
	    }
		
		
		/* @param Current Socket: listen and reply with ACK
		 * 		  Port: get port of sender
		 *  
		*/
		public void listenPacketSendACK(DatagramSocket listen_socket, Integer recPort) throws IOException
		{
			//retrieve message
			listen_socket = new DatagramSocket(recPort);
			byte[] b1 = new byte[1024];
			DatagramPacket pc = new DatagramPacket(b1,b1.length);
			
			//if accepted last data
			
			if ((sequence == (msgLength-1)))
			{
				
				listen_socket.close();
				sequence = 0;
				msgLength = 0;
				expected = 0;
				System.out.print("node> ");
				//startInputThread();
				return;
			}
				
			listen_socket.receive(pc);
			

			
			
			
			//Must trim after getData() function because it returns arbitrary whitespace
			String getMsg = new String(pc.getData()).trim();
			
			//returns seqNumber | msgLength | data | sender's port
			String port = String.valueOf(pc.getPort()).trim();
			
			
			//System.out.println((getMsg + " "+port).replaceAll("\\s+"," "));
			String[] info = (getMsg + " "+port).replaceAll("\\s+"," ").split(" ");
			
			//unpack listened info for convenience [seqNumber | buffLength | data | sender's port]
        	Integer seq = Integer.valueOf(info[0]);
        	sequence = seq;
        	Integer buffLength = Integer.valueOf(info[1]);
        	msgLength = buffLength;
        	Character data = info[2].charAt(0);
        	Integer replyToPort = Integer.valueOf(info[3]);
			
        	
        	//Receiver: print received data time
        	BigDecimal getDataTime = new BigDecimal(System.currentTimeMillis()).scaleByPowerOfTen(-3);
        	System.out.println("["+getDataTime+"] " + "packet" + seq + " " + data + " received");
        	
        	
        	if (seq == buffLength)
        	{
        		System.out.println("do I have ever get here");
        		listen_socket.close();
        		return;
        	}
        	else if (seq == expected && seq < buffLength)
        	{
        		expected++;
        		//System.out.println(info[0] + " " + info[1] + " " + info[2] + " " + info[3]);
        		
        		/*if (seq==2)
        		{
        			seq = -1;
        		}
        		if (seq==3)
        		{
        			seq = -213;
        		}*/
        		sendACK(seq, buffLength, null, InetAddress.getLocalHost(), replyToPort);
        	}
        	else if (seq > expected)
        	{
        		//reject: cannot accept yet
        	}
        	else if (seq < expected)
        	{
        		//reject duplicate: already accepted
        	}
			
			
			//Receiver: print reply ack status
			BigDecimal replyACKTime = new BigDecimal(System.currentTimeMillis()).scaleByPowerOfTen(-3);
			System.out.println("["+replyACKTime+"]"+" ACK" + seq + " sent, expecting packet " + expected);
			
			
			listen_socket.close();
		}

		
		
		//sends ACK indicated by null data value
		public void sendACK(Integer seq, Integer buffLength, Character data, 
	    		InetAddress recIP, Integer recPort) throws IOException
		{
			byte[] b0 = (seq.toString() + " " + buffLength.toString() + " " + data).getBytes();
	    	DatagramSocket Listen_Socket = new DatagramSocket();
			DatagramPacket p1 = new DatagramPacket(b0,b0.length,recIP,recPort);
			Listen_Socket.send(p1);
			Listen_Socket.close();
		}
		
		

		private void startInputThread()
	    {
	        ReadInput input = new ReadInput();
	        Thread thread1 = new Thread(input);
	        thread1.start();
	    }
	    
	    
		Integer counter = 0;
		char[] buffer;  
		
		class ReadInput implements Runnable
	    {
	        public void run()
	        {
	            Scanner reader = new Scanner(System.in);
	            while (true)
	            {
	            	String[] cmdArray;
	            	System.out.print("node> ");
	                cmdArray = reader.nextLine().trim().replaceAll("\\s+"," ").split(" ");
	                
	                //checks if appropriate number of arguments were given
	                if (cmdArray.length!=2)
	                {
	                	System.out.println("Incorrect number of arguments. Restart with 1 argument.");
	                	return;
	                }
	                
	                /*
	                 * This thread will create threads for each packet and wait for ALL packet threads to finish.
	                 * Probably have to receive ACK on port + X
	                */
	                if (cmdArray[0].trim().toLowerCase().equals("send"))
	                {
	                	//resets counter & loads message cmdArray[1] into buffer (array)
	                	counter = 0;    
	                	buffer = cmdArray[1].toCharArray();
	                	
	                	//checks if window size is larger than the buffer
	                	if (winSize>buffer.length)
	                	{
	                		System.out.println("Window size is larger than the message. "
	                				+ " Aborting send. Restart program with reduced window size.");
	                		return;
	                	}
	                	
	                	
	                	
	                	//sends message character by character following window size
	                	while (counter<buffer.length)
	                	{
	                		for (int i = (0+counter); i < (winSize+counter); i++)
	                		{
	                			
	                			if (i < buffer.length)
	                			{
	                				DatagramSocket s1 = null;
	                				InetAddress ia;
	                			
	                				//info: [seqNumber | msgLength | data | sender's port]
	                				try
	                				{
	                					ia = InetAddress.getLocalHost();
	                					sendPacketGetACK(s1,i, buffer.length, buffer[i], ia, peerPort);
	                					
	                				} 
	                				catch (UnknownHostException e) {e.printStackTrace();} 
	                				catch (IOException e) {e.printStackTrace();}
	                				//catch (NumberFormatException e) {e.printStackTrace();}
	                				
	                				//{System.out.println(info[0] + " " + info[1] + " " + info[2] + " " + info[3]);}
	    		        		
	                				try
	                				{Thread.sleep(300);} 
	                				catch (InterruptedException e)
	                				{e.printStackTrace();}
	                			} //if statement prevents i going past counter 		
	                			
	                		} //end sendPacketGetACK
	                	}
	                	counter = 0;
	                	buffer = null;
	                }  //end if "send"
	               
	            }
	        }     
	    } //end ReadInput subclass
	    

	    /*
	     * @param [Sequence number | buffer length | data], recipientIP, recipientPort 
	    */
	    public void sendPacketGetACK(DatagramSocket socket, Integer seq, Integer buffLength, Character data, 
	    		InetAddress recIP, Integer recPort) throws IOException
	    {
	    	//send Packet
	    	
	    		//sequence number | buffer length | data (char)
	    		byte[] b0 = (seq.toString() + " " + buffLength.toString() + " " + data).getBytes();
	    	
	    		socket = new DatagramSocket();
	    		DatagramPacket Client_Register = new DatagramPacket(b0,b0.length,recIP,recPort);
	    		
	    						
	    		System.out.println(packetIsDropped());
	    		socket.send(Client_Register);
	    		
				
				
				//Sender: print send data time
				BigDecimal currTime = new BigDecimal(System.currentTimeMillis()).scaleByPowerOfTen(-3);
	            System.out.println("["+currTime+"] " + "packet" + seq + " " + data + " sent");
				
	            
	        //_______________________________________//_______________________________________//     
				
			//get ACK
	    		socket.setSoTimeout(500);
	    		byte[] b1 = new byte[1024];
	    		DatagramPacket pc = new DatagramPacket(b1,b1.length);
	    		
	    		//if not received ACK, then continuously resend this packet
	    		try
	    		{socket.receive(pc);}
	    		catch(SocketTimeoutException e)
	    		{	
	    			
	    			System.out.println("Timed out. Resending.");
	    			sendPacketGetACK(socket, seq , buffLength, data, recIP, recPort);
	    			return;
	    			//System.out.println("Program will terminate."); System.exit(0);
	    		}

	    		//Must trim after getData() function because it returns arbitrary whitespace
	    		String getMsg = new String(pc.getData()).trim();
	    		
				
	    		//returns seqNumber | msgLength | data | sender's port
	    		String port = String.valueOf(pc.getPort()).trim();
				
	    		String[] info = (getMsg + " "+port).replaceAll("\\s+"," ").split(" ");
	    		socket.close();
				
				
	    	//unpack listened info for convenience [seqNumber | buffLength | data | sender's port]
	        	Integer ackSeq = Integer.valueOf(info[0]);
	        	Integer ackBuffLength = Integer.valueOf(info[1]);
	        	Character ackData = info[2].charAt(0);
	        	Integer ackReplyToPort = Integer.valueOf(info[3]);
	    		
	       //Based on ack's info, decide to increment counter
	        	if (ackSeq >= counter)
	        	{
	        		int x = ackSeq-counter;
	        		counter = counter + x + 1;
	        	}
	        	else
	        	{
	        		//received duplicate ack
	        	}
	    	
	    	//Sender: print receive ACK status
	    		BigDecimal getACKTime = new BigDecimal(System.currentTimeMillis()).scaleByPowerOfTen(-3);
				System.out.println("["+getACKTime+"]"+" ACK" + ackSeq + " received, window moves to " + (counter));
	    		
	    	
	  } //end sendPacketGetACK
	    
	    
	 
	 
	  public Boolean packetIsDropped()
	  {
		  Boolean dropped = false;
		  if (mode.equals("-d"))
		  {
			  if ((counter+1)%nVal == 0)
			  {
					dropped = true;
			  }
		  }
		  else if (mode.equals("-p"))
		  {
			  double rand = Math.random();
			  if (0 < rand && rand < pVal)
			  {
				  dropped = true;
			  }
		  }
		  return dropped;
	  }
	  
	  public String printStatus(String status)
	  {
		return status;
	  }
	  
}
