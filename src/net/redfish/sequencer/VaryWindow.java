
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
import javax.swing.JTextField;
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

		// notes

		this.varyNoteLowerCheckBox = new JCheckBox("Vary Note Lower");
		varyPanel.add(this.varyNoteLowerCheckBox);

		this.varyNoteUpperCheckBox = new JCheckBox("Vary Note Upper");
		varyPanel.add(this.varyNoteUpperCheckBox);

		this.varyNoteKeepSameCheckBox = new JCheckBox("Keep Notes Equal");
		varyPanel.add(this.varyNoteKeepSameCheckBox);

		JPanel	varyNoteRangePanel = new JPanel();
		varyNoteRangePanel.setLayout(new BoxLayout(varyNoteRangePanel, BoxLayout.X_AXIS));
		varyPanel.add(varyNoteRangePanel);

		JLabel	varyNoteRangeLabel = new JLabel("Vary Note Range:");
		varyNoteRangePanel.add(varyNoteRangeLabel);
		this.varyNoteRangeTextField = new JTextField(10);
		this.varyNoteRangeTextField.setText("5");
		varyNoteRangeLabel.setLabelFor(this.varyNoteRangeTextField);
		varyNoteRangePanel.add(this.varyNoteRangeTextField);

		// constrain the note range to 0-127
		AbstractDocument	varyNoteRangeDocument = (AbstractDocument) this.varyNoteRangeTextField.getDocument();
		varyNoteRangeDocument.setDocumentFilter(new NumbersOnlyDocumentFilter(this.varyNoteRangeTextField, 0, 127));

    // gateTime

		this.varyVelocityLowerCheckBox = new JCheckBox("Vary Velocity Lower");
		varyPanel.add(this.varyVelocityLowerCheckBox);

		this.varyVelocityUpperCheckBox = new JCheckBox("Vary Velocity Upper");
		varyPanel.add(this.varyVelocityUpperCheckBox);

		this.varyVelocityKeepSameCheckBox = new JCheckBox("Keep Velocities Equal");
		varyPanel.add(this.varyVelocityKeepSameCheckBox);

		JPanel	varyVelocityRangePanel = new JPanel();
		varyVelocityRangePanel.setLayout(new BoxLayout(varyVelocityRangePanel, BoxLayout.X_AXIS));
		varyPanel.add(varyVelocityRangePanel);

		JLabel	varyVelocityRangeLabel = new JLabel("Vary Velocity Range:");
		varyVelocityRangePanel.add(varyVelocityRangeLabel);
		this.varyVelocityRangeTextField = new JTextField(10);
		this.varyVelocityRangeTextField.setText("20");
		varyVelocityRangeLabel.setLabelFor(this.varyVelocityRangeTextField);
		varyVelocityRangePanel.add(this.varyVelocityRangeTextField);

		// constrain the note range to 0-127
		AbstractDocument	varyVelocityRangeDocument = (AbstractDocument) this.varyVelocityRangeTextField.getDocument();
		varyVelocityRangeDocument.setDocumentFilter(new NumbersOnlyDocumentFilter(this.varyVelocityRangeTextField, 0, 127));

		// gate time

		this.varyGateTimeLowerCheckBox = new JCheckBox("Vary Gate Time Lower");
		varyPanel.add(this.varyGateTimeLowerCheckBox);

		this.varyGateTimeUpperCheckBox = new JCheckBox("Vary Gate Time Upper");
		varyPanel.add(this.varyGateTimeUpperCheckBox);

		this.varyGateTimeKeepSameCheckBox = new JCheckBox("Keep Gate Times Equal");
		varyPanel.add(this.varyGateTimeKeepSameCheckBox);

		JPanel	varyGateTimeRangePanel = new JPanel();
		varyGateTimeRangePanel.setLayout(new BoxLayout(varyGateTimeRangePanel, BoxLayout.X_AXIS));
		varyPanel.add(varyGateTimeRangePanel);

		JLabel	varyGateTimeRangeLabel = new JLabel("Vary Gate Time Range:");
		varyGateTimeRangePanel.add(varyGateTimeRangeLabel);
		this.varyGateTimeRangeTextField = new JTextField(10);
		this.varyGateTimeRangeTextField.setText("20");
		varyGateTimeRangeLabel.setLabelFor(this.varyGateTimeRangeTextField);
		varyGateTimeRangePanel.add(this.varyGateTimeRangeTextField);

		// constrain the note range to 0-127
		AbstractDocument	varyGateTimeRangeDocument = (AbstractDocument) this.varyGateTimeRangeTextField.getDocument();
		varyGateTimeRangeDocument.setDocumentFilter(new NumbersOnlyDocumentFilter(this.varyGateTimeRangeTextField, 0, 127));

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
			varySelectedOption();
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

		int	variation = Integer.parseInt(this.varyGateTimeRangeTextField.getText());

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
	varyNotes(Option inOption)
	{
		boolean	varied = false;

		int	variation = Integer.parseInt(this.varyNoteRangeTextField.getText());

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
	varyVelocities(Option inOption)
	{
		boolean	varied = false;

		int	variation = Integer.parseInt(this.varyVelocityRangeTextField.getText());

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

	private void
	varySelectedOption()
	{
		// notes

		Option	option = SequencerWindow.getInstance().getSelectedOption();

		if (option == null)
		{
			return;
		}

		boolean	varied = false;

		// notes

		if (varyNotes(option))
		{
			varied = true;
		}

		// velocities

		if (varyVelocities(option))
		{
			varied = true;
		}

		// gate times

		if (varyGateTimes(option))
		{
			varied = true;
		}

		// put further variations here

		SequencerWindow.getInstance().setOption(option);
	}

	private void
	varySequence()
	{
	}

	// PRIVATE DATA

	private JCheckBox
	varyGateTimeKeepSameCheckBox;

	private JCheckBox
	varyGateTimeLowerCheckBox;

	private JCheckBox
	varyGateTimeUpperCheckBox;

	private JTextField
	varyGateTimeRangeTextField;

	private JCheckBox
	varyNoteKeepSameCheckBox;

	private JCheckBox
	varyNoteLowerCheckBox;

	private JCheckBox
	varyNoteUpperCheckBox;

	private JTextField
	varyNoteRangeTextField;

	private JCheckBox
	varyVelocityKeepSameCheckBox;

	private JCheckBox
	varyVelocityLowerCheckBox;

	private JCheckBox
	varyVelocityUpperCheckBox;

	private JTextField
	varyVelocityRangeTextField;

	// INNER CLASSES
	
	// I mean, there has to be a better way...?
	// but the filter overrides are called for each digit, so...

	class NumbersOnlyDocumentFilter
	extends DocumentFilter
	{
		public
		NumbersOnlyDocumentFilter(JTextField inTextField, int inMinimum, int inMaximum)
		{
			this.textField = inTextField;
			this.minimum = inMinimum;
			this.maximum = inMaximum;
		}

		@Override
		public void
		insertString(DocumentFilter.FilterBypass inBypass, int inOffset, String inString, AttributeSet inAttributes) 
		throws BadLocationException
		{
			if (inString != null && inString.matches("\\d*"))
			{
				String	oldString = this.textField.getText();

				super.insertString(inBypass, inOffset, inString, inAttributes);

				String	newString = this.textField.getText();

				try
				{
					int	number = Integer.parseInt(newString);

					if (number < this.minimum || number > this.maximum)
					{
						this.textField.setText(oldString);
					}
				}
				catch (Exception inException)
				{
				}
			}
		}
		
		@Override
		public void
		replace(DocumentFilter.FilterBypass inBypass, int inOffset, int inLength, String inText, AttributeSet inAttributes) 
		throws BadLocationException
		{
			if (inText != null && inText.matches("\\d*"))
			{
				String	oldString = this.textField.getText();

				super.replace(inBypass, inOffset, inLength, inText, inAttributes);

				String	newString = this.textField.getText();

				try
				{
					int	number = Integer.parseInt(newString);

					if (number < this.minimum || number > this.maximum)
					{
						this.textField.setText(oldString);
					}
				}
				catch (Exception inException)
				{
				}
			}
		}

		private int
		maximum;

		private int
		minimum;

		private JTextField
		textField;

	}

}

