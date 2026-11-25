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
			if (this.device == this.midiHost.getMidiClockDevice ())
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
					this.midiHost.onMidiClock();
				}
				else if (command == ShortMessage.START)
				{
					this.midiHost.onMidiStart();
				}
				else if (command == ShortMessage.CONTINUE)
				{
					this.midiHost.onMidiContinue();
				}
				else if (command == ShortMessage.STOP)
				{
					this.midiHost.onMidiStop();
				}
			}
			else if (this.device == this.midiHost.getMidiInputDevice ())
			{
				if (shortMessage.getCommand() == ShortMessage.NOTE_ON)
				{
					if (shortMessage.getData2() == 0)
					{
						this.midiHost.onMidiNoteOff(shortMessage.getData1(), 0);
					}
					else
					{
						this.midiHost.onMidiNoteOn(shortMessage.getData1(), shortMessage.getData2());
					}
				}
				else if (shortMessage.getCommand() == ShortMessage.NOTE_OFF)
				{
					this.midiHost.onMidiNoteOff(shortMessage.getData1(), shortMessage.getData2());
				}
			}
		}
	}

	// PRIVATE DATA
	
	private MidiDevice
	device = null;

	private MidiHost
	midiHost = null;
	
}

