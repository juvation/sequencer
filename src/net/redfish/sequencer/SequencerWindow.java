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

		setResizable(false);

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
		
		JLabel	probabilityLabel = new JLabel("P%");
		probabilityLabel.setLabelFor(this.probabilitySlider);
		probabilityPanel.add(probabilityLabel);

		// mute probability panel

		JPanel	mutePanel = new JPanel();
		mutePanel.setLayout(new BoxLayout(mutePanel, BoxLayout.Y_AXIS));
		optionPanel.add(mutePanel);

		this.muteSlider = new JSlider(JSlider.VERTICAL);
		this.muteSlider.setMaximum(100);
		this.muteSlider.setMinimum(0);
		this.muteSlider.setPaintTicks(true);
		this.muteSlider.setMajorTickSpacing(10);
		this.muteSlider.addChangeListener(this);
		mutePanel.add(this.muteSlider);

		JLabel	muteLowerLabel = new JLabel("Mute P%");
		muteLowerLabel.setLabelFor(this.muteSlider);
		mutePanel.add(muteLowerLabel);

		// tie probability panel

		JPanel	tiePanel = new JPanel();
		tiePanel.setLayout(new BoxLayout(tiePanel, BoxLayout.Y_AXIS));
		optionPanel.add(tiePanel);

		this.tieSlider = new JSlider(JSlider.VERTICAL);
		this.tieSlider.setMaximum(100);
		this.tieSlider.setMinimum(0);
		this.tieSlider.setPaintTicks(true);
		this.tieSlider.setMajorTickSpacing(10);
		this.tieSlider.addChangeListener(this);
		tiePanel.add(this.tieSlider);
		
		JLabel	tieLabel = new JLabel("Tie P%");
		tieLabel.setLabelFor(this.tieSlider);
		tiePanel.add(tieLabel);

		// ratchet probability panel

		JPanel	ratchetPanel = new JPanel();
		ratchetPanel.setLayout(new BoxLayout(ratchetPanel, BoxLayout.Y_AXIS));
		optionPanel.add(ratchetPanel);

		this.ratchetSlider = new JSlider(JSlider.VERTICAL);
		this.ratchetSlider.setMaximum(100);
		this.ratchetSlider.setMinimum(0);
		this.ratchetSlider.setPaintTicks(true);
		this.ratchetSlider.setMajorTickSpacing(10);
		this.ratchetSlider.addChangeListener(this);
		ratchetPanel.add(this.ratchetSlider);
		
		JLabel	ratchetLowerLabel = new JLabel("Rat P%");
		ratchetLowerLabel.setLabelFor(this.ratchetSlider);
		ratchetPanel.add(ratchetLowerLabel);

		// LOWER

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
		
		JLabel	velocityLowerLabel = new JLabel("Velocity");
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
		
		JLabel	gateTimeLowerLabel = new JLabel("Gate");
		gateTimeLowerLabel.setLabelFor(this.gateTimeLowerSlider);
		gateTimeLowerPanel.add(gateTimeLowerLabel);

		// UPPER

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

		// velocity upper panel

		JPanel	velocityUpperPanel = new JPanel();
		velocityUpperPanel.setLayout(new BoxLayout(velocityUpperPanel, BoxLayout.Y_AXIS));
		optionPanel.add(velocityUpperPanel);

		this.velocityUpperSlider = new JSlider(JSlider.VERTICAL);
		this.velocityUpperSlider.setMaximum(127);
		this.velocityUpperSlider.setMinimum(0);
		this.velocityUpperSlider.setPaintTicks(true);
		this.velocityUpperSlider.setMajorTickSpacing(10);
		this.velocityUpperSlider.addChangeListener(this);
		velocityUpperPanel.add(this.velocityUpperSlider);
		
		JLabel	velocityUpperLabel = new JLabel("Velocity");
		velocityUpperLabel.setLabelFor(this.velocityUpperSlider);
		velocityUpperPanel.add(velocityUpperLabel);

		// gate time upper panel

		JPanel	gateTimeUpperPanel = new JPanel();
		gateTimeUpperPanel.setLayout(new BoxLayout(gateTimeUpperPanel, BoxLayout.Y_AXIS));
		optionPanel.add(gateTimeUpperPanel);

		this.gateTimeUpperSlider = new JSlider(JSlider.VERTICAL);
		this.gateTimeUpperSlider.setMaximum(100);
		this.gateTimeUpperSlider.setMinimum(0);
		this.gateTimeUpperSlider.setPaintTicks(true);
		this.gateTimeUpperSlider.setMajorTickSpacing(10);
		this.gateTimeUpperSlider.addChangeListener(this);
		gateTimeUpperPanel.add(this.gateTimeUpperSlider);
		
		JLabel	gateTimeUpperLabel = new JLabel("Gate");
		gateTimeUpperLabel.setLabelFor(this.gateTimeUpperSlider);
		gateTimeUpperPanel.add(gateTimeUpperLabel);

		// TOOLBAR

		JPanel	buttonBar = new JPanel();
		buttonBar.setLayout(new FlowLayout(FlowLayout.CENTER));
		getContentPane().add(buttonBar, BorderLayout.SOUTH);
		
		JButton	startButton = new JButton("Start");
		startButton.setActionCommand("START");
		startButton.addActionListener(this);
		buttonBar.add(startButton);

		JButton	stopButton = new JButton("Stop");
		stopButton.setActionCommand("STOP");
		stopButton.addActionListener(this);
		buttonBar.add(stopButton);

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
		
		System.out.println("SequencerWindow.actionPerformed() " + actionCommand);

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
		else
		if (actionCommand.equalsIgnoreCase("COPY"))
		{
			int stepNumber = this.table.getSelectedColumn();
			int optionNumber = this.table.getSelectedRow();

			if (stepNumber >= 0 && optionNumber >= 0)
			{
				Option	option = getOption(stepNumber, optionNumber);
				TransferableOption	transferable = new TransferableOption(option);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, this);
			}
			else
			{
				SequencerWindow.showErrorDialog("Error", "Please select an option to copy.");
			}
		}
		else
		if (actionCommand.equalsIgnoreCase("PASTE"))
		{
			Clipboard	clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable	transferable = clipboard.getContents(this);
			
			if (transferable != null)
			{
				DataFlavor	flavour = new DataFlavor(Option.class, "AssQuencer Option");
				
				try
				{
					Object	data = transferable.getTransferData(flavour);
					
					if (data != null && data instanceof Option)
					{
						Option	option = (Option) data;

						// copy the option!
						// it might get pasted twice and this is only a reference
						Option	copy = new Option(option);
						
						setOption(copy);
					}
					else
					{
						System.err.println("data is null or not an Option");
					}
				}
				catch(Exception inException)
				{
					System.err.println(inException);
				}
			}
			else
			{
				System.err.println("transferable is null");
			}
		}
		else
		if (actionCommand.equalsIgnoreCase("SWAP_PASTE"))
		{
			// this is a nondestructive paste which swaps the selection & paste buffer
			
			int stepNumber = this.table.getSelectedColumn();
			int optionNumber = this.table.getSelectedRow();

			if (stepNumber >= 0 || optionNumber >= 0)
			{
				Option	option = getOption(stepNumber, optionNumber);
				
				Clipboard	clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable	transferable = clipboard.getContents(this);
				
				if (transferable != null)
				{
					DataFlavor	flavour = new DataFlavor(Option.class, "AssQuencer Option");
					
					try
					{
						Object	data = transferable.getTransferData(flavour);
						
						if (data != null && data instanceof Option)
						{
							Option	pasteOption = (Option) data;
							
							// ok now copy the selection into the clipboard
							Option	copyOption = getOption(stepNumber, optionNumber);
							transferable = new TransferableOption(copyOption);
							Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, this);

							// and now paste the saved option into place
							setOption(pasteOption);
						}
					}
					catch(Exception inException)
					{
						System.err.println(inException);
					}
				}
			}
			else
			{
				SequencerWindow.showErrorDialog("Error", "Please select an option location to swap paste.");
			}
		}
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
					setOptionGateTimeLower(stepNumber, optionNumber);
				}
				else if (source == this.gateTimeUpperSlider)
				{
					setOptionGateTimeUpper(stepNumber, optionNumber);
				}
				else if (source == this.probabilitySlider)
				{
					setOptionProbability(stepNumber, optionNumber);
				}
				else if (source == this.muteSlider)
				{
					setOptionMuteProbability(stepNumber, optionNumber);
				}
				else if (source == this.ratchetSlider)
				{
					setOptionRatchetProbability(stepNumber, optionNumber);
				}
				else if (source == this.tieSlider)
				{
					setOptionTieProbability(stepNumber, optionNumber);
				}
				else if (source == this.velocityLowerSlider)
				{
					setOptionVelocityLower(stepNumber, optionNumber);
				}
				else if (source == this.velocityUpperSlider)
				{
					setOptionVelocityUpper(stepNumber, optionNumber);
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
			this.muteSlider.setValue(option.muteProbability);
			this.tieSlider.setValue(option.tieProbability);
			this.ratchetSlider.setValue(option.ratchetProbability);

			this.velocityLowerSlider.setValue(option.velocityLower);
			this.velocityUpperSlider.setValue(option.velocityUpper);

			this.gateTimeLowerSlider.setValue(option.gateTimeLower);
			this.gateTimeUpperSlider.setValue(option.gateTimeUpper);

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
	setOption(Option inOption)
	{
		int	stepNumber = this.table.getSelectedColumn();
		int	optionNumber = this.table.getSelectedRow();

		if (stepNumber >= 0 || optionNumber >= 0)
		{
			this.table.setValueAt(inOption, optionNumber, stepNumber);
		}
		else
		{
			SequencerWindow.showErrorDialog("Error", "Please select a step option.");
		}

		setSequenceModified(true);
	}

	public void
	setOptionGateTimeLower(int inStepNumber, int inOptionNumber)
	{
			Option	option = getOption(inStepNumber, inOptionNumber);
			option.gateTimeLower = Math.min(option.gateTimeUpper, this.gateTimeLowerSlider.getValue());

			this.table.setValueAt(option, inOptionNumber, inStepNumber);
	}

	public void
	setOptionGateTimeUpper(int inStepNumber, int inOptionNumber)
	{
			Option	option = getOption(inStepNumber, inOptionNumber);
			option.gateTimeUpper = Math.max(option.gateTimeLower, this.gateTimeUpperSlider.getValue());

			this.table.setValueAt(option, inOptionNumber, inStepNumber);
	}

	// called from KeyboardView
	public void
	setOptionNoteLower(int inStepNumber, int inOptionNumber, int inValue)
	{
		Option	option = getOption(inStepNumber, inOptionNumber);
		option.noteLower = Math.min(option.noteUpper, inValue);
	
		this.table.setValueAt(option, inOptionNumber, inStepNumber);
	}

	// called from KeyboardView
	public void
	setOptionNoteUpper(int inStepNumber, int inOptionNumber, int inValue)
	{
			Option	option = getOption(inStepNumber, inOptionNumber);
			option.noteUpper = Math.max(option.noteLower, inValue);

			this.table.setValueAt(option, inOptionNumber, inStepNumber);
	}

	public void
	setOptionMuteProbability(int inStepNumber, int inOptionNumber)
	{
			Option	option = getOption(inStepNumber, inOptionNumber);
			option.muteProbability = this.muteSlider.getValue();

			this.table.setValueAt(option, inOptionNumber, inStepNumber);
	}

	public void
	setOptionProbability(int inStepNumber, int inOptionNumber)
	{
			Option	option = getOption(inStepNumber, inOptionNumber);
			option.probability = this.probabilitySlider.getValue();

			this.table.setValueAt(option, inOptionNumber, inStepNumber);
	}

	public void
	setOptionRatchetProbability(int inStepNumber, int inOptionNumber)
	{
			Option	option = getOption(inStepNumber, inOptionNumber);
			option.ratchetProbability = this.ratchetSlider.getValue();

			this.table.setValueAt(option, inOptionNumber, inStepNumber);
	}

	public void
	setOptionTieProbability(int inStepNumber, int inOptionNumber)
	{
			Option	option = getOption(inStepNumber, inOptionNumber);
			option.tieProbability = this.tieSlider.getValue();

			this.table.setValueAt(option, inOptionNumber, inStepNumber);
	}

	public void
	setOptionVelocityLower(int inStepNumber, int inOptionNumber)
	{
			Option	option = getOption(inStepNumber, inOptionNumber);
			option.velocityLower = Math.min(option.velocityUpper, this.velocityLowerSlider.getValue());

			this.table.setValueAt(option, inOptionNumber, inStepNumber);
	}

	public void
	setOptionVelocityUpper(int inStepNumber, int inOptionNumber)
	{
			Option	option = getOption(inStepNumber, inOptionNumber);
			option.velocityUpper = Math.max(option.velocityLower, this.velocityUpperSlider.getValue());

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
	muteSlider = null;

	private JSlider
	probabilitySlider = null;

	private Random
	random = new Random();

	private JSlider
	ratchetSlider = null;

	private Sequence
	sequence;

	private JLabel
	statusLabel = null;
	
	private JTable
	table = null;
	
	private JSlider
	tieSlider = null;

	private KeyboardView
	upperKeyboardView = null;

	private JLabel
	upperOctaveLabel = null;

	private JSlider
	velocityLowerSlider = null;

	private JSlider
	velocityUpperSlider = null;

	// PRIVATE DATA

}

