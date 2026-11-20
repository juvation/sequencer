// Sequencer.java

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

// PUBLIC CLASS

public class Sequence
{

	// PUBLIC CONSTRUCTOR
	
	public Sequence(int inStepCount, int inOptionCount)
	{
		this.steps = new ArrayList<Step>();

		for (int i = 0; i < inStepCount; i++)
		{
			this.steps.add(new Step(inOptionCount));
		}
	}

	// PUBLIC METHODS

	public Option
	getSelectedOption(int inStepNumber)
	{
		return this.steps.get(inStepNumber).getSelectedOption();
	}

	public Step
	getStep(int inStepNumber)
	{
		return this.steps.get(inStepNumber);
	}

	public boolean
	isModified()
	{
		return this.modified;
	}

	public void
	selectOption(int inStepNumber)
	{
		System.out.println("select option for step " + inStepNumber);

		getStep(inStepNumber).selectOption();
	}

	public void
	setModified(boolean inModified)
	{
		this.modified = inModified;
	}

	public void
	write(OutputStream outStream)
	throws IOException
	{
	}

	// PRIVATE DATA
	
	private boolean
	modified = false;

	private ArrayList<Step>
	steps;
}

