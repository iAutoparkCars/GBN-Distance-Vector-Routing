import java.util.*;
import java.net.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;


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
	    
	    private int sendACK = 0;
	    
	    //used to calculate summary
	    Integer ackfailcounter = 1;
	    Integer ACKsSent = 0;
    	Integer ACKsDropped = 0;
		Integer summaryCounter = 0;
	    
	    public Boolean ackIsDropped()
	    {
	    	Boolean dropped = false;
	    	if (mode.equals("-d"))
	    	{
	    		if ((ackfailcounter)%nVal == 0)
	    		{
	    			dropped = true;
	    			ACKsDropped++;
	    		}
	    	}
	    	else if (mode.equals("-p"))
	    	{
	    		double rand = Math.random();
	    		//System.out.println(rand);
	    		if (0 < rand && rand < pVal)
	    		{
	    			dropped = true;
	    			ACKsDropped++;
	    		}
	    	}	
	    	ackfailcounter++;
	    	ACKsSent++;
	    	//System.out.println(dropped);
	    	return dropped;
	    }
	    
	   
	    
		public Node(String args[]) throws IOException, InterruptedException
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
	        
	       
			//listen for packets indefinitely
	        while(true)
	        {
	        	listenPacketSendACK(s1,selfPort);
	        }
	    
	    }
		
		
		
		/* @param Current Socket: listen and reply with ACK
		 * 		  Port: get port of sender
		 *  
		*/
		public void listenPacketSendACK(DatagramSocket listen_socket, Integer recPort) throws IOException, InterruptedException
		{
			//retrieve message
			listen_socket = new DatagramSocket(recPort);
			byte[] b1 = new byte[1024];
			DatagramPacket pc = new DatagramPacket(b1,b1.length);
			
			//if accepted last data -- cleanup
			if ((sequence == (msgLength-1)))
			{
				//System.out.format("%n%nsequence: %d  __ msgLength: %d __ expected: %d%n __ ACKsSent: %d __ ACKsDropped: %d%n%n", sequence, msgLength, expected,ACKsSent,ACKsDropped);
				if (ACKsSent!=0 && expected == 0)
				{
					listen_socket.close();
					
				}
				
				sequence = 0;
				msgLength = 0;
				//expected = 0;
				//System.out.format("%n%nAFTER SUMMARY: sequence: %d  __ msgLength: %d __ expected: %d%n __ ACKsSent: %d __ ACKsDropped: %d%n%n", sequence, msgLength, expected,ACKsSent,ACKsDropped);
				
				listen_socket.close();
				return;
			}	// end if statement to indicate received last packet 
			
			
			listen_socket.setSoTimeout(30000);
			
			try{listen_socket.receive(pc);}
			catch(SocketTimeoutException e)
			{
				System.out.println("Program ended to conserve resources. (half minute timeout). Please restart program.");
				System.exit(0);
			}
			

			
			
			
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
			
        	int AckSeq = 0;
        	
        	//unpack listened info for convenience [seqNumber | buffLength | data | sender's port]
        	//System.out.println("\nsequence received: " + info[0] + " buffLength received: " + info[1] + " data: " + info[2]);
        	if (seq == expected && seq < buffLength)
        	{
        		//Receiver: print received data time
        		BigDecimal getDataTime = new BigDecimal(System.currentTimeMillis()).scaleByPowerOfTen(-3);
        		System.out.println("["+getDataTime+"] " + "packet" + seq + " " + data + " received");
        		
        		expected++;
        		AckSeq = seq;
        	}
        	else if (seq > expected && sequence < msgLength)
        	{
        		//reject: cannot accept yet
        		BigDecimal getDataTime = new BigDecimal(System.currentTimeMillis()).scaleByPowerOfTen(-3);
        		System.out.println("["+getDataTime+"] " + "packet" + seq + " " + data + " discarded. Can only accept expected: "+ expected);
        		
        	}
        	else if (seq < expected && sequence < msgLength)
        	{
        		//reject duplicate: already accepted
        		BigDecimal getDataTime = new BigDecimal(System.currentTimeMillis()).scaleByPowerOfTen(-3);
        		System.out.println("["+getDataTime+"] " + "packet" + seq + " " + data + " discarded (duplicate rejected)");
        		AckSeq = seq;
        	}
        	
			if (expected == 0)
				AckSeq = -5;
			
        	if (!ackIsDropped() && sequence < msgLength)
        	{
        		sendACK(AckSeq, buffLength, null, InetAddress.getLocalHost(), replyToPort);
        	}
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
		
			//send ACK status message
			String endStatus = " sent, expecting packet " + expected;
			if (sequence == (msgLength-1) && summaryCounter == 0 && expected > 0)
        	{
        		summaryCounter++;
        		Double lossRate = ((double)ACKsDropped/(double)ACKsSent);
				String summary = "============ [Summary] " + ACKsDropped+"/"+ACKsSent + " ACK's discarded, loss rate = "+  lossRate + " ============"
						+ " Leftover incoming packets within window may follow. This is intended. \nnode>";
				

				
				endStatus = " sent. \n" + summary;
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//used to reset variables for summary
				ackfailcounter = 1;
				ACKsSent = 0;
				ACKsDropped = 0;
				
				expected = 0;
				summaryCounter = 0;
				
        	}
        	
			//Receiver: print reply ack status
			BigDecimal replyACKTime = new BigDecimal(System.currentTimeMillis()).scaleByPowerOfTen(-3);
			System.out.println("["+replyACKTime+"]"+" ACK" + seq + endStatus);
			
			if ( (expected-1) < 0)
			{
				//summaryCounter = 0;
			}
		
		}
		
		

		private void startInputThread()
	    {
	        ReadInput input = new ReadInput();
	        Thread thread1 = new Thread(input);
	        thread1.start();
	    }
	    
	    
		Integer counter = 0;
		Integer failcounter = 1;
		char[] buffer;  
		
		//used for summary
		Integer packetsSent = 0;
    	Integer packetsDropped = 0;
		
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
	                	System.exit(0);
	                }
	                
	                /*
	                 * This thread will create threads for each packet and wait for ALL packet threads to finish.
	                 * Probably have to receive ACK on port + X
	                */
	                if (cmdArray[0].trim().toLowerCase().equals("send"))
	                {
	                	//resets counter & loads message cmdArray[1] into buffer (array)
	                	counter = 0;  
	                	packetsSent = 0;
	                	packetsDropped = 0;
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
	                		
	                				
	                				InetAddress ia;
	                			
	                				//info: [seqNumber | msgLength | data | sender's port]
	                				try
	                				{
	                					ia = InetAddress.getLocalHost();
	                					sendPacketGetACK(ia);
	                				
	                					
	                				} 
	                				catch (UnknownHostException e) {e.printStackTrace();} 
	                				catch (IOException e) {e.printStackTrace();}
	                				//catch (NumberFormatException e) {e.printStackTrace();}
	                				
	                				//{System.out.println(info[0] + " " + info[1] + " " + info[2] + " " + info[3]);}
	    		        		
	                				try
	                				{Thread.sleep(300);} 
	                				catch (InterruptedException e)
	                				{e.printStackTrace();}
	                		
	                	
	                	} //end WHILE: finish sending all data
	                	Double lossRate = ((double)packetsDropped/(double)packetsSent);
	                	System.out.format("============ [Summary] %d/%d packets discarded, loss rate = %f ============%n"
	                			, packetsDropped, packetsSent, lossRate);
	                	
	                	
	                	
	                	
	                	//reset the count for summary
	                	packetsSent = 0;
	                	packetsDropped = 0;
	                	
	                	//resets count for packet sending
	                	counter = 0;
	                	Integer failcounter = 1;
	                	buffer = null;
	                	
	                	
	                	
	                }  //end if "send"
	               
	            }
	        }     
	    } //end ReadInput subclass
	    

		final DatagramSocket s1 = new DatagramSocket();
	    /*
	     * @param [Sequence number | buffer length | data], recipientIP, recipientPort 
	    */
	    public void sendPacketGetACK(InetAddress recIP) throws IOException
	    {
	    	//send Packet
	    	
	    	//  SendPacket [seqNumber | msgLength | data | sender's port]
	    	
	    	
	    	for (int i = (0+counter); i < (winSize+counter); i++)
    		{
    			
    			if (i < buffer.length)
    			{
    				SendThread send1 = new SendThread(i, buffer.length, buffer[i], recIP, peerPort);
    				send1.start();
    				
    				try {Thread.sleep(100);}
    				catch (InterruptedException e)
    				{e.printStackTrace();}
    				
    				/*new Thread()
    				{
    					public void run() {
    						
    						//sequence number | buffer length | data (char)
    						byte[] b0 = (seq.toString() + " " + buffLength.toString() + " " + data).getBytes();
    						
    						DatagramPacket Client_Register = new DatagramPacket(b0,b0.length,recIP,recPort);
    						
    						
    						//if (!packetIsDropped())		
    						{try
    						{s1.send(Client_Register);} 
    						catch (IOException e) {e.printStackTrace();}}
    						
    						
    						
    						//Sender: print send data time
    						BigDecimal currTime = new BigDecimal(System.currentTimeMillis()).scaleByPowerOfTen(-3);
    						System.out.println("["+currTime+"] " + "packet" + seq + " " + data + " sent");        	 
    						
    					}
    				}.start();		//end sending thread
*/    				
    			
    			}
    			
    		}	//end inner loop for window size
	    	
	    	
	    	
	    		/*//sequence number | buffer length | data (char)
	    		byte[] b0 = (seq.toString() + " " + buffLength.toString() + " " + data).getBytes();
	    	
	    		socket = new DatagramSocket();
	    		DatagramPacket Client_Register = new DatagramPacket(b0,b0.length,recIP,recPort);
	    		
	    		
	    		//if (!packetIsDropped())		
	    			{socket.send(Client_Register);}
	    		
				
				
				//Sender: print send data time
				BigDecimal currTime = new BigDecimal(System.currentTimeMillis()).scaleByPowerOfTen(-3);
	            System.out.println("["+currTime+"] " + "packet" + seq + " " + data + " sent");*/
				
	            
	        //_______________________________________//_______________________________________//     
				
			//get ACK
	    		s1.setSoTimeout(500);
	    		byte[] b1 = new byte[1024];
	    		DatagramPacket pc = new DatagramPacket(b1,b1.length);
	    		
	    		//if not received ACK, then continuously resend this packet
	    		try
	    		{s1.receive(pc);}
	    		catch(SocketTimeoutException e)
	    		{	
	    			
	    			System.out.println("Timed out. Resending.");
	    			sendPacketGetACK(recIP);
	    			return;
	    		}
	    		
	    		

	    		//Must trim after getData() function because it returns arbitrary whitespace
	    		String getMsg = new String(pc.getData()).trim();
	    		
				
	    		//returns seqNumber | msgLength | data | sender's port
	    		String port = String.valueOf(pc.getPort()).trim();
				
	    		String[] info = (getMsg + " "+port).replaceAll("\\s+"," ").split(" ");
	    		//s1.close();
				
				
	    	//unpack listened info for convenience [seqNumber | buffLength | data | sender's port]
	        	Integer ackSeq = Integer.valueOf(info[0]);
	        	Integer ackBuffLength = Integer.valueOf(info[1]);
	        	Character ackData = info[2].charAt(0);
	        	Integer ackReplyToPort = Integer.valueOf(info[3]);
	    		
	        	if (ackSeq == -5)
	        	{
	        		System.out.println("Sequence with -5 indicates receiver has not received any data in correct order. Resend.");
	        		sendPacketGetACK(recIP);
	    			return;
	        	}
	        	
	        	
	        	
	        	
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
	    	
	        	/*if (counter == buffer.length)
	        	{
	        		counter = 0;
	        	}*/
	        	
	        	int printWindow = counter;
	        	if (counter == buffer.length)
	        		printWindow = 0;
	        	
	    	//Sender: print receive ACK status
	    		BigDecimal getACKTime = new BigDecimal(System.currentTimeMillis()).scaleByPowerOfTen(-3);
				System.out.println("["+getACKTime+"]"+" ACK" + ackSeq + " received, window moves to " + printWindow);
	    		
	    	
	  } //end sendPacketGetACK
	    
	    
	 
	 //calculates whether to fail packet
	  public Boolean packetIsDropped()
	  {
		  Boolean dropped = false;
		  if (mode.equals("-d"))
		  {
			  if ((failcounter)%nVal == 0)
			  {
					dropped = true;
					packetsDropped++;
			  }
		  }
		  else if (mode.equals("-p"))
		  {
			  Random r = new Random();
			  double rand = r.nextDouble();
			  if (0 < rand && rand < pVal)
			  {
				  dropped = true;
				  packetsDropped++;
			  }
		  }
		  failcounter++;
		  packetsSent++;
		  return dropped;
	  }
	  
	  class SendThread extends Thread
	  { 

		  public SendThread (Integer seq1, Integer buffLength1, Character data1, 
		    		InetAddress recIP1, Integer recPort1) { 
		    run(seq1, buffLength1, data1, recIP1, recPort1);
		  }

		  public void run(Integer seq1, Integer buffLength1, Character data1, 
		    		InetAddress recIP1, Integer recPort1) { 
		  //System.out.println("Run: "+ seq1); 
			//sequence number | buffer length | data (char)
				
			  byte[] b0 = (seq1.toString() + " " + buffLength1.toString() + " " + data1).getBytes();
				
				DatagramPacket Client_Register = new DatagramPacket(b0,b0.length,recIP1,recPort1);
				
				
				if (!packetIsDropped())		
				{try
				{s1.send(Client_Register);} 
				catch (IOException e) {e.printStackTrace();}}
				
				
				
				//Sender: print send data time
				BigDecimal currTime = new BigDecimal(System.currentTimeMillis()).scaleByPowerOfTen(-3);
				System.out.println("["+currTime+"] " + "packet" + seq1 + " " + data1 + " sent"); 
		  } 
	  }
	  
}
