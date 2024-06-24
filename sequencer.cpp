// sequencer.cpp

// system headers

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <dispatch/dispatch.h>

// language headers

#include <chrono>
#include <thread>
#include <vector>

// library headers

#include "MIDICLClient.h"
#include "MIDICLOutputPort.h"

// TIMER IMPLEMENTATION

#if !defined(MAC_OS_X_VERSION_10_12) || \
    (MAC_OS_X_VERSION_MIN_REQUIRED < MAC_OS_X_VERSION_10_12)
typedef int clockid_t;
#endif

struct itimerspec {
    struct timespec it_interval;    /* timer period */
    struct timespec it_value;       /* timer expiration */
};

struct sigevent;

/* If used a lot, queue should probably be outside of this struct */
struct macos_timer {
    dispatch_queue_t tim_queue;
    dispatch_source_t tim_timer;
    void (*tim_func)(union sigval);
    void *tim_arg;
};

typedef struct macos_timer *timer_t;

static inline void
_timer_cancel(void *arg)
{
    struct macos_timer *tim = (struct macos_timer *)arg;
    dispatch_release(tim->tim_timer);
    dispatch_release(tim->tim_queue);
    tim->tim_timer = NULL;
    tim->tim_queue = NULL;
    free(tim);
}

static inline void
_timer_handler(void *arg)
{
    struct macos_timer *tim = (struct macos_timer *)arg;
    union sigval sv;

    sv.sival_ptr = tim->tim_arg;

    if (tim->tim_func != NULL)
        tim->tim_func(sv);
}

static inline int
timer_create(clockid_t clockid, struct sigevent *sevp,
    timer_t *timerid)
{
    struct macos_timer *tim;

    *timerid = NULL;

    switch (clockid) {
        case CLOCK_REALTIME:

            /* What is implemented so far */
            if (sevp->sigev_notify != SIGEV_THREAD) {
                errno = ENOTSUP;
                return (-1);
            }

            tim = (struct macos_timer *)
                malloc(sizeof (struct macos_timer));
            if (tim == NULL) {
                errno = ENOMEM;
                return (-1);
            }

            tim->tim_queue =
                dispatch_queue_create("timerqueue",
                0);
            tim->tim_timer =
                dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER,
                0, 0, tim->tim_queue);

            tim->tim_func = sevp->sigev_notify_function;
            tim->tim_arg = sevp->sigev_value.sival_ptr;
            *timerid = tim;

            /* Opting to use pure C instead of Block versions */
            dispatch_set_context(tim->tim_timer, tim);
            dispatch_source_set_event_handler_f(tim->tim_timer,
                _timer_handler);
            dispatch_source_set_cancel_handler_f(tim->tim_timer,
                _timer_cancel);

            return (0);
        default:
            break;
    }

    errno = EINVAL;
    return (-1);
}

static inline int
timer_settime(timer_t tim, int flags,
    const struct itimerspec *its, struct itimerspec *remainvalue)
{
    if (tim != NULL) {

        /* Both zero, is disarm */
        if (its->it_value.tv_sec == 0 &&
            its->it_value.tv_nsec == 0) {
        /* There's a comment about suspend count in Apple docs */
            dispatch_suspend(tim->tim_timer);
            return (0);
        }

        dispatch_time_t start;
        start = dispatch_time(DISPATCH_TIME_NOW,
            NSEC_PER_SEC * its->it_value.tv_sec +
            its->it_value.tv_nsec);
        dispatch_source_set_timer(tim->tim_timer, start,
            NSEC_PER_SEC * its->it_value.tv_sec +
            its->it_value.tv_nsec,
            0);
        dispatch_resume(tim->tim_timer);
    }
    return (0);
}

static inline int
timer_delete(timer_t tim)
{
    /* Calls _timer_cancel() */
    if (tim != NULL)
        dispatch_source_cancel(tim->tim_timer);

    return (0);
}
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

		bool
		Play(MIDICLOutputPort *inOutputPort);
	
		void
		Stop();

		void
		SetBPM(float inBPM)
		{
			// this should update the clock tick ms too
		}

		Sequence &
		GetSequence()
		{
			return mSequence;
		}

		uint32_t		mTimerTicks;

	private:
	
		static void TimerTickProc(union sigval inValue);

		void TimerTick();

		MIDICLOutputPort	*mOutputPort;
		
		int								mStepNumber;
		Sequence					mSequence;
		
		// timer stuff
		
		timer_t						mTimer;
		struct sigevent		mEvent;
		struct itimerspec	mInterval;

};

Sequencer::Sequencer()
	:
	mStepNumber(0),
	mTimerTicks(0),
	mTimer(0)
{
}

bool
Sequencer::Play(MIDICLOutputPort *inOutputPort)
{
	mOutputPort = inOutputPort;

	int	result = 0;

	mSequence.SelectNoteOption(0);

	mTimer = 0;
	mTimerTicks = 0;

	mEvent.sigev_notify = SIGEV_THREAD;
	mEvent.sigev_value.sival_ptr = (void *)this;
	mEvent.sigev_notify_function = TimerTickProc;
	mEvent.sigev_notify_attributes = NULL;

	result = timer_create(CLOCK_REALTIME, &mEvent, &mTimer);

	if (result)
		return false;
	
	mInterval.it_value.tv_sec = 0;
	mInterval.it_value.tv_nsec = 20833333;

	result = timer_settime(mTimer, 0, &mInterval, NULL);

	if (result)
	{
		result = errno;
		timer_delete(mTimer);
		mTimer = 0;
		return result;
	}

	return true;
}

void
Sequencer::Stop()
{
	if (mTimer)
	{
		timer_delete(mTimer);
		mTimer = 0;
	}
}

void
Sequencer::TimerTickProc(union sigval inValue)
{
	Sequencer	*sequencer = (Sequencer *) inValue.sival_ptr;
	sequencer->TimerTick();
}

// this goes off at 24ppqn
// HACK hardwire steps to quaver length
void
Sequencer::TimerTick()
{
	uint32_t		stepNumber = mTimerTicks / 12;
	NoteOption	*selectedOption = mSequence.GetSelectedNoteOption(stepNumber);

	if (selectedOption)
	{
		uint32_t	subtick = mTimerTicks % (24 / 2);
	
		switch(subtick)
		{
			// new step
			case 0:
				printf("step %u firing (ratchet? %d)\n", stepNumber, selectedOption->mRatchet);
				mOutputPort->SendNoteOn(0, selectedOption->mNote, selectedOption->mVelocity);
				break;
			
			// if ratcheting then turn off note 1 here
			case 2:
				if (selectedOption->mRatchet)
					mOutputPort->SendNoteOff(0, selectedOption->mNote, selectedOption->mVelocity);
				break;
				
			// if ratcheting then turn on note 2 here
			case 3:
				if (selectedOption->mRatchet)
					mOutputPort->SendNoteOn(0, selectedOption->mNote, selectedOption->mVelocity);
				break;
				
			// if ratcheting then turn off note 2 here
			case 5:
				if (selectedOption->mRatchet)
					mOutputPort->SendNoteOff(0, selectedOption->mNote, selectedOption->mVelocity);
				break;
				
			// if ratcheting then turn on note 3 here
			case 6:
				if (selectedOption->mRatchet)
					mOutputPort->SendNoteOn(0, selectedOption->mNote, selectedOption->mVelocity);
				break;

			// if ratcheting then turn off note 3 here
			case 8:
				if (selectedOption->mRatchet)
					mOutputPort->SendNoteOff(0, selectedOption->mNote, selectedOption->mVelocity);
				break;
			
			// if ratcheting then turn on note 4 here
			case 9:
				if (selectedOption->mRatchet)
					mOutputPort->SendNoteOn(0, selectedOption->mNote, selectedOption->mVelocity);
				break;
				
			// if ratcheting then turn off note 4 here
			case 11:
				if (selectedOption->mRatchet)
					mOutputPort->SendNoteOff(0, selectedOption->mNote, selectedOption->mVelocity);
				
				// and this is a good spot to calculate the next step's option
				mSequence.SelectNoteOption((stepNumber + 1) % 16);
				break;
		}

		// now sort out whether to turn a non-ratchet note off
		if (!selectedOption->mRatchet)
		{
			if (subtick == selectedOption->mGateTime / 12)
				mOutputPort->SendNoteOff(0, selectedOption->mNote, selectedOption->mVelocity);
		}
	}

	mTimerTicks++;
	
	// HACK hardwire to 16 steps
	if (mTimerTicks / 12 == 16)
		mTimerTicks = 0;
}

int main(int argc, const char *argv[])
{
	Sequencer	sequencer;
	Sequence	&sequence(sequencer.GetSequence());

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

	MIDICLOutputPort	*outputPort(client.MakeOutputPort(CFSTR ("AssQuencer Output")));
	outputPort->SetDestination(destinationRef);

	// set up our sequence
	for (uint32_t stepNumber = 0; stepNumber < 16; stepNumber++)
	{
		Step	&step(sequence.GetStep(stepNumber));

		step.GetNoteOption(0).mNoteLower = 40;
		step.GetNoteOption(0).mNoteUpper = 60;
		step.GetNoteOption(0).mGateTimeLower = 70;
		step.GetNoteOption(0).mGateTimeUpper = 80;
		step.GetNoteOption(0).mProbability = 90;
	
		step.GetNoteOption(1).mNoteLower = 50;
		step.GetNoteOption(1).mNoteUpper = 50;
		step.GetNoteOption(1).mProbability = 25;
		step.GetNoteOption(1).mRatchetProbability = 100;
	}

	if (sequencer.Play(outputPort))
	{
		sleep(10);
	
		sequencer.Stop();
	
		printf("sequencer received %d timer ticks\n", sequencer.mTimerTicks);
	}
	else
	{
		printf("could not start sequencer\n");
	}
}


