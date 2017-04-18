import java.io.IOException;
import java.util.Scanner;

class gbnnode2{
    
    
    public static void main (String args[]) throws IOException, InterruptedException
    {
    	
    	try
    	{
    		String mode = args[3];
    		if (mode.equals("-d") && args.length==5)
            {
    			if (Integer.valueOf(args[4]) <= 0)
    			{
    				System.out.println("Incorrect n value. Restart program with n value > 0");
    				System.exit(0);
    			}
                Node n1 = new Node(args);
            }
            else if (mode.equals("-p") && args.length==5)
            {
            	if (Double.valueOf(args[4]) < 0 || Double.valueOf(args[4]) > 1.0)
    			{
    				System.out.println("Incorrect p value. Restart program with p value between 0 and 1");
    				System.exit(0);
    			}
            	Node n2 = new Node(args);
            }    
            else 
                System.out.println("Incorrect arguments");
    	}
    	catch (ArrayIndexOutOfBoundsException e)
    	{
        	System.out.println("Incorrect arguments");
    	}
        
    
    }  
    
}