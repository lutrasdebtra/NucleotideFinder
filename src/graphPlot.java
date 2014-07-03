/*
 * Plots nucleotide distributions in 200 length chunks
 * Stuart Bradley
 * 2-6-2014
 * 
 * What's erichseifert
 * translated from German mean?
 * Oh wait, it's a name.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;

@SuppressWarnings("serial")
public class graphPlot extends JFrame {
	@SuppressWarnings("unchecked")
	public graphPlot(String sequence, int chunk, String nucleo, String title) {

		final JPanel panel = new JPanel(new BorderLayout());
		//Preferred Size is set as to keep graphs reasonably equivalent for different sequence lengths
		int numGraphs = sequence.length() / 200;
		panel.setPreferredSize(new Dimension(1000, numGraphs * 375));
		JScrollPane scroll = new JScrollPane(panel);
		getContentPane().add(scroll);
		
		ArrayList<String> sequences = new ArrayList<String>();

		// Chunk sequences into ArrayList
		for (int i = 0; i < sequence.length();i++) {
			try {
				String chunk_str = sequence.substring(i, i+chunk);
				sequences.add(chunk_str);
			} catch (StringIndexOutOfBoundsException e) {
				break;
			}
		}
		
		// Count number of matches per chunk
		int[] seq_data = new int[sequences.size()];

		int array_counter = 0;
		for (String s : sequences) {
			int counter = 0;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c == nucleo.charAt(0)) {
					counter++;
				}
			}
			seq_data[array_counter] = counter;
			array_counter++;
		}

		// Convert chunk counts to DataTable in 200 long groups
		int[][] seq_data_200 = chunkArray(seq_data, 200);
		DataTable[] listData = new DataTable[seq_data_200.length];
		for (int i = 0; i < seq_data_200.length; i++){
			DataTable data = new DataTable(Integer.class, Integer.class);
			for (int j = 0; j < seq_data_200[i].length; j++) {
				data.add(j, seq_data_200[i][j]);
			}
			// Add an additional max size to the DataTable so axis will always go 0 .. chunk
			data.add(seq_data_200[i].length, chunk);
			listData[i] = data;
		}

		// Build Graph
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panel.setLayout(new GridLayout(seq_data_200.length,1,20,20));

		for (int i = 0; i < listData.length; i++) {
			XYPlot plot = new XYPlot(listData[i]);

			Color color = new Color(0, 0, 255);
			// Used to colourise graphs based on large chunks
			/*
			* if (containsChunk(seq_data_200[i], chunk)) {
			* 	color = new Color(255, 0, 0);
			* } else if (containsChunk(seq_data_200[i], chunk-1)) {
			* 	color = new Color (255, 100, 0);
			* }
			*/
			
			LineRenderer lines = new DefaultLineRenderer2D();
			lines.setColor(color);
			plot.setLineRenderer(listData[i], lines);

			PointRenderer points = new DefaultPointRenderer2D();
			points.setColor(color);
			plot.setPointRenderer(listData[i], points);

			// Spaces graphs so axis and title are visible
			double insetsTop = 20.0, insetsLeft = 60.0, insetsBottom = 60.0, insetsRight = 40.0;
			plot.setInsets(new Insets2D.Double(insetsTop, insetsLeft, insetsBottom, insetsRight));
			plot.getTitle().setText(getTitle(i, sequence, title));
			
			// Sets up X axis
			plot.getAxisRenderer(XYPlot.AXIS_X).setLabel("Bases");
			plot.getAxisRenderer(XYPlot.AXIS_X).setCustomTicks(getTicksX(i, sequence));
			
			// Sets up Y axis
			plot.getAxisRenderer(XYPlot.AXIS_Y).setLabel("Number of " + nucleo + "'s");
			plot.getAxisRenderer(XYPlot.AXIS_Y).setMinorTicksVisible(false);
			plot.getAxisRenderer(XYPlot.AXIS_Y).setTickSpacing(1);
			plot.getAxisRenderer(XYPlot.AXIS_Y).setCustomTicks(getTicksY(chunk));

			plot.getNavigator().setZoomable(false);
			plot.getNavigator().setPannable(false);

			panel.add(new InteractivePanel(plot), BorderLayout.CENTER);
		} 


		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1500, 1000);
	}

	// Splits an array into chunkSize chunks
	private static int[][] chunkArray(int[] array, int chunkSize) {
		int numOfChunks = (int) Math.ceil((double) array.length / chunkSize);
		int[][] output = new int[numOfChunks][];

		for (int i = 0; i < numOfChunks; ++i) {
			int start = i * chunkSize;
			int length = Math.min(array.length - start, chunkSize);

			int[] temp = new int[length];
			System.arraycopy(array, start, temp, 0, length);
			output[i] = temp;
		}

		return output;
	}
	
	// Returns graph title
	private String getTitle(int i, String sequence, String title) {
		// Gets title from FastaReader.titles
		String[] s = title.split(" ");
		
		// Checks whether it's the last graph or not and returns accordingly
		if (i * 200 + 200 < sequence.length()) {
			return (i * 200) + " to " + (i * 200 + 200) + " - " + s[1];
		} else {
			return (i * 200) + " to " + sequence.length()+ " - " + s[1];
		}
	}
	
	// Returns a custom set of ticks for X that are the bases of the sequence
	private Map<Double, String> getTicksX(int i, String sequence) {
		Map<Double, String> ticks = new HashMap<Double, String>();
		int start = i * 200;
		int end;

		if (i * 200 + 200 < sequence.length()) {
			end = (i * 200 + 200);
		} else {
			end = sequence.length();
		}
		double counter = 0.0;
		for (int j = start; j < end; j++) {
			ticks.put(counter, Character.toString(sequence.charAt(j)));
			counter++;
		}
		// Additional tick for the fake max value
		ticks.put(counter, ".");
		return ticks;
	}
	
	// Creates 0 .. 4 custom ticks, this doesn't override automatic ticking and thus is ineffective
	private Map<Double, String> getTicksY(int chunks) {
		Map<Double, String> ticks = new HashMap<Double, String>();
		for (int i = 0; i < chunks; i++) {
			ticks.put((double) i, Integer.toString(i));
		}
		return ticks;
	}
	// Used to check whether a graph's colour needs to be altered
/*
*	 private boolean containsChunk(int[] arraySeq, int chunk) {
*		 for (int i = 0; i < arraySeq.length; i++) {
*			 	if (arraySeq[i] == chunk) {
*			 		return true;
*			 	}
*		 }
*	
*		 return false;
*	 }
*/
}
