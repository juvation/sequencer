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

	// PUBLIC DATA
	
	public int			probability = 100;

	public int			note = 60;
	public int			noteLower = 60;
	public int			noteUpper = 60;

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


