// CurrentStepCellRenderer.java

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.SwingConstants;

public class CurrentStepCellRenderer
	extends JComponent
	implements TableCellRenderer
{
	public
	CurrentStepCellRenderer()
	{
		// for centring the label
		setLayout(new GridBagLayout());
		setOpaque(false);

		Font	largeFont = new Font("Sans Serif", Font.BOLD, 18);

		this.stepLabel = new JLabel();		
		this.stepLabel.setFont(largeFont);
		this.stepLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(this.stepLabel);

		// inverted selections plain don't work
		// so resort to borders :-|
		this.border = BorderFactory.createLineBorder(Color.black, 1);
	}
	
	// TABLECELLRENDERER IMPLEMENTATION
	
	@Override
	public Component
	getTableCellRendererComponent(JTable inTable, Object inValue,
		boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn)
	{
		Integer	step = (Integer) inValue;

		try
		{
			// maybe we don't have borders in the current step indicator
			// setBorder(this.border);

			if (inIsSelected)
			{
				setBackground(Color.RED);
				this.stepLabel.setBackground(Color.RED);
			}
			else
			{
				setBackground(Color.WHITE);
				this.stepLabel.setBackground(Color.WHITE);
			}

			this.stepLabel.setText(Integer.toString(step));
		}
		catch(Exception inException)
		{
			System.err.println(inException);
		}
		
		return this;
	}
	
	public void
	setValue(Object inValue)
	{
	}
	
	// PRIVATE DATA
	
	private Border
	border = null;
	
	private JLabel
	stepLabel = null;

}
