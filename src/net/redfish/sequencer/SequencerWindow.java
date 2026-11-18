// SequencerWindow.java

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.prefs.Preferences;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.UIManager;

// CLASS

public class SequencerWindow
	extends JFrame
	implements ActionListener, ChangeListener, ClipboardOwner, ListSelectionListener, MouseListener
{
	// MAINLINE

	public static void
	main (String[] inArgs)
		throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName ());
		
		// OK so the mystery is somewhat solved
		// userRoot() on Mac seems to return the prefs domain of the app package
		// and not the actual user root
		// so storing prefs actually stoes off com.prophetvs.editor/kPreferencesPath
		// but it would be dangerous to make cross-platform assumptions
		// so I am going to leave this as-is
		
		sPrefs = Preferences.userRoot ().node (kPreferencesPath);

		// OK, parsed OK, time to make the boss window
		sInstance = new SequencerWindow();
		sInstance.setLocationRelativeTo (null);
		sInstance.setVisible (true);
	}

	// SINGLETON ACCESS POINT
	
	public static SequencerWindow
	getInstance ()
	{
		// the mainline constructs us
		// therefore no need to check here
		return sInstance;
	}
	
	// PUBLIC STATIC METHODS

	public static File
	getFileForSave(Object inOwner, String inTitle, String inSelectedFileName)
	{
		File	selectedFile = null;
		
		String	osName = System.getProperty("os.name");

		if (osName.startsWith("Mac"))
		{
			FileDialog	dialog = null;
			
			if (inOwner instanceof Frame)
			{
				dialog = new FileDialog((Frame) inOwner, inTitle, FileDialog.SAVE);
			}
			else
			if (inOwner instanceof Dialog)
			{
				dialog = new FileDialog((Dialog) inOwner, inTitle, FileDialog.SAVE);
			}
			else
			{
				// HACK taking a chance here - just pass in a real owner!
				dialog = new FileDialog((Frame) null, inTitle, FileDialog.SAVE);
			}
			
			if (inSelectedFileName != null)
			{
				dialog.setFile(inSelectedFileName);
			}
			
			dialog.setVisible(true);
			
			String	file = dialog.getFile();
			
			if (file != null)
			{
				String	directory = dialog.getDirectory();
				
				if (directory != null)
				{
					String	path = null;
					
					if (directory.endsWith(File.separator))
					{
						path = directory + file;
					}
					else
					{
						path = directory + File.separatorChar + file;
					}
					
					selectedFile = new File(path);
				}
			}
		}
		else
		{
			JFileChooser	openDialog = new JFileChooser();

			openDialog.setDialogTitle(inTitle);
			// openDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
			openDialog.setMultiSelectionEnabled(false);
			openDialog.setDragEnabled(true);

			if (inSelectedFileName != null)
			{
				openDialog.setSelectedFile
					(new File(inSelectedFileName));
			}

			int	response = 0;
			
			if (inOwner instanceof Frame)
			{
				response = openDialog.showSaveDialog((Frame) inOwner);
			}
			else
			if (inOwner instanceof Dialog)
			{
				response = openDialog.showSaveDialog((Dialog) inOwner);
			}
			else
			{
				// HACK taking a chance here - just pass in a real owner!
				response = openDialog.showSaveDialog((Dialog) inOwner);
			}

			if (response == JFileChooser.APPROVE_OPTION)
			{
				selectedFile = openDialog.getSelectedFile();
			}
		}
		
		return selectedFile;
	}

	// ASSUME C4 = 60
	// TODO Roland vs Yamaha, LFS
	static public String
	getNoteString(int inNote)
	{
		int	note = inNote % 12;
		int	octave = (inNote / 12) - 1;

		return "" + kNoteStrings[note] + octave;
	}

	public static int
	showConfirmDialog(String inTitle, String inMessage)
	{
		return JOptionPane.showConfirmDialog
			(null, inMessage, inTitle, JOptionPane.YES_NO_OPTION);
	}
	
	public static void
	showErrorDialog(String inTitle, String inMessage)
	{
		JOptionPane.showMessageDialog
			(null, inMessage, inTitle, JOptionPane.ERROR_MESSAGE);
	}
	
	public static void
	showErrorDialog(String inTitle, Throwable inThrowable)
	{
		inThrowable.printStackTrace(System.err);
		
		showErrorDialog(inTitle, inThrowable.toString());
	}
	
	// PUBLIC CONSTRUCTOR
	
	public 
	SequencerWindow()
	{
		this.sequence = new Sequence(16, 2);

		// CONFIGURATION
		
		setTitle("AssQuencer");

		setResizable(true);

		// LAYOUT

		getContentPane().setLayout(new BorderLayout());

		// CONTENTS
		
		// NORTH PANE - BANK STATUS
		
		JPanel	statusPanel = new JPanel();
		statusPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		getContentPane().add(statusPanel, BorderLayout.NORTH);
		
		this.statusLabel = new JLabel();
		statusPanel.add(this.statusLabel);
		
		// MAIN PANE - SEQUENCER VIEW
		
		JPanel	sequencerPanel = new JPanel();
		sequencerPanel.setLayout(new BoxLayout(sequencerPanel, BoxLayout.Y_AXIS));
		getContentPane().add(sequencerPanel, BorderLayout.CENTER);

		// TODO active step view

		// sequencer view

		this.table = new JTable(kTableRowCount, kTableColumnCount)
		{
			public Dimension
			getPreferredScrollableViewportSize()
			{
				return getPreferredSize();
			}
			
			public boolean
			isCellEditable(int inRow, int inColumn)
			{
				return false;
			}
		};
		
		this.table.addMouseListener(this);
		this.table.setCellSelectionEnabled(true);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.setDefaultEditor(Object.class, null);
		this.table.setDefaultRenderer(Object.class, new OptionCellRenderer());
		this.table.setTableHeader(null);
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		ListSelectionModel	selectionModel = this.table.getSelectionModel();
		selectionModel.addListSelectionListener(this);
		
		selectionModel = this.table.getColumnModel().getSelectionModel();
		selectionModel.addListSelectionListener(this);
		
		// add copy/paste actions

		Toolkit	toolkit = Toolkit.getDefaultToolkit();
		KeyStroke copyKey = KeyStroke.getKeyStroke
			(KeyEvent.VK_C, toolkit.getMenuShortcutKeyMask(), false);
		KeyStroke pasteKey = KeyStroke.getKeyStroke
			(KeyEvent.VK_V, toolkit.getMenuShortcutKeyMask(), false);
		this.table.registerKeyboardAction
			(this, "COPY", copyKey, JComponent.WHEN_FOCUSED);
		this.table.registerKeyboardAction
			(this, "PASTE", pasteKey, JComponent.WHEN_FOCUSED);

		// add a scroller around the sequencer panel
		JScrollPane	scrollPane = new JScrollPane(this.table);
		sequencerPanel.add(scrollPane);

		// set the cell contents

		for (int stepNumber = 0; stepNumber < kTableColumnCount; stepNumber++)
		{
			Step	step = this.sequence.getStep(stepNumber);

			for (int optionNumber = 0; optionNumber < kTableRowCount; optionNumber++)
			{
				Option	option = step.getOption(optionNumber);

				this.table.setValueAt(option, optionNumber, stepNumber);
			}
		}
		
		// pack the cells
		packColumns(2);
		packRows(2);

		// OPTION PANEL

		JPanel	optionPanel = new JPanel();
		optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.X_AXIS));
		sequencerPanel.add(optionPanel);

		// probability panel

		JPanel	probabilityPanel = new JPanel();
		probabilityPanel.setLayout(new BoxLayout(probabilityPanel, BoxLayout.Y_AXIS));
		optionPanel.add(probabilityPanel);

		this.probabilitySlider = new JSlider(JSlider.VERTICAL);
		this.probabilitySlider.setMaximum(100);
		this.probabilitySlider.setMinimum(0);
		this.probabilitySlider.setPaintTicks(true);
		this.probabilitySlider.setMajorTickSpacing(10);
		this.probabilitySlider.addChangeListener(this);
		probabilityPanel.add(this.probabilitySlider);
		
		JLabel	probabilityLabel = new JLabel("Probability");
		probabilityLabel.setLabelFor(this.probabilitySlider);
		probabilityPanel.add(probabilityLabel);

		// lower keyboard panel

		JPanel	lowerKeyboardPanel = new JPanel();
		lowerKeyboardPanel.setLayout(new BoxLayout(lowerKeyboardPanel, BoxLayout.Y_AXIS));
		optionPanel.add(lowerKeyboardPanel);	

		this.lowerKeyboardView = new KeyboardView(false, 0, 0);
		lowerKeyboardPanel.add(this.lowerKeyboardView);

		JPanel	lowerOctavePanel = new JPanel();
		lowerOctavePanel.setLayout(new BoxLayout(lowerOctavePanel, BoxLayout.X_AXIS));
		lowerKeyboardPanel.add(lowerOctavePanel);
		
		JButton	lowerOctaveDownButton = new JButton("Octave Down");
		lowerOctaveDownButton.setActionCommand("LOWER_OCTAVE_DOWN");
		lowerOctaveDownButton.addActionListener(this);
		lowerOctavePanel.add(lowerOctaveDownButton);
		
		this.lowerOctaveLabel = new JLabel("0");
		lowerOctavePanel.add(this.lowerOctaveLabel);
		
		JButton	lowerOctaveUpButton = new JButton("Octave Up");
		lowerOctaveUpButton.setActionCommand("LOWER_OCTAVE_UP");
		lowerOctaveUpButton.addActionListener(this);
		lowerOctavePanel.add(lowerOctaveUpButton);

		// velocity lower panel

		JPanel	velocityLowerPanel = new JPanel();
		velocityLowerPanel.setLayout(new BoxLayout(velocityLowerPanel, BoxLayout.Y_AXIS));
		optionPanel.add(velocityLowerPanel);

		this.velocityLowerSlider = new JSlider(JSlider.VERTICAL);
		this.velocityLowerSlider.setMaximum(127);
		this.velocityLowerSlider.setMinimum(0);
		this.velocityLowerSlider.setPaintTicks(true);
		this.velocityLowerSlider.setMajorTickSpacing(10);
		this.velocityLowerSlider.addChangeListener(this);
		velocityLowerPanel.add(this.velocityLowerSlider);
		
		JLabel	velocityLowerLabel = new JLabel("Velocity Lower");
		velocityLowerLabel.setLabelFor(this.velocityLowerSlider);
		velocityLowerPanel.add(velocityLowerLabel);

		// gate time lower panel

		JPanel	gateTimeLowerPanel = new JPanel();
		gateTimeLowerPanel.setLayout(new BoxLayout(gateTimeLowerPanel, BoxLayout.Y_AXIS));
		optionPanel.add(gateTimeLowerPanel);

		this.gateTimeLowerSlider = new JSlider(JSlider.VERTICAL);
		this.gateTimeLowerSlider.setMaximum(100);
		this.gateTimeLowerSlider.setMinimum(0);
		this.gateTimeLowerSlider.setPaintTicks(true);
		this.gateTimeLowerSlider.setMajorTickSpacing(10);
		this.gateTimeLowerSlider.addChangeListener(this);
		gateTimeLowerPanel.add(this.gateTimeLowerSlider);
		
		JLabel	gateTimeLowerLabel = new JLabel("Gate Time Lower");
		gateTimeLowerLabel.setLabelFor(this.gateTimeLowerSlider);
		gateTimeLowerPanel.add(gateTimeLowerLabel);

		// upper keyboard panel
		
		JPanel	upperKeyboardPanel = new JPanel();
		upperKeyboardPanel.setLayout(new BoxLayout(upperKeyboardPanel, BoxLayout.Y_AXIS));
		optionPanel.add(upperKeyboardPanel);	
		
		this.upperKeyboardView = new KeyboardView(true, 0, 0);
		upperKeyboardPanel.add(this.upperKeyboardView);

		JPanel	upperOctavePanel = new JPanel();
		upperOctavePanel.setLayout(new BoxLayout(upperOctavePanel, BoxLayout.X_AXIS));
		upperKeyboardPanel.add(upperOctavePanel);
		
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

		// TOOLBAR

		JPanel	buttonBar = new JPanel();
		buttonBar.setLayout(new FlowLayout(FlowLayout.CENTER));
		getContentPane().add(buttonBar, BorderLayout.SOUTH);
		
		JButton	saveButton = new JButton("Save Sequence");
		saveButton.setActionCommand("SAVE");
		saveButton.addActionListener(this);
		buttonBar.add(saveButton);
		
		JButton	saveAsButton = new JButton("Save Sequence as...");
		saveAsButton.setActionCommand("SAVE_AS");
		saveAsButton.addActionListener(this);
		buttonBar.add(saveAsButton);
		
		JButton	copyButton = new JButton("Copy Option");
		copyButton.setActionCommand("COPY");
		copyButton.addActionListener(this);
		buttonBar.add(copyButton);
		
		JButton	pasteButton = new JButton("Paste Option");
		pasteButton.setActionCommand("PASTE");
		pasteButton.addActionListener(this);
		buttonBar.add(pasteButton);
		
		JButton	swapPasteButton = new JButton("Swap Paste Option");
		swapPasteButton.setActionCommand("SWAP_PASTE");
		swapPasteButton.addActionListener(this);
		buttonBar.add(swapPasteButton);
		
		pack();

		this.table.setRowSelectionInterval(0, 0);
		this.table.setColumnSelectionInterval(0, 0);
	}

	// COMPONENT OVERRIDE
	
	@Override
	public void
	setVisible(boolean inVisible)
	{
		if (inVisible)
		{
			super.setVisible(inVisible);
		}
		else
		{
			boolean	closeWindow = true;
			
			// see if any child windows are open
			Frame[]	frames = Frame.getFrames();
			
			if (this.sequence.isModified())
			{
				StringBuffer	buffer = new StringBuffer();
				
				buffer.append("Save \"");
				buffer.append(getTitle());
				buffer.append(" before closing?");
				
				int	response = JOptionPane.showConfirmDialog
					(this, buffer.toString(), "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
					
				if (response == JOptionPane.YES_OPTION)
				{
					saveSequence(false);
				}
				else
				if (response == JOptionPane.CANCEL_OPTION)
				{
					closeWindow = false;
				}
			}
			
			if (closeWindow)
			{
				System.exit(0);
			}
		}
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
			
			if (! isVisible())
			{
				dispose();
			}
		}
		else
		if (actionCommand.equalsIgnoreCase("SAVE")
			|| actionCommand.equalsIgnoreCase("SAVE_AS"))
		{
			boolean	askForFile = false;
			
			if (actionCommand.equalsIgnoreCase("SAVE_AS"))
			{
				askForFile = true;
			}
			else
			{
				if (this.file == null)
				{
					askForFile = true;
				}
			}
			
			saveSequence(askForFile);
		}
		else
		if (actionCommand.equalsIgnoreCase("LOWER_OCTAVE_DOWN"))
		{
			int	octaveOffset = this.lowerKeyboardView.getOctaveOffset();
			
			octaveOffset--;
			
			if (this.lowerKeyboardView.setOctaveOffset(octaveOffset))
			{
				this.lowerOctaveLabel.setText("" + octaveOffset);

				this.lowerKeyboardView.revalidate();
				this.lowerKeyboardView.repaint();
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

				this.lowerKeyboardView.revalidate();
				this.lowerKeyboardView.repaint();
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

				this.upperKeyboardView.revalidate();
				this.upperKeyboardView.repaint();
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

				this.upperKeyboardView.revalidate();
				this.upperKeyboardView.repaint();
			}
		}
		/*
		else
		if (actionCommand.equalsIgnoreCase("COPY"))
		{
			int stepNumber = this.table.getSelectedRow();
			int optionNumber = this.table.getSelectedColumn();

			if (stepNumber == -1 || optionNumber == -1)
			{
				SequencerWindow.showErrorDialog
					("Error", "Please select a step option to copy.");
			}
			else
			{
				int	patchNumber = (row * kTableColumnCount) + column;
				Patch	patch = this.bank.getPatchCopy(patchNumber);
				TransferablePatch	transferable = new TransferablePatch(patch);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, this);
			}
		}
		else
		if (actionCommand.equalsIgnoreCase("PASTE"))
		{
			int row = this.table.getSelectedRow();
			int column = this.table.getSelectedColumn();

			if (row == -1 || column == -1)
			{
				SequencerWindow.showErrorDialog
					("Error", "Please select a patch location to paste.");
			}
			else
			{
				int	patchNumber = (row * kTableColumnCount) + column;

				Clipboard	clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable	transferable = clipboard.getContents(this);
				
				if (transferable != null)
				{
					DataFlavor	flavour = new DataFlavor(Patch.class, "Prophet VS Patch");
					
					try
					{
						Object	data = transferable.getTransferData(flavour);
						
						if (data != null && data instanceof Patch)
						{
							Patch	patch = (Patch) data;
							
							// copy the patch!
							// it might get pasted twice and this is only a reference
							Patch	copy = new Patch(patch);
							
							// override the patch number in the patch
							// so it goes in the right place
							copy.setPatchNumber(patchNumber);
							setPatch(copy);
						}
					}
					catch(Exception inException)
					{
						System.err.println(inException);
					}
				}
			}
		}
		else
		if (actionCommand.equalsIgnoreCase("SWAP_PASTE"))
		{
			// this is a nondestructive paste which swaps the selection & paste buffer
			
			int stepNumber = this.table.getSelectedColumn();
			int optionNumber = this.table.getSelectedRow();

			if (stepNumber == -1 || optionNumber == -1)
			{
				SequencerWindow.showErrorDialog("Error", "Please select a patch location to swap paste.");
			}
			else
			{
				int	patchNumber = (row * kTableColumnCount) + column;
				
				Clipboard	clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable	transferable = clipboard.getContents(this);
				
				if (transferable != null)
				{
					DataFlavor	flavour = new DataFlavor(Patch.class, "Prophet VS Patch");
					
					try
					{
						Object	data = transferable.getTransferData(flavour);
						
						if (data != null && data instanceof Patch)
						{
							Patch	pastePatch = (Patch) data;
							
							// ok now copy the selection into the clipboard
							Patch	copyPatch = this.bank.getPatchCopy(patchNumber);
							transferable = new TransferablePatch(copyPatch);
							Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, this);

							// and now paste the saved patch into place
							// override the patch number in the patch
							// so it goes in the right place
							pastePatch.setPatchNumber(patchNumber);
							setPatch(pastePatch);
						}
					}
					catch(Exception inException)
					{
System.err.println(inException);
					}
				}
			}
		}
		*/
		else
		{
			System.err.println("HEY! Unknown action command: " + actionCommand);
		}
	}
	
	// CHANGELISTENER IMPLEMENTATION
	
	public void
	stateChanged (ChangeEvent inEvent)
	{
		JComponent	source = (JComponent) inEvent.getSource ();

		if (source instanceof JSlider)
		{
			int	stepNumber = this.table.getSelectedColumn ();
			int	optionNumber = this.table.getSelectedRow ();
			
			if (stepNumber >= 0 && optionNumber >= 0)
			{
				if (source == this.gateTimeLowerSlider)
				{
					int	value = this.gateTimeLowerSlider.getValue();

					setOptionGateTimeLower(stepNumber, optionNumber, value);
				}
				else if (source == this.gateTimeUpperSlider)
				{
					int	value = this.gateTimeUpperSlider.getValue();

					setOptionGateTimeUpper(stepNumber, optionNumber, value);
				}
				else if (source == this.probabilitySlider)
				{
					int	value = this.probabilitySlider.getValue();

					setOptionProbability(stepNumber, optionNumber, value);
				}
				else if (source == this.velocityLowerSlider)
				{
					int	value = this.velocityLowerSlider.getValue();

					setOptionVelocityLower(stepNumber, optionNumber, value);
				}
				else if (source == this.velocityUpperSlider)
				{
					int	value = this.velocityUpperSlider.getValue();

					setOptionVelocityUpper(stepNumber, optionNumber, value);
				}
			}
		}
	}

	// CLIPBOARD OWNER IMPLEMENTATION
	
	@Override
	public void
	lostOwnership(Clipboard inClipboard, Transferable inContents)
	{
		// like we care
	}
	
	// LIST SELECTION LISTENER IMPLEMENTATION
	
	// note we DO allow program changes from bank selection window changes
	// if the bank isn't in the prophet
	// it just becomes a handy way of selecting patches :-)
	public void
	valueChanged (ListSelectionEvent inEvent)
	{
		if (inEvent.getValueIsAdjusting ())
		{
			return;
		}

		int	stepNumber = this.table.getSelectedColumn ();
		int	optionNumber = this.table.getSelectedRow ();
		
		if (stepNumber >= 0 && optionNumber >= 0)
		{
			// update the option view here

			System.out.println("ListSelectionListener.valueChanged() to step " + stepNumber + " option " + optionNumber);

			Option	option = getOption(stepNumber, optionNumber);

			this.probabilitySlider.setValue(option.probability);
			this.velocityLowerSlider.setValue(option.velocityLower);

			this.lowerKeyboardView.setOption(stepNumber, optionNumber);
			this.upperKeyboardView.setOption(stepNumber, optionNumber);
		}
	}
	
	// MOUSE LISTENER IMPLEMENTATION

	@Override
	public void
	mouseClicked(MouseEvent inEvent)
	{
	}

	public void
	mousePressed(MouseEvent inEvent)
	{
	}

	public void
	mouseReleased(MouseEvent inEvent)
	{
	}

	public void
	mouseEntered(MouseEvent inEvent)
	{
	}

	public void
	mouseExited(MouseEvent inEvent)
	{
	}

	// PUBLIC METHODS

	public Option
	getOption(int inStepNumber, int inOptionNumber)
	{
		Step		step = this.sequence.getStep(inStepNumber);

		return step.getOption(inOptionNumber);
	}

	public int
	getRandomPositiveInt(int inLimit)
	{
		int	number = this.random.nextInt();

		return Math.abs(number) % inLimit;
	}

	public void
	setSequenceModified(boolean inModified)
	{
		getRootPane().putClientProperty("windowModified", new Boolean(inModified));
		getRootPane().putClientProperty("Window.documentModified", new Boolean(inModified));
		
		this.sequence.setModified(inModified);
	}
	
	public void
	setOption(Option inPatch)
	{
		int	stepNumber = this.table.getSelectedColumn();
		int	optionNumber = this.table.getSelectedRow();

		if (stepNumber == -1 || optionNumber == -1)
		{
			SequencerWindow.showErrorDialog
				("Error", "Please select a step option to copy.");
		}
		else
		{
			Option	option = getOption(stepNumber, optionNumber);

			this.table.setValueAt(option, optionNumber, stepNumber);
		}

		setSequenceModified(true);
	}

	public void
	setOptionGateTimeLower(int inStepNumber, int inOptionNumber, int inValue)
	{
			Option	option = getOption(inStepNumber, inOptionNumber);
			option.gateTimeLower = Math.min(option.gateTimeUpper, inValue);

			this.table.setValueAt(option, inOptionNumber, inStepNumber);
	}

	public void
	setOptionGateTimeUpper(int inStepNumber, int inOptionNumber, int inValue)
	{
			Option	option = getOption(inStepNumber, inOptionNumber);
			option.gateTimeUpper = Math.max(option.gateTimeLower, inValue);

			this.table.setValueAt(option, inOptionNumber, inStepNumber);
	}

	public void
	setOptionNoteLower(int inStepNumber, int inOptionNumber, int inValue)
	{
		Option	option = getOption(inStepNumber, inOptionNumber);
		option.noteLower = Math.min(option.noteUpper, inValue);
	
		this.table.setValueAt(option, inOptionNumber, inStepNumber);
	}

	public void
	setOptionNoteUpper(int inStepNumber, int inOptionNumber, int inValue)
	{
			Option	option = getOption(inStepNumber, inOptionNumber);
			option.noteUpper = Math.max(option.noteLower, inValue);

			this.table.setValueAt(option, inOptionNumber, inStepNumber);
	}

	public void
	setOptionProbability(int inStepNumber, int inOptionNumber, int inValue)
	{
			Option	option = getOption(inStepNumber, inOptionNumber);
			option.probability = inValue;

			this.table.setValueAt(option, inOptionNumber, inStepNumber);
	}

	public void
	setOptionVelocityLower(int inStepNumber, int inOptionNumber, int inValue)
	{
			Option	option = getOption(inStepNumber, inOptionNumber);
			option.velocityLower = Math.min(option.velocityUpper, inValue);

			this.table.setValueAt(option, inOptionNumber, inStepNumber);
	}

	public void
	setOptionVelocityUpper(int inStepNumber, int inOptionNumber, int inValue)
	{
			Option	option = getOption(inStepNumber, inOptionNumber);
			option.velocityUpper = Math.max(option.velocityLower, inValue);

			this.table.setValueAt(option, inOptionNumber, inStepNumber);
	}

	// PRIVATE METHODS
	
	private void
	packColumns(int inMargin)
	{
		TableModel	model = this.table.getModel();
		DefaultTableColumnModel	columnModel = (DefaultTableColumnModel) table.getColumnModel();
			
		for (int col = 0; col < this.table.getColumnCount(); col++)
		{
			TableColumn	column = columnModel.getColumn(col);
			
			int	width = 0;

			// determine widest row in column
			for (int row = 0; row < this.table.getRowCount(); row++)
			{
				TableCellRenderer	renderer = this.table.getCellRenderer(row, col);
				Component	component = renderer.getTableCellRendererComponent
					(this.table, this.table.getValueAt(row, col), false, false, row, col);
				
				width = Math.max(width, component.getPreferredSize().width);
			}
			
			// add the margin
			width += 2 * inMargin;
			
			column.setPreferredWidth(width);
		}
	}
	
	private void
	packRows(int inMargin)
	{
		for (int row = 0; row < this.table.getRowCount(); row++)
		{
			int	height = this.table.getRowHeight();
			
			// determine highest cell in row
			for (int col = 0; col < this.table.getColumnCount(); col++)
			{
				TableCellRenderer	renderer = this.table.getCellRenderer(row, col);
				Component	component = this.table.prepareRenderer(renderer, row, col);
				int	componentHeight = component.getPreferredSize().height +(2 * inMargin);
				
				height = Math.max(height, componentHeight);
			}
			
			this.table.setRowHeight(height);
		}
	}
	
	private boolean
	saveSequence(boolean inAskForFile)
	{
		boolean	saved = false;
		
		if (inAskForFile || this.file == null)
		{
			File	tempFile = SequencerWindow.getFileForSave(this, "Save", null);
			
			if (tempFile != null)
			{
				this.file = tempFile;
			}
			else
			{
				// otherwise we save to the original file
				// having selected "Save as..."
				return false;
			}
		}
		
		if (this.file != null)
		{
			FileOutputStream	fos = null;
			
			try
			{
				fos = new FileOutputStream(this.file);
				// this.sequence.write(fos);
				
				setSequenceModified(false);
				saved = true;
			}
			catch(Exception inException)
			{
				SequencerWindow.showErrorDialog("Error", inException);
			}
			finally
			{
				if (fos != null)
				{
					try
					{
						fos.close();
					}
					catch(Exception inException)
					{
					}
				}
			}
		}
		
		return saved;
	}
	
	// STATIC PRIVATE FINAL DATA
	
	private static final
	String[] kNoteStrings = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	
	private static final String
	kPreferencesPath = "net/redfish/sequencer";
	
	private static final int
	kTableColumnCount = 16;

	private static final int
	kTableRowCount = 2;

	// PRIVATE STATIC DATA
	
	private static Preferences
	sPrefs = null;

	private static SequencerWindow
	sInstance = null;

	// PRIVATE DATA

	private File
	file = null;
	
	private JSlider
	gateTimeLowerSlider = null;

	private JSlider
	gateTimeUpperSlider = null;

	private KeyboardView
	lowerKeyboardView = null;

	private JLabel
	lowerOctaveLabel = null;

	private JSlider
	velocityLowerSlider = null;

	private JSlider
	velocityUpperSlider = null;

	private JSlider
	probabilitySlider = null;

	private Random
	random = new Random();

	private Sequence
	sequence;

	private JLabel
	statusLabel = null;
	
	private JTable
	table = null;
	
	private KeyboardView
	upperKeyboardView = null;

	private JLabel
	upperOctaveLabel = null;

	// PRIVATE DATA

}

