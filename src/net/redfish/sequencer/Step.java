// Step.java

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

import java.util.ArrayList;

// PUBLIC CLASS

public class Step
{
	// PUBLIC CONSTRUCTOR

	public Step()
	{
		this.selectedOption = null;
		this.noteOptions = new ArrayList<Option>(2);
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
		return noteOptions.get(inOptionNumber);
	}

	public void
	selectOption()
	{
		this.selectedOption = null;

		int	roll = SequencerWindow.getInstance().getRandomInt();
		int	target = 0;

		for (Option option : this.noteOptions)
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
			return;
		}

		// TODO determine the option probability options

/*
		mSelectedOption->mMute = Utility::SelectProbability(mSelectedOption->mMuteProbability);
		mSelectedOption->mTie = Utility::SelectProbability(mSelectedOption->mTieProbability);
		
		mSelectedOption->mGateTime = Utility::SelectValue<int8_t>
			(mSelectedOption->mGateTimeLower, mSelectedOption->mGateTimeUpper);
		
		mSelectedOption->mNote = Utility::SelectValue<uint8_t>
			(mSelectedOption->mNoteLower, mSelectedOption->mNoteUpper);
		
		mSelectedOption->mVelocity = Utility::SelectValue<uint8_t>
			(mSelectedOption->mVelocityLower, mSelectedOption->mVelocityUpper);
		
		// wait, this way we only have one type of ratchet
		// unless there is a ratchet granularity property
		mSelectedOption->mRatchet = Utility::SelectProbability(mSelectedOption->mRatchetProbability);
*/

	}

	
	// PRIVATE DATA
	
	private Option
	selectedOption;

	private ArrayList<Option>
	noteOptions;
	
}


