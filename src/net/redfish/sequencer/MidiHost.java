// MidiHost.java
//
// Copyright 2026 Jason Proctor

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Transmitter;

// CLASS

// changed "send" to "on"
// as this is usually used for reception
// entities can send shit easily without a MidiHost

public interface MidiHost
{
	// PUBLIC ABSTRACT METHODS
	
	public abstract int
	getMidiChannel0 ();
	
	public abstract MidiDevice
	getMidiClockDevice ();
	
	public abstract MidiDevice
	getMidiInputDevice ();
	
	public abstract MidiDevice
	getMidiOutputDevice ();
	
	public abstract void
	onMidiChannelPressure (int inPressure);
	
	public abstract void
	onMidiClock ();

	public abstract void
	onMidiContinue ();

	public abstract void
	onMidiControlChange (int inControlNumber, int inControlValue);
	
	public abstract void
	onMidiNoteOff (int inNoteNumber, int inVelocity);
	
	public abstract void
	onMidiNoteOn (int inNoteNumber, int inVelocity);
	
	public abstract void
	onMidiPitchBend (int inLSB, int inMSB);
	
	public abstract void
	onMidiProgramChange (int inPatchNumber);

	public abstract void
	onMidiStart ();

	public abstract void
	onMidiStop ();

}

