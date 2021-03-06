import javax.swing.*;
import java.awt.*;
import java.util.*;

public class PianoWindow
{
	static private PianoGlassPane pianoGlassPane;
	
	public void createAndShowGUI(Map<String, Double> notes)
	{
		JFrame f = new JFrame("Piano");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container contentPane = f.getContentPane();
		contentPane.add(new PianoPanel());
		
		pianoGlassPane = new PianoGlassPane(notes);
		f.setGlassPane(pianoGlassPane);
		pianoGlassPane.setVisible(true);
		//pianoGlassPane.repaint();
		f.setResizable(false);
		f.pack();
		f.setVisible(true);
		
	}
}
@SuppressWarnings("serial")
class PianoGlassPane extends JComponent
{
	private Point point = new Point(8, 125);
	private int interval = 24;

	Map<String, Double> notes;
	protected void paintComponent(Graphics g)
	{
		int i = 0;		
		g.setColor(Color.red);
		ArrayList<String> keys = new ArrayList<String>(notes.keySet());
		Collections.sort(keys, new NoteComparator());
		for(String n : keys)
		{
			g.setColor(new Color(255, 255 - (int)(255*notes.get(n)), 255 - (int)(255*notes.get(n))));
			if(n.contains("#"))
			{
				g.fillOval(point.x + i * interval - 15, 50, 12, 12);
			}
			else
			{
				g.fillOval(point.x + i * interval - 6, point.y - 6, 18, 18);
				i++;
			}
		}
		
	}
	
	public PianoGlassPane(Map<String, Double> notes)
	{
		this.notes = notes;
	}
}

@SuppressWarnings("serial")
class PianoPanel extends JPanel
{
	ImageIcon piano = new ImageIcon("piano.png");
	public PianoPanel()
	{
		setBorder(BorderFactory.createLineBorder(Color.BLACK));

		JLabel label = new JLabel();
		label.setIcon(piano);
		add(label);
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(piano.getIconWidth(), piano.getIconHeight());
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

	}
}
