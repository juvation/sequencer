// Option.java

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

// PUBLIC CLASS

public class Option
{
	public Option()
	{
		this.probability = 100;

		this.note = 64;
		this.noteLower = 64;
		this.noteUpper = 64;

		this.velocity = 100;
		this.velocityLower = 100;
		this.velocityUpper = 100;

		this.gateTime = 50;
		this.gateTimeLower = 50;
		this.gateTimeUpper = 50;

		this.ratchet = false;
		this.ratchetProbability = 0;

		this.mute = false;
		this.muteProbability = 0;

		this.tie = false;
		this.tieProbability = 0;
	}

	// PUBLIC DATA
	
	public int			probability = 100;

	public int			note = 64;
	public int			noteLower = 64;
	public int			noteUpper = 64;

	public int			velocity = 100;
	public int			velocityLower = 100;
	public int			velocityUpper = 100;

	// percent of step length
	public int			gateTime = 50;
	public int			gateTimeLower = 50;
	public int			gateTimeUpper = 50;

	public boolean	ratchet = false;
	public int			ratchetProbability = 0;
	
	public boolean	mute = false;
	public int			muteProbability = 0;

	public boolean	tie = false;
	public int			tieProbability = 0;
	
}


