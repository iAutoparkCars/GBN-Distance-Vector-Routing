import java.util.Scanner;

class Gbnnode{
    
    
    public static void main (String args[])
    {
        String mode = args[3];
        if (mode.equals("-d") && args.length==5)
        {
            Determ d1 = new Determ();
        }
        else if (mode.equals("-p") && args.length==5)
        {
            Prob p1 = new Prob();
        }    
        else 
            System.out.println("Incorrect arguments");
    
    }  
    
}