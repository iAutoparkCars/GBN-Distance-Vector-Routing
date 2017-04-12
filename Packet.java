
public class Packet
{
	private Integer seq = 0;
	private Character data = null;
	
	Packet(Integer seq, Character data)
	{
		this.seq = seq;
		this.data = data;
	}

	public Integer getSeq()
	{
		return this.seq;
	}
	
	public Character getData()
	{
		return this.data;
	}
	
}
