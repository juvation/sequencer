// KeyboardView.java

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

public class KeyboardView
	extends JComponent
	implements MouseInputListener
{
	// PUBLIC CONSTRUCTOR
	
	public
	KeyboardView(boolean inIsUpper, int inStepNumber, int inOptionNumber)
	{
		this.isUpper = inIsUpper;
		this.stepNumber = inStepNumber;
		this.optionNumber = inOptionNumber;

		setLayout(null);
		
		// for key labels
		setFont(new Font("Sans Serif", Font.BOLD, 12));

		addMouseListener(this);
	}
	
	// COMPONENT INTERFACE
	
	// otherwise it won't show up in any managed layout, LFS
	// note the width is ignored in a width-managed layout
	public Dimension
	getPreferredSize()
	{
		return new Dimension(100, 200);
	}

	@Override
	public void
	paintComponent(Graphics inGraphics)
	{
		erase(inGraphics);
		draw(inGraphics);
	}
	
	// MOUSEINPUTLISTENER INTERFACE
	
	@Override
	public void
	mouseClicked(MouseEvent inEvent)
	{
		int	keyPressed = getMidiNoteNumber(inEvent);

		System.out.println("clicked key: " + keyPressed);

		if (this.isUpper)
		{
			SequencerWindow.getInstance().setOptionNoteUpper(this.stepNumber, this.optionNumber, keyPressed);
		}
		else
		{
			SequencerWindow.getInstance().setOptionNoteLower(this.stepNumber, this.optionNumber, keyPressed);
		}
	}
	
	public void
	mouseDragged(MouseEvent inEvent)
	{
	}

	public void
	mouseEntered(MouseEvent inEvent)
	{
	}
	
	public void
	mouseExited(MouseEvent inEvent)
	{
	}
	
	public void
	mouseMoved(MouseEvent inEvent)
	{
	}

	public void
	mousePressed(MouseEvent inEvent)
	{
	}
	
	public void
	mouseReleased(MouseEvent inEvent)
	{
	}

	// PUBLIC METHODS
	
	public int
	getOctaveOffset()
	{
		return (this.octaveOffset / 12) - 4;
	}
	
	public boolean
	setOctaveOffset(int inOctaveOffset)
	{
		boolean	ok = inOctaveOffset >= -3 && inOctaveOffset <= 3;
		
		if (ok)
		{
			this.octaveOffset = (inOctaveOffset + 4) * 12;
		}
		
		return ok;
	}
	
	public void
	setOption(int inStepNumber, int inOptionNumber)
	{
		System.out.println("KeyboardView.setOption() to step " + inStepNumber + " option " + inOptionNumber);

		this.stepNumber = inStepNumber;
		this.optionNumber = inOptionNumber;
	}

	// PRIVATE METHODS
	
	private void
	draw(Graphics inGraphics)
	{
		Rectangle	bounds = getBounds();
		
		int	keyWidth = 0;
		this.numWhiteKeys = 0;
		
		for (int octave = 1; ; octave++)
		{
			this.numWhiteKeys = (octave * 7) + 1;
			keyWidth = bounds.width / this.numWhiteKeys;
			
			if (keyWidth < 50)
			{
				break;
			}
		}

		FontMetrics	metrics = inGraphics.getFontMetrics();

		for (int key = 0; key < numWhiteKeys; key++)
		{
			inGraphics.drawRect(key * keyWidth, 0, keyWidth, bounds.height - 1);

			// label the key
			// note we have a white key number, not a MIDI note
			// so hack one into the other

			// we could do this with key / 7 * 12
			// but it falls over mapping D# to F!
			int	note = 0;

			switch (key % 7)
			{
				// C
				case 0:
					note = 0;
					break;

				// D
				case 1:
					// map C# to D
					note = 2;
					break;

				// E
				case 2:
					// map D to E
					note = 4;
					break;

				// F
				case 3:
					// map D# to F
					note = 5;
					break;
				
				// G
				case 4:
					// map E to G
					note = 7;
					break;
	
				// A
				case 5:
					// map F to A
					note = 9;
					break;
				
				// B
				case 6:
					// map F# to B
					note = 11;
					break;

				default:
					System.out.println("oi, unrecognised key " + key);
					break;
			}

			// now map the octaves
			note += (key / 7) * 12;

			int			noteNumber = this.octaveOffset + note;
			String	noteString = SequencerWindow.getNoteString(noteNumber);
			int			noteStringWidth = metrics.stringWidth(noteString);

			inGraphics.drawString(noteString, key * keyWidth + ((keyWidth / 2) - (noteStringWidth / 2)), bounds.height - 20);
		}

		// for each octave
		int	numOctaves = numWhiteKeys / 7;
		
		for (int i = 0; i < numOctaves; i++)
		{
			int	octaveBase = i * (7 * keyWidth);
			
			// ASSUME black keys are half the width of white ones
			// and two thirds of the length
			
			for (int j = 0; j < this.blackKeyIndexes.length; j++)
			{
				int	x = octaveBase + ((blackKeyIndexes [j] + 1) * keyWidth) - (keyWidth / 4);
				
				Rectangle	blackKey = new Rectangle(x, 0, keyWidth / 2, (bounds.height * 2) / 3);
				
				inGraphics.fillRect(blackKey.x, blackKey.y, blackKey.width, blackKey.height);
			}
		}
	}
	
	private void
	erase(Graphics inGraphics)
	{
		inGraphics.setColor(getBackground());
		inGraphics.fillRect(0, 0, getSize().width, getSize().height);
		inGraphics.setColor(getForeground());
	}
	
	private int
	getMidiNoteNumber(MouseEvent inEvent)
	{
		int	mouseX = inEvent.getX();
		int	mouseY = inEvent.getY();
		
		Rectangle	bounds = getBounds();
		
		// calculate the octave from the key
		// not the other way round
		// because error from the integer arithmetic accumulates
		
		int	keyWidth = bounds.width / this.numWhiteKeys;
		int	whiteKey = mouseX / keyWidth;
		int	octave = whiteKey / 7;		
		whiteKey -= (octave * 7);
		
		// for the final key number
		// start with the white key value
		int	midiKey = (octave * 12) + this.whiteKeyOffsets [whiteKey];

		// if the click is above the black key area
		if (mouseY <(bounds.height * 2) / 3)
		{
			int	keyStart = ((octave * 7) + whiteKey) * keyWidth;

			// try for a black key starting here
			if (whiteKey == 0 || whiteKey == 1 || whiteKey == 3 || whiteKey == 4 || whiteKey == 5)
			{
				int	blackKeyStart = keyStart + ((keyWidth * 3) / 4);
				
				if (mouseX >= blackKeyStart)
				{
					// huzzah we have a black key press
					midiKey++;
				}
			}

			// try for a black key ending here
			if (whiteKey == 1 || whiteKey == 2 || whiteKey == 4 || whiteKey == 5 || whiteKey == 6)
			{
				if (mouseX <= keyStart + (keyWidth / 4))
				{
					// huzzah we have a black key press
					midiKey--;
				}
			}
		}
		
		return midiKey + this.octaveOffset;
	}

	// PRIVATE DATA
	
	private boolean
	isUpper;

	private int
	stepNumber;
	
	private int
	optionNumber;
	
	private int
	numWhiteKeys = 0;
	
	private int
	octaveOffset = 48;
	
	private int[]
	blackKeyIndexes = {0, 1, 3, 4, 5};

	private int[]
	whiteKeyOffsets = {0, 2, 4, 5, 7, 9, 11};
	
}

