// sequencer.cpp

// system headers

#include <stdio.h>
#include <stdlib.h>

// language headers

#include <chrono>
#include <thread>
#include <vector>

// library headers

#include "MIDICLClient.h"
#include "MIDICLOutputPort.h"

// UTILITY LOL

class Utility
{
	public:

		static bool
		SelectProbability(uint8_t inProbability);

		template<typename T> static
		T SelectValue(const T &inLower, const T &inUpper);

};

bool Utility::SelectProbability(uint8_t inProbability)
{
	uint8_t	retval = 0;

	if (inProbability == 0)
	{
		retval = false;
	}
	else if (inProbability == 100)
	{
		retval = true;
	}
	else
	{
		retval = (random() % 100) < inProbability;
	}

	return retval;
}

template<typename T>
T Utility::SelectValue(const T &inLower, const T &inUpper)
{
	T	retval = 0;

// fprintf(stderr, "select value from %d to %d\n", inLower, inUpper);

	if (inLower == inUpper)
	{
		retval = inLower;
	}
	else
	{
		int32_t	range = inUpper - inLower;
		range = abs(range);

		// random() gives 0..range so
		range++;

// fprintf(stderr, "range is %d\n", range);

		T	roll = (T) (random() % range);
		retval = inLower + roll;
	}

	return retval;
}

// NOTE OPTION

struct NoteOption
{
	// we need a constructor

	NoteOption()
		:
		mProbability(100),
		mNote(64),
		mNoteLower(64),
		mNoteUpper(64),
		mVelocity(100),
		mVelocityLower(100),
		mVelocityUpper(100),
		mGateTime(50),
		mGateTimeLower(50),
		mGateTimeUpper(50),
		mRatchet(false),
		mRatchetProbability(0),
		mMute(false),
		mMuteProbability(0),
		mTie(false),
		mTieProbability(0)
	{
	}

	uint8_t	mProbability;

	uint8_t	mNote;
	uint8_t	mNoteLower;
	uint8_t	mNoteUpper;

	uint8_t	mVelocity;
	uint8_t	mVelocityLower;
	uint8_t	mVelocityUpper;

	// percent of step length
	int8_t	mGateTime;
	int8_t	mGateTimeLower;
	int8_t	mGateTimeUpper;

	bool	mRatchet;
	uint8_t	mRatchetProbability;
	
	bool	mMute;
	uint8_t	mMuteProbability;

	bool	mTie;
	uint8_t	mTieProbability;

};

// STEP

class Step
{
	public:

		Step()
			:
			mSelectedOption(nullptr),
			mNoteOptions(2)
		{
		}

		NoteOption *
		GetSelectedNoteOption()
		{
			return mSelectedOption;
		}

		void
		SelectNoteOption();

		NoteOption &
		GetNoteOption(uint32_t inOptionNumber)
		{
			return mNoteOptions[inOptionNumber];
		}

		const NoteOption &
		GetNoteOption(uint32_t inOptionNumber) const
		{
			return mNoteOptions[inOptionNumber];
		}
		
	private:

		NoteOption								*mSelectedOption;
		std::vector<NoteOption>		mNoteOptions;
};

void
Step::SelectNoteOption()
{
	// how much validation here do we really need to do?
	// remember that it's possible that we don't select an option at all

	mSelectedOption = nullptr;

	uint8_t	roll = (uint8_t) (random() % 100);
	uint8_t	target = 0;

	for (NoteOption &noteOption : mNoteOptions)
	{
		target += noteOption.mProbability;

		if (roll < target)
		{
			mSelectedOption = &noteOption;
			break;
		}
	}

	// this is entirely possible and accepted
	if (mSelectedOption == nullptr)
		return;

	// determine the property values

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
}

// SEQUENCE

class Sequence
{
	public:
	
		Sequence()
			:
			mSteps(16)
		{
		}

		NoteOption *
		GetSelectedNoteOption(uint32_t inStepNumber)
		{
			return mSteps[inStepNumber].GetSelectedNoteOption();
		}

		// this sets mSelectedOption in the step
		void
		SelectNoteOption(uint32_t inStepNumber);

		Step &GetStep(uint32_t inStepNumber)
		{
			return mSteps[inStepNumber];
		}

		const Step &GetStep(uint32_t inStepNumber) const
		{
			return mSteps[inStepNumber];
		}

	private:
	
		std::vector<Step>	mSteps;
};

void
Sequence::SelectNoteOption(uint32_t inStepNumber)
{
	GetStep(inStepNumber).SelectNoteOption();
}

// SEQUENCER

class Sequencer
{
	public:
	
		Sequencer();

		void
		Play();
	
		void
		SetBPM(float inBPM)
		{
			// this should update the clock tick ms too
		}

		Sequence &
		GetSequence(uint32_t inSequenceNumber)
		{
			return mSequences[inSequenceNumber];
		}

		void
		SetSequence(uint32_t inSequenceNumber, Sequence &inSequence)
		{
			mSequences[inSequenceNumber] = inSequence;
		}

	private:
	
		std::vector<Sequence>	mSequences;

};

void
Sequencer::Play()
{
}

#if 0

// the problem with doing stuff on off-clocks is that we might not have any!
// if the division is set to 24ppqn etc

// the time is the time of the next step, which is likely in the future
// note this should be called after any step division action
onClockTick(time)
{
	uint32_t	stepNumber = DetermineStep(time);

	// send the messages we already decided on
	sendStepMessages(stepNumber, time);

	uint32_t	nextStepNumber = (stepNumber + 1) % stepCount;

	updateAccumulator(nextStepNumber);
	selectControlOption(nextStepNumber);
	selectNoteOption(nextStepNumber);
}

#endif

int main(int argc, const char *argv[])
{
	Sequence	sequence;

	srandom(time(nullptr));

	MIDICLClient	client(CFSTR("AssQuencer"));

	int	numDestinations = MIDIGetNumberOfDestinations ();

	printf("found %d destinations\n", numDestinations);

	// 2 = 303
	// 8 = Pro-3
	// for the USB hosts it depends on what order they are powered up LFS
	int	destinationNumber = 8;
	MIDIEndpointRef	destinationRef = MIDIGetDestination(destinationNumber);

	CFStringRef	property = nullptr;
	char	displayName [64];
	displayName[0] = 0;

	if (MIDIObjectGetStringProperty(MIDIGetDestination(destinationNumber), kMIDIPropertyDisplayName, &property) == noErr)
	{
		CFStringGetCString (property, displayName, sizeof (displayName), 0);
		CFRelease (property);
	}

	printf("selecting destination %d (%s)\n", 8, displayName);

	MIDICLOutputPort	*outputPort(client.MakeOutputPort (CFSTR ("AssQuencer Output")));
	outputPort->SetDestination (destinationRef);

	// set up our sequence
	for (uint32_t stepNumber = 0; stepNumber < 16; stepNumber++)
	{
		Step	&step(sequence.GetStep(stepNumber));

		step.GetNoteOption(0).mNoteLower = 40;
		step.GetNoteOption(0).mNoteUpper = 60;
		step.GetNoteOption(0).mProbability = 75;
	
		step.GetNoteOption(1).mNoteLower = 50;
		step.GetNoteOption(1).mNoteUpper = 50;
		step.GetNoteOption(1).mProbability = 25;
		step.GetNoteOption(1).mRatchetProbability = 100;
	}

	// before we start, select the first note option
	sequence.SelectNoteOption(0);

	// we wait sync for a quaver length LOL
	for (uint32_t stepNumber = 0; stepNumber < 16; stepNumber++)
	{
		uint32_t	waitTime = 0;

		NoteOption	*selectedOption = sequence.GetSelectedNoteOption(stepNumber);

		if (selectedOption)
		{
			if (selectedOption->mRatchet)
			{
				// chrono doesn't understand doubles
				// so we use 7x31ms + 33ms
				auto	sleepPeriod = std::chrono::milliseconds(31);

				// ratchet 1
				outputPort->SendNoteOn(0, selectedOption->mNote, selectedOption->mVelocity);
				std::this_thread::sleep_for(sleepPeriod);
				outputPort->SendNoteOff(0, selectedOption->mNote, selectedOption->mVelocity);
				std::this_thread::sleep_for(sleepPeriod);

				// ratchet 2
				outputPort->SendNoteOn(0, selectedOption->mNote, selectedOption->mVelocity);
				std::this_thread::sleep_for(sleepPeriod);
				outputPort->SendNoteOff(0, selectedOption->mNote, selectedOption->mVelocity);
				std::this_thread::sleep_for(sleepPeriod);
				
				// ratchet 3
				outputPort->SendNoteOn(0, selectedOption->mNote, selectedOption->mVelocity);
				std::this_thread::sleep_for(sleepPeriod);
				outputPort->SendNoteOff(0, selectedOption->mNote, selectedOption->mVelocity);
				std::this_thread::sleep_for(sleepPeriod);

				// ratchet 4
				outputPort->SendNoteOn(0, selectedOption->mNote, selectedOption->mVelocity);
				std::this_thread::sleep_for(sleepPeriod);
				outputPort->SendNoteOff(0, selectedOption->mNote, selectedOption->mVelocity);
				
				std::this_thread::sleep_for(std::chrono::milliseconds(33));
			}
			else
			{
				outputPort->SendNoteOn(0, selectedOption->mNote, selectedOption->mVelocity);
				std::this_thread::sleep_for(std::chrono::milliseconds(125));
				outputPort->SendNoteOff(0, selectedOption->mNote, selectedOption->mVelocity);

				waitTime = 125;
			}
		}
		else
		{
			waitTime = 250;
		}

		// calculate the next step option
		sequence.SelectNoteOption((stepNumber + 1) % 16);
		
		// wait for it...
		std::this_thread::sleep_for(std::chrono::milliseconds(waitTime));
	}
}

/*


If we have 120bpm
then 1 crotchet is 0.5 second = 500ms
then 1 quaver is 0.25 second = 250ms
and a 2 step (total) ratchet is 125ms
and a 4 step (total) ratchet is 62.5ms

A whole quaver is 250ms
In total we have 4 notes per quaver for a ratchet
the MIDI note ons are 62.5ms apart
which means for 50% gate time the notes are 31.75ms long



*/

