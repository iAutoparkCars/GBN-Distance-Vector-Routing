GBN Emulation for PA2:


	My score: 80/100     Mean: 67/100


How to compile (no makefile)
	javac gbnnode.java
	
	javac dvnode.java


How to run

		Go-Back-N Protocol:
			A   (sender): java gbnnode 3500 2000 3 -p 0
			B (receiver): java gbnnode 2000 3500 3 -p .6

	**Note: the sender will never fail sending packet with probability 0 in arguments **
	**Note: I tested using these arguments. Using different arguments
		may or may not break my program **	


Go-Back-N Protocol Comments:     
		When the receiver sends ACK with a sequence of -5, this indicates 
	receiver has not received any data in correct order for this window. Resend entire window.

		When the receiver has finished receiving ALL the data, the summary will print.
	After the summary prints, there will sometimes be packets still received within the same window. 
	The receiver will just reject these extraneous data as intended. 
	

		If node A is the sender and node B is the receiver,
	my program allows A to send to B, then B to send to A.
	
		Look for "node>" prompting for input. Sometimes the "node>" gets printed out of order 
	due to the extraneous packets.
	If you send from A to B, at B's output you may have to look for the "node>". 
	Just type in "send UrMessage" in B's command line to send from B to A and it
	will send. Because there is multithreading, (I am not too proficient), A
	may not receive all the data from B. 
	
	There is a half minute timeout for both nodes. Restart program after timeout.




