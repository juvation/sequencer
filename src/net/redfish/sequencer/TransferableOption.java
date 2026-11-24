// TransferableOption.java

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

// CLASS

public class TransferableOption
	implements Transferable
{
	public
	TransferableOption (Option inOption)
	{
		this.option = inOption;
	}
	
	// TRANSFERABLE IMPLEMENTATION
	
	public Object
	getTransferData (DataFlavor inFlavor)
		throws UnsupportedFlavorException
	{
		if (inFlavor.getRepresentationClass () != Option.class)
		{
			// huh? why not just return null here?
			throw new UnsupportedFlavorException (inFlavor);
		}
		
		return this.option;
	}
	
	public DataFlavor[]
	getTransferDataFlavors ()
	{
		DataFlavor[]	flavors = new DataFlavor [1];
		flavors [0] = new DataFlavor (Option.class, "AssQuencer Option");
		
		return flavors;
	}
	
	public boolean
	isDataFlavorSupported (DataFlavor inFlavor)
	{
		return (inFlavor.getRepresentationClass () == Option.class);
	}
	
	// PRIVATE DATA
	
	private Option
	option = null;
	
}

