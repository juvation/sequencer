// Sequencer.java

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

import java.util.ArrayList;

// PUBLIC CLASS

public class Sequence
{

	// PUBLIC CONSTRUCTOR
	
	public Sequence()
	{
		this.steps = new ArrayList<Step>(16);
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
		getStep(inStepNumber).selectOption();
	}

	public void
	setModified(boolean inModified)
	{
		this.modified = inModified;
	}

	// PRIVATE DATA
	
	private boolean
	modified = false;

	private ArrayList<Step>
	steps;
}

