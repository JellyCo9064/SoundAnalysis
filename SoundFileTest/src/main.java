import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class main
{

	public static final int SAMPLE_RATE = 44100;

	public static void main(String[] args)
	{				
		double duration = 0.2;
		long numFrames = (long) (SAMPLE_RATE * duration);
		
		//Create pure sine wave sound file
		Scanner console = new Scanner(System.in);
		System.out.println("Enter Frequency: ");
		double frequencyIn = console.nextDouble();
		console.close();
		try
		{
			WavFile newFile = WavFile.newWavFile(new File("sound.wav"), 1, numFrames, 2, SAMPLE_RATE);
			writeFile(newFile, (int)numFrames, frequencyIn);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		
		//Read sound data into list
		WavFile readFile = null;
		try
		{
			readFile = WavFile.openWavFile(new File("sound.wav"));
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		ArrayList<Float> values = new ArrayList<Float>();		
		readFile(readFile, values);
			
		
		//Read frequencies and note names
		TreeMap<Double, String> notes = new TreeMap<Double, String>();
		readNotes(new File("Notes.csv"), notes);
		

		long time = System.nanoTime();
		ArrayList<Double> transform = new ArrayList<Double>();
		Map<String, Double> intensities = new TreeMap<String, Double>();
		double max = -1;
		double frequency = -1;
		//Conduct fourier transform on existing notes, determine maximum
		for (Double d : notes.keySet())
		{
			double temp = doFourier(values, d, SAMPLE_RATE);
			transform.add(temp);
			if (temp > max)
			{
				max = temp;
				frequency = d;
			}
		}
		//Populate map with note names and transform values as percentage of max
		int count = 0;
		for(Double d : notes.keySet())
		{
			double percent = (transform.get(count) / max);
			//System.out.println(notes.get(d) + ": " + "\t" + percent);
			//lights.add(notes.get(d) + ", " + percent);
			intensities.put(notes.get(d), percent);
			count++;
		}	

		
		
		//Create window
		List<JCheckBox> buttons = new ArrayList<JCheckBox>();
		PianoWindow window = new PianoWindow();
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run()
			{
				window.createAndShowGUI(intensities);					
			}
		}	
	
		);

		System.out.println("Note: " + notes.get(frequency));
		System.out.println("Frequency: " + frequency);
		System.out.println("Time: " + (System.nanoTime() - time) * Math.pow(10, -9));
		//printCSV(lights, "lightData.csv");
//
//		ArrayList<Double> peaks = new ArrayList<Double>();
//
//		System.out.println(findPeaks(transform, 4));
//
//		PrintStream soundData = null;
//		PrintStream transformData = null;
//		try
//		{
//			soundData = new PrintStream(new File("soundData.txt"));
//			transformData = new PrintStream(new File("transformData.txt"));
//		}
//		catch(Exception e)
//		{
//			System.out.println(e.getMessage());
//		}
//
//		for (int i = 0; i < values.size(); i++)
//		{
//			soundData.print(i + 20 / (float) SAMPLE_RATE + ",");
//			soundData.print(values.get(i) + "\n");
//		}
//		for (int i = 0; i < transform.size(); i++)
//		{
//			transformData.print(i + 20 + ",");
//			transformData.print(transform.get(i) + "\n");
//		}
	}
	
	public static <T> void printCSV(List<T> list, String fileName)
	{
		PrintStream toPrint = null;
		try
		{
			toPrint = new PrintStream(new File(fileName));
		}
		catch(Exception e)
		{
			System.out.println("Write to File failed");
		}
		
		for(T item : list)
		{
			toPrint.println(item.toString());
		}
	}
	
	public static void readNotes(File readFile, Map<Double, String> noteMap)
	{
		Scanner scan = null;
		try
		{
			scan = new Scanner(readFile);
		}
		catch(Exception e)
		{
			System.out.println("File not found");
		}
		
		while(scan.hasNext())
		{
			String line = scan.nextLine();
			noteMap.put(Double.parseDouble(line.substring(line.indexOf(",") + 1)), line.substring(0,line.indexOf(",")));
		}
		
	}
	
	
	
	

	public static void writeFile(WavFile newFile, int numFrames, double frequency)
	{
		try
		{
			double[] buffer = new double[100];
			long frameCounter = 0;

			while (frameCounter < numFrames)
			{
				long remaining = newFile.getFramesRemaining();
				int toWrite = (remaining > 100) ? 100 : (int) remaining;

				for (int s = 0; s < toWrite; s++, frameCounter++)
				{
					buffer[s] = Math.sin(2 * Math.PI * frequency * frameCounter / SAMPLE_RATE)
					+ Math.sin(2 * Math.PI * 553.3653 * frameCounter / SAMPLE_RATE) 
					+  Math.sin(2 * Math.PI * 658.2551 * frameCounter / SAMPLE_RATE);
				}
				newFile.writeFrames(buffer, toWrite);
			}

			newFile.close();
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	public static void readFile(WavFile file, ArrayList<Float> readList)
	{
		try
		{
			double[] buffer = new double[100];
			int framesRead;

			do
			{
				framesRead = file.readFrames(buffer, 100);

				for (double d : buffer)
				{
					readList.add((float) d);
				}
			} while (framesRead != 0);

			file.close();
		} catch (Exception e)
		{

		}
	}

	public static double doFourier(ArrayList<Float> values, double frequency, int sampleRate)
	{

		Complex total = new Complex(0, 0);

		for (int n = 0; n < values.size(); n += 2)
		{

			// long time = System.currentTimeMillis();
			double i = (double) -2 * Math.PI * frequency * n / sampleRate;

			Complex c = new Complex(Math.cos(i), Math.sin(i));
			Complex mag = c.scale(values.get(n).doubleValue());

			total = total.plus(mag);
			// System.out.println((time - System.currentTimeMillis()));
		}

		return total.abs();
	}

	public static Map<Integer, Double> findPeaks(ArrayList<Double> values, int m)
	{
		Map<Integer, Double> result = new HashMap<Integer, Double>();

		boolean metRise = false;
		double previous = -1;
		int saveIndex = -1;
		double saveValue = -1;
		int riseCount = 0, fallCount = 0;
		for (int i = 0; i < values.size(); i++)
		{
			double d = values.get(i);

			// Increasing
			if (d > previous)
			{
				// Continuing to Increase
				if (fallCount == 0)
					riseCount++;
				// Switched from Decreasing to Increasing
				else
				{
					System.out.println("Switched to Increasing");
					// If
					if (metRise && fallCount >= m)
					{
						result.put(saveIndex, saveValue);
						metRise = false;
					}
					riseCount = 1;
					fallCount = 0;
				}
			}
			// Decreasing
			else
			{
				// Continuing to Decrease
				if (riseCount == 0)
					fallCount++;
				// Switched from Increasing to Decreasing
				else
				{
					System.out.println("Switched to Decreasing!");
					// If sufficient # of data points increasing
					if (metRise = riseCount >= m)
					{
						// Save possible peak index and value
						saveIndex = i - 1;
						saveValue = previous;
					}

					fallCount = 1;
					riseCount = 0;
				}
			}
			previous = d;

			if (i == values.size() - 1)
				result.put(saveIndex, saveValue);
		}
		return result;
	}

}
