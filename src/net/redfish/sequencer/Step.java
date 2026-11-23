// Step.java

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

import java.util.ArrayList;

// PUBLIC CLASS

public class Step
{
	// PUBLIC CONSTRUCTOR

	public Step(int inOptionCount)
	{
		this.selectedOption = null;

		this.options = new ArrayList<Option>();

		for (int i = 0; i < inOptionCount; i++)
		{
			this.options.add(new Option());
		}
	}

	// PUBLIC METHODS

	public Option
	getSelectedOption()
	{
		return this.selectedOption;
	}

	public Option
	getOption(int inOptionNumber)
	{
		return options.get(inOptionNumber);
	}

	public boolean
	selectBoolean(int inProbability)
	{
		boolean	selected = false;

		if (inProbability == 0)
		{
			selected = false;
		}
		else if (inProbability == 100)
		{
			selected = true;
		}
		else
		{
			if (SequencerWindow.getInstance().getRandomPositiveInt(100) < inProbability)
			{
				selected = true;
			}
		}

		return selected;
	}

	public int
	selectInt(int inLower, int inUpper)
	{
		int	value = 0;

		if (inLower == inUpper)
		{
			value = inLower;
		}
		else
		{
			int	range = inUpper - inLower;
			range = Math.abs(range);
			range++;

			int	roll = SequencerWindow.getInstance().getRandomPositiveInt(range);
			value = inLower + roll;
		}

		return value;
	}

	public void
	selectOption()
	{
		this.selectedOption = null;

		int	roll = SequencerWindow.getInstance().getRandomPositiveInt(100);
		int	target = 0;

		for (Option option : this.options)
		{
			target += option.probability;

			if (roll < target)
			{
				this.selectedOption = option;
				break;
			}
		}

		if (this.selectedOption == null)
		{
			System.err.println("no option selected");
			return;
		}

		this.selectedOption.note = selectInt
			(this.selectedOption.noteLower, this.selectedOption.noteUpper);

		this.selectedOption.velocity = selectInt
			(this.selectedOption.velocityLower, this.selectedOption.velocityUpper);

		this.selectedOption.gateTime = selectInt
			(this.selectedOption.gateTimeLower, this.selectedOption.gateTimeUpper);

		this.selectedOption.mute = selectBoolean(this.selectedOption.muteProbability);
		this.selectedOption.tie = selectBoolean(this.selectedOption.tieProbability);
		
		// wait, this way we only have one type of ratchet
		// unless there is a ratchet granularity property
		this.selectedOption.ratchet = selectBoolean(this.selectedOption.ratchetProbability);
	}

	public void
	setOption(int inOptionNumber, Option inOption)
	{
		this.options.set(inOptionNumber, inOption);
	}

	// PRIVATE DATA
	
	private Option
	selectedOption;

	private ArrayList<Option>
	options;
	
}


