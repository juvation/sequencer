// OptionCellRenderer.java

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.SwingConstants;

public class OptionCellRenderer
	extends JComponent
	implements TableCellRenderer
{
	public
	OptionCellRenderer()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);

		// have to set bounds to get horiz alignment to work
		Rectangle	bounds = getBounds();

		bounds.width = 100;
		bounds.height = 18;

		Font	largeFont = new Font("Sans Serif", Font.BOLD, 18);
		Font	smallFont = new Font("Sans Serif", Font.BOLD, 12);
		
		this.probabilityLabel = new JLabel();
		this.probabilityLabel.setFont(largeFont);
		this.probabilityLabel.setBounds(bounds);
		this.probabilityLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(this.probabilityLabel);

		this.noteLabel = new JLabel();
		this.noteLabel.setFont(largeFont);
		add(this.noteLabel);
		
		this.velocityLabel = new JLabel();
		this.velocityLabel.setFont(smallFont);
		add(this.velocityLabel);

		this.gateTimeLabel = new JLabel();
		this.gateTimeLabel.setFont(smallFont);
		add(this.gateTimeLabel);

		this.muteProbabilityLabel = new JLabel();
		this.muteProbabilityLabel.setFont(smallFont);
		add(this.muteProbabilityLabel);

		this.tieProbabilityLabel = new JLabel();
		this.tieProbabilityLabel.setFont(smallFont);
		add(this.tieProbabilityLabel);

		this.ratchetProbabilityLabel = new JLabel();
		this.ratchetProbabilityLabel.setFont(smallFont);
		add(this.ratchetProbabilityLabel);

		// inverted selections plain don't work
		// so resort to borders :-|
		this.border = BorderFactory.createLineBorder(Color.black, 1);
	}
	
	// COMPONENT OVERRIDE
	
	// in order to get the right size in BankWindow
	// delegating to Component gets it wrong
	// could just hardcode to 100, but it might be wrong on Windows etc
	public Dimension
	getPreferredSize()
	{
		Dimension	size = super.getPreferredSize();

		// leave the height alone
		
		size.setSize(100, size.height);
		
		return size;
	}
	
	// TABLECELLRENDERER IMPLEMENTATION
	
	public Component
	getTableCellRendererComponent(JTable inTable, Object inValue,
		boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn)
	{
		Option	option =(Option) inValue;

		try
		{
// System.err.println("cell row=" + inRow + " col=" + inColumn);
// System.err.println("selected=" + inIsSelected + " hasfocus=" + inHasFocus);

			if (inIsSelected)
			{
				setBorder(this.border);

				setBackground(Color.GREEN);
			}
			else
			{
				setBorder(null);

				setBackground(Color.WHITE);
			}

			this.probabilityLabel.setText
				("" + option.probability + "%");
			
			this.noteLabel.setText
				(SequencerWindow.getNoteString(option.noteLower) + "-" + SequencerWindow.getNoteString(option.noteUpper));

			this.velocityLabel.setText
				("V: " + option.velocityLower + "-" + option.velocityUpper);

			this.gateTimeLabel.setText
				("G: " + option.gateTimeLower + "-" + option.gateTimeUpper);

			this.muteProbabilityLabel.setText
				("Mute: " + option.muteProbability + "%");

			this.tieProbabilityLabel.setText
				("Tie: " + option.tieProbability + "%");

			this.ratchetProbabilityLabel.setText
				("Ratchet: " + option.ratchetProbability + "%");
		}
		catch(Exception inException)
		{
			System.err.println(inException);
		}
		
		return this;
	}

	// remember to override setValue() here if subclassing DefaultTableCellRenderer

	// PRIVATE DATA
	
	private Border
	border = null;
	
	private JLabel
	gateTimeLabel = null;

	private JLabel
	muteProbabilityLabel = null;
	
	private JLabel
	noteLabel = null;

	private JLabel
	probabilityLabel = null;

	private JLabel
	ratchetProbabilityLabel = null;

	private JLabel
	tieProbabilityLabel = null;

	private JLabel
	velocityLabel = null;

}
