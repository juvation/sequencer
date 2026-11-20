// Option.java

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

// PUBLIC CLASS

public class Option
{
	public Option()
	{
	}

	// this is stupid
	// java should know how to clone() properly
	public
	Option (Option inCopy)
	{
		this.gateTimeLower = inCopy.gateTimeLower;
		this.gateTimeUpper = inCopy.gateTimeUpper;

		this.muteProbability = inCopy.muteProbability;

		this.noteLower = inCopy.noteLower;
		this.noteUpper = inCopy.noteUpper;

		this.probability = inCopy.probability;

		this.ratchetProbability = inCopy.ratchetProbability;

		this.tieProbability = inCopy.tieProbability;

		this.velocityLower = inCopy.velocityLower;
		this.velocityUpper = inCopy.velocityUpper;
	}

	// PUBLIC DATA

	// computed
	public int
	gateTime = 50;

	public int
	gateTimeLower = 50;

	public int
	gateTimeUpper = 50;

	// computed
	public boolean
	mute = false;

	public int
	muteProbability = 0;

	// computed
	public int
	note = 60;

	public int
	noteLower = 60;

	public int
	noteUpper = 60;

	public int
	probability = 100;

	// computed
	public boolean
	ratchet = false;

	public int
	ratchetProbability = 0;
	
	// computed
	public boolean
	tie = false;

	public int
	tieProbability = 0;
	
	// computed
	public int
	velocity = 100;

	public int
	velocityLower = 100;

	public int
	velocityUpper = 100;

}


