// Option.java
//
// Copyright 2026 Jason Proctor

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

// PUBLIC CLASS

public class Option
{
	public Option()
	{
		reset();
	}

	// this is stupid
	// java should know how to clone() properly
	public
	Option (Option inCopy)
	{
		copyFrom(inCopy);
	}

	// note we have to copy the computed fields
	// as we may be copying the selected option
	// after its computed fields are... computed
	public void
	copyFrom(Option inCopy)
	{
		this.gateTime = inCopy.gateTime;
		this.gateTimeLower = inCopy.gateTimeLower;
		this.gateTimeUpper = inCopy.gateTimeUpper;

		this.mute = inCopy.mute;
		this.muteProbability = inCopy.muteProbability;

		this.note = inCopy.note;
		this.noteLower = inCopy.noteLower;
		this.noteUpper = inCopy.noteUpper;

		this.probability = inCopy.probability;

		this.ratchet = inCopy.ratchet;
		this.ratchetProbability = inCopy.ratchetProbability;

		this.tie = inCopy.tie;
		this.tieProbability = inCopy.tieProbability;

		this.valid = inCopy.valid;

		this.velocity = inCopy.velocity;
		this.velocityLower = inCopy.velocityLower;
		this.velocityUpper = inCopy.velocityUpper;
	}

	public void
	reset()
	{
		this.gateTime = 50;
		this.gateTimeLower = 50;
		this.gateTimeUpper = 50;

		this.mute = false;
		this.muteProbability = 0;

		this.note = 60;
		this.noteLower = 60;
		this.noteUpper = 60;

		this.probability = 100;

		this.ratchet = false;
		this.ratchetProbability = 0;

		this.tie = false;
		this.tieProbability = 0;

		this.valid = false;

		this.velocity = 100;
		this.velocityLower = 100;
		this.velocityUpper = 100;
	}

	// PUBLIC DATA

	// computed
	public int
	gateTime;

	public int
	gateTimeLower;

	public int
	gateTimeUpper;

	// computed
	public boolean
	mute;

	public int
	muteProbability;

	// computed
	public int
	note;

	public int
	noteLower;

	public int
	noteUpper;

	public int
	probability;

	// computed
	public boolean
	ratchet;

	public int
	ratchetProbability;
	
	// computed
	public boolean
	tie;

	public int
	tieProbability;
	
	// used by the timer tick
	public boolean
	valid = false;

	// computed
	public int
	velocity;

	public int
	velocityLower;

	public int
	velocityUpper;

}


