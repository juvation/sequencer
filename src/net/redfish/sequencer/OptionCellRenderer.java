// OptionCellRenderer.java

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

public class OptionCellRenderer
	extends JComponent
	implements TableCellRenderer
{
	public
	OptionCellRenderer ()
	{
		setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
		setOpaque (false);
		
		this.noteLabel = new JLabel ();
		this.noteLabel.setFont (new Font ("Sans Serif", Font.BOLD, 12));
		add (this.noteLabel);
		
		// inverted selections plain don't work
		// so resort to borders :-|
		this.border = BorderFactory.createLineBorder (Color.black, 1);
	}
	
	// COMPONENT OVERRIDE
	
	// in order to get the right size in BankWindow
	// delegating to Component gets it wrong
	// could just hardcode to 100, but it might be wrong on Windows etc
	public Dimension
	getPreferredSize ()
	{
		Dimension	size = super.getPreferredSize ();

		// leave the height alone
		
		// calculate the width based on our labels
		int	width = 0;

/*		
		if (this.filterLabel != null)
		{
			width = Math.max (width, this.filterLabel.getPreferredSize ().width);
		}
		
		width = Math.max (width, this.titleLabel.getPreferredSize ().width);
		width = Math.max (width, this.waveALabel.getPreferredSize ().width);
		width = Math.max (width, this.waveBLabel.getPreferredSize ().width);
		width = Math.max (width, this.waveCLabel.getPreferredSize ().width);
		width = Math.max (width, this.waveDLabel.getPreferredSize ().width);
*/

		size.setSize (width, size.height);
		
		return size;
	}
	
	// TABLECELLRENDERER IMPLEMENTATION
	
	public Component
	getTableCellRendererComponent(JTable inTable, Object inValue,
		boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn)
	{
		Option	option = (Option) inValue;

		try
		{
// System.err.println ("cell row=" + inRow + " col=" + inColumn);
// System.err.println ("selected=" + inIsSelected + " hasfocus=" + inHasFocus);

			if (inIsSelected)
			{
				setBorder (this.border);
			}
			else
			{
				setBorder (null);
			}
			
			this.noteLabel.setText ("HEY");
		}
		catch (Exception inException)
		{
			System.err.println (inException);
		}
		
		return this;
	}
	
	public void
	setValue (Object inValue)
	{
	}
	
	// The following methods override the defaults for performance reasons

	/*
	protected void
	firePropertyChange (String inPropertyName, Object inOldValue, Object inNewValue)
	{
	}
	
	public void
	firePropertyChange (String inPropertyName, boolean inOldValue, boolean inNewValue)
	{
	}
	
	public void
	revalidate ()
	{
	}
	
	public void
	validate ()
	{
	}
	*/
	
	// PRIVATE DATA
	
	private Border
	border = null;
	
	private JLabel
	noteLabel = null;

}
