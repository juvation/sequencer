
// PACKAGE

package net.redfish.sequencer;

// IMPORTS

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

// CLASS

public class VaryWindow
	extends JFrame
	implements ActionListener
{
	// CONSTRUCTOR
	
	public 
	VaryWindow()
	{
		// CONFIGURATION
		
		setSize(600, 400);
		setResizable(false);

		// TITLE
		
		setTitle("Vary Sequence");

		// LAYOUT

		getContentPane().setLayout(new BorderLayout());

		// CONTENTS
		
		JPanel	varyPanel = new JPanel();
		varyPanel.setLayout(new BoxLayout(varyPanel, BoxLayout.Y_AXIS));
		getContentPane().add(varyPanel, BorderLayout.CENTER);

		// probability

		{
			this.varyProbabilityCheckBox = new JCheckBox("Vary Probability");
			varyPanel.add(this.varyProbabilityCheckBox);

			JPanel	varyProbabilityPanel = new JPanel();
			varyProbabilityPanel.setLayout(new BoxLayout(varyProbabilityPanel, BoxLayout.X_AXIS));
			varyPanel.add(varyProbabilityPanel);
	
			JLabel	varyProbabilityLabel = new JLabel("Variation:");
			varyProbabilityPanel.add(varyProbabilityLabel);
			this.varyProbabilitySlider = new JSlider(JSlider.HORIZONTAL);
			this.varyProbabilitySlider.setMinimum(0);
			this.varyProbabilitySlider.setMaximum(100);
			this.varyProbabilitySlider.setValue(10);
			varyProbabilityLabel.setLabelFor(this.varyProbabilitySlider);
			varyProbabilityPanel.add(this.varyProbabilitySlider);
		}

		// notes

		{
			this.varyNoteLowerCheckBox = new JCheckBox("Vary Note Lower");
			varyPanel.add(this.varyNoteLowerCheckBox);
	
			this.varyNoteUpperCheckBox = new JCheckBox("Vary Note Upper");
			varyPanel.add(this.varyNoteUpperCheckBox);
	
			this.varyNoteKeepSameCheckBox = new JCheckBox("Keep Notes Equal");
			varyPanel.add(this.varyNoteKeepSameCheckBox);
	
			JPanel	varyNoteRangePanel = new JPanel();
			varyNoteRangePanel.setLayout(new BoxLayout(varyNoteRangePanel, BoxLayout.X_AXIS));
			varyPanel.add(varyNoteRangePanel);
	
			JLabel	varyNoteRangeLabel = new JLabel("Variation:");
			varyNoteRangePanel.add(varyNoteRangeLabel);
			this.varyNoteRangeSlider = new JSlider(JSlider.HORIZONTAL);
			this.varyNoteRangeSlider.setMinimum(0);
			this.varyNoteRangeSlider.setMaximum(127);
			this.varyNoteRangeSlider.setValue(10);
			varyNoteRangeLabel.setLabelFor(this.varyNoteRangeSlider);
			varyNoteRangePanel.add(this.varyNoteRangeSlider);
		}

    // velocity

		{
			this.varyVelocityLowerCheckBox = new JCheckBox("Vary Velocity Lower");
			varyPanel.add(this.varyVelocityLowerCheckBox);
	
			this.varyVelocityUpperCheckBox = new JCheckBox("Vary Velocity Upper");
			varyPanel.add(this.varyVelocityUpperCheckBox);
	
			this.varyVelocityKeepSameCheckBox = new JCheckBox("Keep Velocities Equal");
			varyPanel.add(this.varyVelocityKeepSameCheckBox);
	
			JPanel	varyVelocityRangePanel = new JPanel();
			varyVelocityRangePanel.setLayout(new BoxLayout(varyVelocityRangePanel, BoxLayout.X_AXIS));
			varyPanel.add(varyVelocityRangePanel);
	
			JLabel	varyVelocityRangeLabel = new JLabel("Variation:");
			varyVelocityRangePanel.add(varyVelocityRangeLabel);
			this.varyVelocityRangeSlider = new JSlider(JSlider.HORIZONTAL);
			this.varyVelocityRangeSlider.setMinimum(0);
			this.varyVelocityRangeSlider.setMaximum(127);
			this.varyNoteRangeSlider.setValue(20);
			varyVelocityRangeLabel.setLabelFor(this.varyVelocityRangeSlider);
			varyVelocityRangePanel.add(this.varyVelocityRangeSlider);
		}

		// gate time

		{
			this.varyGateTimeLowerCheckBox = new JCheckBox("Vary Gate Time Lower");
			varyPanel.add(this.varyGateTimeLowerCheckBox);
	
			this.varyGateTimeUpperCheckBox = new JCheckBox("Vary Gate Time Upper");
			varyPanel.add(this.varyGateTimeUpperCheckBox);
	
			this.varyGateTimeKeepSameCheckBox = new JCheckBox("Keep Gate Times Equal");
			varyPanel.add(this.varyGateTimeKeepSameCheckBox);
	
			JPanel	varyGateTimeRangePanel = new JPanel();
			varyGateTimeRangePanel.setLayout(new BoxLayout(varyGateTimeRangePanel, BoxLayout.X_AXIS));
			varyPanel.add(varyGateTimeRangePanel);
	
			JLabel	varyGateTimeRangeLabel = new JLabel("Variation:");
			varyGateTimeRangePanel.add(varyGateTimeRangeLabel);
			this.varyGateTimeRangeSlider = new JSlider(JSlider.HORIZONTAL);
			this.varyGateTimeRangeSlider.setMinimum(0);
			this.varyGateTimeRangeSlider.setMaximum(127);
			this.varyNoteRangeSlider.setValue(20);
			varyGateTimeRangeLabel.setLabelFor(this.varyGateTimeRangeSlider);
			varyGateTimeRangePanel.add(this.varyGateTimeRangeSlider);
		}

		// mute probability

		{
			this.varyMuteProbabilityCheckBox = new JCheckBox("Vary Mute Probability");
			varyPanel.add(this.varyMuteProbabilityCheckBox);

			JPanel	varyMuteProbabilityPanel = new JPanel();
			varyMuteProbabilityPanel.setLayout(new BoxLayout(varyMuteProbabilityPanel, BoxLayout.X_AXIS));
			varyPanel.add(varyMuteProbabilityPanel);
	
			JLabel	varyMuteProbabilityLabel = new JLabel("Variation:");
			varyMuteProbabilityPanel.add(varyMuteProbabilityLabel);
			this.varyMuteProbabilitySlider = new JSlider(JSlider.HORIZONTAL);
			this.varyMuteProbabilitySlider.setMinimum(0);
			this.varyMuteProbabilitySlider.setMaximum(100);
			this.varyMuteProbabilitySlider.setValue(50);
			varyMuteProbabilityLabel.setLabelFor(this.varyMuteProbabilitySlider);
			varyMuteProbabilityPanel.add(this.varyMuteProbabilitySlider);
		}

		// tie probability

		{
			this.varyTieProbabilityCheckBox = new JCheckBox("Vary Tie Probability");
			varyPanel.add(this.varyTieProbabilityCheckBox);

			JPanel	varyTieProbabilityPanel = new JPanel();
			varyTieProbabilityPanel.setLayout(new BoxLayout(varyTieProbabilityPanel, BoxLayout.X_AXIS));
			varyPanel.add(varyTieProbabilityPanel);
	
			JLabel	varyTieProbabilityLabel = new JLabel("Variation:");
			varyTieProbabilityPanel.add(varyTieProbabilityLabel);
			this.varyTieProbabilitySlider = new JSlider(JSlider.HORIZONTAL);
			this.varyTieProbabilitySlider.setMinimum(0);
			this.varyTieProbabilitySlider.setMaximum(100);
			this.varyTieProbabilitySlider.setValue(50);
			varyTieProbabilityLabel.setLabelFor(this.varyTieProbabilitySlider);
			varyTieProbabilityPanel.add(this.varyTieProbabilitySlider);
		}

		// ratchet probability

		{
			this.varyRatchetProbabilityCheckBox = new JCheckBox("Vary Ratchet Probability");
			varyPanel.add(this.varyRatchetProbabilityCheckBox);

			JPanel	varyRatchetProbabilityPanel = new JPanel();
			varyRatchetProbabilityPanel.setLayout(new BoxLayout(varyRatchetProbabilityPanel, BoxLayout.X_AXIS));
			varyPanel.add(varyRatchetProbabilityPanel);
	
			JLabel	varyRatchetProbabilityLabel = new JLabel("Variation:");
			varyRatchetProbabilityPanel.add(varyRatchetProbabilityLabel);
			this.varyRatchetProbabilitySlider = new JSlider(JSlider.HORIZONTAL);
			this.varyRatchetProbabilitySlider.setMinimum(0);
			this.varyRatchetProbabilitySlider.setMaximum(100);
			this.varyRatchetProbabilitySlider.setValue(50);
			varyRatchetProbabilityLabel.setLabelFor(this.varyRatchetProbabilitySlider);
			varyRatchetProbabilityPanel.add(this.varyRatchetProbabilitySlider);
		}

		// toolbar

		JPanel	buttonBar = new JPanel();
		buttonBar.setLayout(new FlowLayout(FlowLayout.CENTER));
		getContentPane().add(buttonBar, BorderLayout.SOUTH);
		
		JButton	varyOptionButton = new JButton("Vary Selected Option");
		varyOptionButton.addActionListener(this);
		varyOptionButton.setActionCommand("VARY_SELECTED_OPTION");
		buttonBar.add(varyOptionButton);

		JButton	varySequenceButton = new JButton("Vary Sequence");
		varySequenceButton.addActionListener(this);
		varySequenceButton.setActionCommand("VARY_SEQUENCE");
		buttonBar.add(varySequenceButton);
		
		pack();
	}
	
	// JFRAME OVERRIDES
	
	public JRootPane
	createRootPane()
	{
		JRootPane rootPane = new JRootPane();
		
		int menuShortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		String	osName = System.getProperty("os.name");

		KeyStroke keyStroke = null;
		
		if (osName.startsWith("Windows"))
		{
			keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F4, menuShortcutKey);
		}
		else
		if (osName.startsWith("Mac"))
		{
			keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, menuShortcutKey);
		}
		else
		{
			// no shortcuts for other OSes(yet)
		}
		
		if (keyStroke != null)
		{
			rootPane.registerKeyboardAction(this, "KEYBOARD_CLOSE_ACTION",
				keyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		}
		
		return rootPane;
	}
    
	// ACTIONLISTENER IMPLEMENTATION
	
	public void
	actionPerformed(ActionEvent inEvent)
	{
		String	actionCommand = inEvent.getActionCommand();
		
		if (actionCommand.equalsIgnoreCase("KEYBOARD_CLOSE_ACTION"))
		{
			setVisible(false);
		}
		else
		if (actionCommand.equalsIgnoreCase("VARY_SELECTED_OPTION"))
		{
			Option	option = SequencerWindow.getInstance().getSelectedOption();
	
			if (option != null)
			{
				if (varyOption(option))
				{
					SequencerWindow.getInstance().setSelectedOption(option);
				}
			}
		}
		else
		if (actionCommand.equalsIgnoreCase("VARY_SEQUENCE"))
		{
			varySequence();
		}
		else
		{
			System.err.println("HEY! Unknown action command: " + actionCommand);
		}
	}
	
	// PUBLIC METHODS
	
	// PRIVATE METHODS

	private boolean
	varyGateTimes(Option inOption)
	{
		boolean	varied = false;

		int	variation = this.varyGateTimeRangeSlider.getValue();

		if (variation > 0)
		{
			int	gateTimeLower = inOption.gateTimeLower;
			int	gateTimeUpper = inOption.gateTimeUpper;
	
			if (this.varyGateTimeKeepSameCheckBox.isSelected())
			{
				int	lowerBound = (int) Math.max(gateTimeLower - (variation / 2), 0);
				int	upperBound = (int) Math.min(gateTimeLower + (variation / 2), 100);
	
				int	gateTime = SequencerWindow.getInstance().getRandomIntInRange(lowerBound, upperBound);
	
				inOption.gateTimeLower = gateTime;
				inOption.gateTimeUpper = gateTime;
	
				varied = true;
			}
			else
			{
				if (this.varyGateTimeLowerCheckBox.isSelected())
				{
					int	lowerBound = (int) Math.max(gateTimeLower - (variation / 2), 0);
					int	upperBound = (int) Math.min(gateTimeLower + (variation / 2), 100);
	
					int	gateTime = SequencerWindow.getInstance().getRandomIntInRange(lowerBound, upperBound);
	
					inOption.gateTimeLower = gateTime;
	
					varied = true;
				}
	
				if (this.varyGateTimeUpperCheckBox.isSelected())
				{
					int	lowerBound = (int) Math.max(gateTimeUpper - (variation / 2), 0);
					int	upperBound = (int) Math.min(gateTimeUpper + (variation / 2), 100);
	
					int	gateTime = SequencerWindow.getInstance().getRandomIntInRange(lowerBound, upperBound);
	
					inOption.gateTimeUpper = gateTime;

					varied = true;
				}
			}
		}

		return varied;
	}

	private boolean
	varyMuteProbability(Option inOption)
	{
		boolean	varied = false;

		if (this.varyMuteProbabilityCheckBox.isSelected())
		{
			int	variation = this.varyMuteProbabilitySlider.getValue();

			if (variation > 0)
			{
				int	lowerBound = (int) Math.max(inOption.muteProbability - (variation / 2), 0);
				int	upperBound = (int) Math.min(inOption.muteProbability + (variation / 2), 100);

				System.err.println("lower bound = " + lowerBound + ", upper bound = " + upperBound);
	
				inOption.muteProbability = SequencerWindow.getInstance().getRandomIntInRange(lowerBound, upperBound);
	
				varied = true;
			}
		}

		return varied;
	}

	private boolean
	varyNotes(Option inOption)
	{
		boolean	varied = false;

		int	variation = this.varyNoteRangeSlider.getValue();

		if (variation > 0)
		{
			int	noteLower = inOption.noteLower;
			int	noteUpper = inOption.noteUpper;
	
			if (this.varyNoteKeepSameCheckBox.isSelected())
			{
				int	lowerBound = (int) Math.max(noteLower - (variation / 2), 0);
				int	upperBound = (int) Math.min(noteLower + (variation / 2), 127);
	
				int	note = SequencerWindow.getInstance().getRandomIntInRange(lowerBound, upperBound);
	
				inOption.noteLower = note;
				inOption.noteUpper = note;
	
				varied = true;
			}
			else
			{
				if (this.varyNoteLowerCheckBox.isSelected())
				{
					int	lowerBound = (int) Math.max(noteLower - (variation / 2), 0);
					int	upperBound = (int) Math.min(noteLower + (variation / 2), 127);
	
					int	note = SequencerWindow.getInstance().getRandomIntInRange(lowerBound, upperBound);
	
					inOption.noteLower = note;
	
					varied = true;
				}
	
				if (this.varyNoteUpperCheckBox.isSelected())
				{
					int	lowerBound = (int) Math.max(noteUpper - (variation / 2), 0);
					int	upperBound = (int) Math.min(noteUpper + (variation / 2), 127);
	
					int	note = SequencerWindow.getInstance().getRandomIntInRange(lowerBound, upperBound);
	
					inOption.noteUpper = note;

					varied = true;
				}
			}
		}

		return varied;
	}

	private boolean
	varyProbability(Option inOption)
	{
		boolean	varied = false;

		if (this.varyProbabilityCheckBox.isSelected())
		{
			int	variation = this.varyProbabilitySlider.getValue();

			if (variation > 0)
			{
				int	lowerBound = (int) Math.max(inOption.probability - (variation / 2), 0);
				int	upperBound = (int) Math.min(inOption.probability + (variation / 2), 100);

				System.err.println("lower bound = " + lowerBound + ", upper bound = " + upperBound);
	
				inOption.probability = SequencerWindow.getInstance().getRandomIntInRange(lowerBound, upperBound);
	
				varied = true;
			}
		}

		return varied;
	}

	private boolean
	varyRatchetProbability(Option inOption)
	{
		boolean	varied = false;

		if (this.varyRatchetProbabilityCheckBox.isSelected())
		{
			int	variation = this.varyRatchetProbabilitySlider.getValue();

			if (variation > 0)
			{
				int	lowerBound = (int) Math.max(inOption.ratchetProbability - (variation / 2), 0);
				int	upperBound = (int) Math.min(inOption.ratchetProbability + (variation / 2), 100);

				System.err.println("lower bound = " + lowerBound + ", upper bound = " + upperBound);
	
				inOption.ratchetProbability = SequencerWindow.getInstance().getRandomIntInRange(lowerBound, upperBound);
	
				varied = true;
			}
		}

		return varied;
	}

	private boolean
	varyTieProbability(Option inOption)
	{
		boolean	varied = false;

		if (this.varyTieProbabilityCheckBox.isSelected())
		{
			int	variation = this.varyTieProbabilitySlider.getValue();

			if (variation > 0)
			{
				int	lowerBound = (int) Math.max(inOption.tieProbability - (variation / 2), 0);
				int	upperBound = (int) Math.min(inOption.tieProbability + (variation / 2), 100);

				System.err.println("lower bound = " + lowerBound + ", upper bound = " + upperBound);
	
				inOption.tieProbability = SequencerWindow.getInstance().getRandomIntInRange(lowerBound, upperBound);
	
				varied = true;
			}
		}

		return varied;
	}

	private boolean
	varyVelocities(Option inOption)
	{
		boolean	varied = false;

		int	variation = this.varyVelocityRangeSlider.getValue();

		if (variation > 0)
		{
			int	velocityLower = inOption.velocityLower;
			int	velocityUpper = inOption.velocityUpper;
	
			if (this.varyVelocityKeepSameCheckBox.isSelected())
			{
				int	lowerBound = (int) Math.max(velocityLower - (variation / 2), 0);
				int	upperBound = (int) Math.min(velocityLower + (variation / 2), 127);

				System.err.println("lower bound = " + lowerBound + ", upper bound = " + upperBound);
	
				int	velocity = SequencerWindow.getInstance().getRandomIntInRange(lowerBound, upperBound);
	
				inOption.velocityLower = velocity;
				inOption.velocityUpper = velocity;
	
				varied = true;
			}
			else
			{
				if (this.varyVelocityLowerCheckBox.isSelected())
				{
					int	lowerBound = (int) Math.max(velocityLower - (variation / 2), 0);
					int	upperBound = (int) Math.min(velocityLower + (variation / 2), 127);
	
					int	velocity = SequencerWindow.getInstance().getRandomIntInRange(lowerBound, upperBound);
	
					inOption.velocityLower = velocity;
	
					varied = true;
				}
	
				if (this.varyVelocityUpperCheckBox.isSelected())
				{
					int	lowerBound = (int) Math.max(velocityUpper - (variation / 2), 0);
					int	upperBound = (int) Math.min(velocityUpper + (variation / 2), 127);
	
					int	velocity = SequencerWindow.getInstance().getRandomIntInRange(lowerBound, upperBound);
	
					inOption.velocityUpper = velocity;

					varied = true;
				}
			}
		}

		return varied;
	}

	private boolean
	varyOption(Option inOption)
	{
		boolean	varied = false;

		// probability

		if (varyProbability(inOption))
		{
			varied = true;
		}

		// notes

		if (varyNotes(inOption))
		{
			varied = true;
		}

		// velocities

		if (varyVelocities(inOption))
		{
			varied = true;
		}

		// gate times

		if (varyGateTimes(inOption))
		{
			varied = true;
		}

		// probability

		if (varyMuteProbability(inOption))
		{
			varied = true;
		}

		// probability

		if (varyTieProbability(inOption))
		{
			varied = true;
		}

		// probability

		if (varyRatchetProbability(inOption))
		{
			varied = true;
		}

		// put further variations here

		return varied;
	}

	private void
	varySequence()
	{
		for (int stepNumber = 0; stepNumber < 16; stepNumber++)
		{
			for (int optionNumber = 0; optionNumber < 2; optionNumber++)
			{
				Option	option = SequencerWindow.getInstance().getOption(stepNumber, optionNumber);

				if (varyOption(option))
				{
					SequencerWindow.getInstance().setOption(option, stepNumber, optionNumber);
				}
			}
		}
	}

	// PRIVATE DATA

	private JCheckBox
	varyGateTimeKeepSameCheckBox;

	private JCheckBox
	varyGateTimeLowerCheckBox;

	private JCheckBox
	varyGateTimeUpperCheckBox;

	private JSlider
	varyGateTimeRangeSlider;

	private JCheckBox
	varyMuteProbabilityCheckBox;

	private JSlider
	varyMuteProbabilitySlider;

	private JCheckBox
	varyNoteKeepSameCheckBox;

	private JCheckBox
	varyNoteLowerCheckBox;

	private JCheckBox
	varyNoteUpperCheckBox;

	private JSlider
	varyNoteRangeSlider;

	private JCheckBox
	varyProbabilityCheckBox;

	private JSlider
	varyProbabilitySlider;

	private JCheckBox
	varyRatchetProbabilityCheckBox;

	private JSlider
	varyRatchetProbabilitySlider;

	private JCheckBox
	varyTieProbabilityCheckBox;

	private JSlider
	varyTieProbabilitySlider;

	private JCheckBox
	varyVelocityKeepSameCheckBox;

	private JCheckBox
	varyVelocityLowerCheckBox;

	private JCheckBox
	varyVelocityUpperCheckBox;

	private JSlider
	varyVelocityRangeSlider;


}

