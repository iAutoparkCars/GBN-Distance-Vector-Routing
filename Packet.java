
public class Packet
{
	Boolean dropped = false;
	
	Packet(Integer counter, Integer n)
	{
		if ((counter+1)%n == 0)
		{
			dropped = true;
		}
	}
	
	Packet(Double p)
	{
		
	}

	

	public void probability()
	{
		
	}
	
	
	public Boolean isDropped()
	{
		return dropped;
	}
	
	
}

/*if (mode.equals("-d"))
		{
			
		}
		else if (mode.equals("-p"))
		{
			
		}*/