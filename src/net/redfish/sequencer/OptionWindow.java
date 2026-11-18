// OptionWindow.java

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// CLASS

public class OptionWindow
	extends JFrame
	implements ActionListener
{
	// PUBLIC CONSTRUCTOR
	
	public
	OptionWindow(int inStepNumber, int inOptionNumber)
	{
		this.stepNumber = inStepNumber;
		this.optionNumber = inOptionNumber;

		this.option = SequencerWindow.getInstance().getOption(this.stepNumber, this.optionNumber);

		// CONFIGURATION
		
		setTitle("Step " + this.stepNumber + " Option " + this.optionNumber);
		setResizable(false);
		
		// LAYOUT
		
		getContentPane().setLayout(new BorderLayout());
		
		// KEYBOARD VIEW

		JPanel	keyboardPane = new JPanel();
		keyboardPane.setLayout(new BoxLayout(keyboardPane, BoxLayout.Y_AXIS));
		getContentPane().add(keyboardPane, BorderLayout.CENTER);

		// upper keyboard view

		// true = upper
		this.upperKeyboardView = new KeyboardView(true, this.stepNumber, this.optionNumber);
		keyboardPane.add(this.upperKeyboardView);

		// upper octave panel

		{
			JPanel	upperOctavePanel = new JPanel();
			upperOctavePanel.setLayout(new BoxLayout(upperOctavePanel, BoxLayout.X_AXIS));
			keyboardPane.add(upperOctavePanel);
			
			JButton	upperOctaveDownButton = new JButton("Octave Down");
			upperOctaveDownButton.setActionCommand("UPPER_OCTAVE_DOWN");
			upperOctaveDownButton.addActionListener(this);
			upperOctavePanel.add(upperOctaveDownButton);
			
			this.upperOctaveLabel = new JLabel("0");
			upperOctavePanel.add(this.upperOctaveLabel);
			
			JButton	upperOctaveUpButton = new JButton("Octave Up");
			upperOctaveUpButton.setActionCommand("UPPER_OCTAVE_UP");
			upperOctaveUpButton.addActionListener(this);
			upperOctavePanel.add(upperOctaveUpButton);
	
			keyboardPane.add(upperOctavePanel);
		}

		// lower keyboard view
		
		// false = lower
		this.lowerKeyboardView = new KeyboardView(false, this.stepNumber, this.optionNumber);
		keyboardPane.add(this.lowerKeyboardView);

		// lower octave panel
		{
			JPanel	lowerOctavePanel = new JPanel();
			lowerOctavePanel.setLayout(new BoxLayout(lowerOctavePanel, BoxLayout.X_AXIS));
			keyboardPane.add(lowerOctavePanel);
			
			JButton	lowerOctaveDownButton = new JButton("Octave Down");
			lowerOctaveDownButton.setActionCommand("LOWER_OCTAVE_DOWN");
			lowerOctaveDownButton.addActionListener(this);
			lowerOctavePanel.add(lowerOctaveDownButton);
			
			this.lowerOctaveLabel = new JLabel("0");
			lowerOctavePanel.add(this.lowerOctaveLabel);
			
			JButton	lowerOctaveUpButton = new JButton("Octave Up");
			lowerOctaveUpButton.setActionCommand("OCTAVE_UP");
			lowerOctaveUpButton.addActionListener(this);
			lowerOctavePanel.add(lowerOctaveUpButton);
	
			keyboardPane.add(lowerOctavePanel);
		}
		
		// PACK
		
		pack();
	}

	// COMPONENT OVERRIDE
	
	@Override
	public void
	setVisible(boolean inVisible)
	{
		if (!inVisible)
		{
			SequencerWindow.getInstance().removeOptionWindow(this.option);
		}

		super.setVisible(inVisible);
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
			
			dispose();
		}
		else
		if (actionCommand.equalsIgnoreCase("LOWER_OCTAVE_DOWN"))
		{
			int	octaveOffset = this.lowerKeyboardView.getOctaveOffset();
			
			octaveOffset--;
			
			if (this.lowerKeyboardView.setOctaveOffset(octaveOffset))
			{
				this.lowerOctaveLabel.setText("" + octaveOffset);
			}
		}
		else
		if (actionCommand.equalsIgnoreCase("LOWER_OCTAVE_UP"))
		{
			int	octaveOffset = this.lowerKeyboardView.getOctaveOffset();
			
			octaveOffset++;
			
			if (this.lowerKeyboardView.setOctaveOffset(octaveOffset))
			{
				this.lowerOctaveLabel.setText("" + octaveOffset);
			}
		}
		else
		if (actionCommand.equalsIgnoreCase("UPPER_OCTAVE_DOWN"))
		{
			int	octaveOffset = this.upperKeyboardView.getOctaveOffset();
			
			octaveOffset--;
			
			if (this.upperKeyboardView.setOctaveOffset(octaveOffset))
			{
				this.upperOctaveLabel.setText("" + octaveOffset);
			}
		}
		else
		if (actionCommand.equalsIgnoreCase("UPPER_OCTAVE_UP"))
		{
			int	octaveOffset = this.upperKeyboardView.getOctaveOffset();
			
			octaveOffset++;
			
			if (this.upperKeyboardView.setOctaveOffset(octaveOffset))
			{
				this.upperOctaveLabel.setText("" + octaveOffset);
			}
		}
	}
	
	// PUBLIC METHODS
	
	// PRIVATE DATA
	
	private int
	stepNumber;
	
	private int
	optionNumber;
	
	private JLabel
	lowerOctaveLabel = null;
	
	private JLabel
	upperOctaveLabel = null;

	private KeyboardView
	lowerKeyboardView = null;

	private KeyboardView
	upperKeyboardView = null;
	
	private Option
	option;

}

