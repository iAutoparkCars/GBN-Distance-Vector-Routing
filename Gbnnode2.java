import java.io.IOException;
import java.util.Scanner;

class Gbnnode2{
    
    
    public static void main (String args[]) throws IOException
    {
        String mode = args[3];
        if (mode.equals("-d") && args.length==5)
        {
            Node n1 = new Node(args);
        }
        else if (mode.equals("-p") && args.length==5)
        {
            Node n2 = new Node(args);
        }    
        else 
            System.out.println("Incorrect arguments");
    
    }  
    
}