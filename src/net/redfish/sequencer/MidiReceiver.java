// MidiReceiver.java

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.swing.SwingUtilities;

// CLASS

public class MidiReceiver 
	implements Receiver
{
	// PUBLIC CONSTRUCTOR
	
	public
	MidiReceiver (MidiDevice inDevice, MidiHost inMidiHost)
	{
		System.err.println("MidiReceiver instantiated");
		System.err.println(inDevice);

		this.device = inDevice;
		this.midiHost = inMidiHost;
	}
	
	// RECEIVER IMPLEMENTATION
	
	public void
	close ()
	{
		// i have never known close() to mean anything
		// in the javax.sound.midi stuff
	}
	
	public void
	send (MidiMessage inMessage, long inTimeStamp)
	{
		if (inMessage instanceof ShortMessage)
		{
			ShortMessage	shortMessage = (ShortMessage) inMessage;
			
			// check the device and the channel match
			if (this.device == this.midiHost.getMidiInputDevice ())
			{
				// weirdness here
				// ShortMessage only deals with channel messages
				// so it ANDs off the lower nibble
				// and we have to use the raw data
				// I mean, LFS

				int	command = shortMessage.getMessage()[0];

				// stupid Java thinks that bytes are signed
				command &= 0xff;

				// system realtime messages
				if (command == ShortMessage.TIMING_CLOCK)
				{
					this.midiHost.sendMidiClock();
				}
				else if (command == ShortMessage.START)
				{
					this.midiHost.sendMidiStart();
				}
				else if (command == ShortMessage.CONTINUE)
				{
					this.midiHost.sendMidiContinue();
				}
				else if (command == ShortMessage.STOP)
				{
					this.midiHost.sendMidiStop();
				}
			}
		}
	}

	// PRIVATE METHODS
	
	private void
	handleNoteOff (ShortMessage inMessage)
	{
		this.midiHost.sendMidiNoteOff(inMessage.getData1 ());
	}
	
	private void
	handleNoteOn (ShortMessage inMessage)
	{
		if (inMessage.getData2 () == 0)
		{
			handleNoteOff(inMessage);
		}
		else
		{
			this.midiHost.sendMidiNoteOn(inMessage.getData1 (), inMessage.getData2 ());
		}
	}

	// PRIVATE DATA
	
	private MidiDevice
	device = null;

	private MidiHost
	midiHost = null;
	
}

