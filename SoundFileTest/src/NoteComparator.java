import java.util.Comparator;

public class NoteComparator implements Comparator<String>
{
	public int compare(String s1, String s2)
	{
		char[] char1 = s1.toCharArray();
		char[] char2 = s2.toCharArray();
		for(int i = 0; i < char1.length; i++)
		{
			if(char1[i] == 65 || char1[i] == 66)
				char1[i] += 26;
		}
		for(int i = 0; i < char2.length; i++)
		{
			if(char2[i] == 65 || char2[i] == 66)
				char2[i] += 26;
		}
		
		if(char1[char1.length - 1] < char2[char2.length - 1])
			return -1;
		else if(char1[char1.length - 1] > char2[char2.length - 1])
			return 1;		
		
		if(char1[0] < char2[0])
			return -1;
		else if(char1[0] > char2[0])
			return 1;
		
		if(char1.length > char2.length)
			return 1;
		else if (char1.length < char2.length)
			return -1;
		else
			return 0;

	}
}
