/*
 * Graphs distributions of Nucleotides in a Fasta Sequence
 * Stuart Bradley
 * 2-6-2014
 * 
 * Lots of teenage boys
 * Could have used this program while 
 * losing their V-cards
 */

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class gFinder extends JFrame {

	File fasta_file;
	String nucleo = "G";
	// Size of ORFs to count nucleotides in
	int chunk; 
	ArrayList<String> sequences;
	// Init for File reader 
	FastaReader fast = new FastaReader();
	graphPlot frame;
	// Sets which graph is selected for viewing
	int current_graph = 0;

	public gFinder() {
		initUI();
	}

	public void initUI() {
		final JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.setLayout(new GridLayout(4,2,5,5));
		
		// File Opener
		JLabel fileLabel = new JLabel("Open Fasta File:");
		panel.add(fileLabel);
		JButton open = new JButton("Open");
		open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JFileChooser fileopen = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("fasta files", "fasta");
                fileopen.addChoosableFileFilter(filter);
                
                int ret = fileopen.showDialog(panel, "Open file");
                
                if (ret == JFileChooser.APPROVE_OPTION) {
                    fasta_file = fileopen.getSelectedFile();
                    // Read file and get result
                    sequences = fast.FastaReader_sequences(fasta_file);
                }
            }
        });
		
		panel.add(open);
		
		// Chunk size selector
		final JLabel chunkLabel = new JLabel("Chunk Size [1]:");
		panel.add(chunkLabel);
		final JSlider chunkSlider = new JSlider(1,20,1);
		
		chunkSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                int value = chunkSlider.getValue();
                chunkLabel.setText("Chunk Size [" + value + "]:");
                chunk = value;
            }
		});
		
		panel.add(chunkSlider);
		
		// Nucleotide picker
		String[] nucleotides = {"G","C","A", "T"};
		JLabel nucleotideLabel = new JLabel("Nucleotide:");
		panel.add(nucleotideLabel);
		final JComboBox<String> nucleotideCombo = new JComboBox<String>(nucleotides);
		
		nucleotideCombo.addItemListener(new ItemListener() {
			@Override 
			public void itemStateChanged(ItemEvent event) {
				nucleo = nucleotideCombo.getSelectedItem().toString();
			}
		});
		
		panel.add(nucleotideCombo);
		
		// Draw button
		JLabel redrawLabel = new JLabel("Draw:");
		panel.add(redrawLabel);
		
		JPanel panel_buttons = new JPanel();
		JButton backward = new JButton("<");
		
		backward.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	// if frame is empty program hasn't been used, start at 0
            	if (frame == null) {
            		frame = new graphPlot(sequences.get(0), chunk, nucleo, fast.titles.get(0));
            		frame.setVisible(true);
            	// Else check that current - 1 is valid and show graph
            	} else if ((frame != null) && (current_graph - 1 >= 0)) {
            		// Delete old graph
            		frame.dispose();
            		current_graph--;
            		frame = new graphPlot(sequences.get(current_graph), chunk, nucleo, fast.titles.get(current_graph));
            		frame.setVisible(true);
            	}
            }
		});
		panel_buttons.add(backward);
		
		// Same as above
		JButton foward = new JButton (">");

		foward.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	if (frame == null) {
            		frame = new graphPlot(sequences.get(current_graph), chunk, nucleo, fast.titles.get(0));
            		frame.setVisible(true);
            	} else if ((frame != null) && (current_graph + 1 <= sequences.size())) {
            		frame.dispose();
            		current_graph++;
            		frame = new graphPlot(sequences.get(current_graph), chunk, nucleo, fast.titles.get(current_graph));
            		frame.setVisible(true);
            	}
            }
		});
		
		panel_buttons.add(foward);
		panel.add(panel_buttons);
		

		setTitle("Base-distribution - V1.0");
		// Size is set larger to accommodate OSX button sizes
		setSize(400, 300);
		// Centres - CHANGE TO RIGHT ALIGNED 
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				gFinder ex = new gFinder();
				ex.setVisible(true);
			}
		});
	}

}
