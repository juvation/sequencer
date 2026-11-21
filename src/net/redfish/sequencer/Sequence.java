// Sequencer.java

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hm.mono.OurJSONStringifier;

// PUBLIC CLASS

public class Sequence
{

	// PUBLIC CONSTRUCTOR
	
	public Sequence(int inStepCount, int inOptionCount)
	{
		this.stepCount = inStepCount;
		this.optionCount = inOptionCount;

		this.steps = new ArrayList<Step>();

		for (int i = 0; i < inStepCount; i++)
		{
			this.steps.add(new Step(inOptionCount));
		}
	}

	// PUBLIC METHODS

	public Option
	getSelectedOption(int inStepNumber)
	{
		return this.steps.get(inStepNumber).getSelectedOption();
	}

	public Step
	getStep(int inStepNumber)
	{
		return this.steps.get(inStepNumber);
	}

	public boolean
	isModified()
	{
		return this.modified;
	}

	public void
	selectOption(int inStepNumber)
	{
		System.out.println("select option for step " + inStepNumber);

		getStep(inStepNumber).selectOption();
	}

	public void
	setModified(boolean inModified)
	{
		this.modified = inModified;
	}

	public void
	write(OutputStream outStream)
	throws IOException
	{
		Map<String, Object>	sequenceMap = new HashMap<String, Object>();

		List<Object>	stepsList = new ArrayList<Object>();
		sequenceMap.put("steps", stepsList);

		for (int stepNumber = 0; stepNumber < this.stepCount; stepNumber++)
		{
			Step	step = getStep(stepNumber);

			Map<String, Object>	stepMap = new HashMap<String, Object>();
			stepsList.add(stepMap);

			List<Object>	optionsList = new ArrayList<Object>();
			stepMap.put("options", optionsList);

			for (int optionNumber = 0; optionNumber < this.optionCount; optionNumber++)
			{
				Option	option = step.getOption(optionNumber);

				Map<String, Object>	optionMap = new HashMap<String, Object>();

				optionMap.put("probability", option.probability);

				optionMap.put("muteProbability", option.muteProbability);
				optionMap.put("tieProbability", option.tieProbability);
				optionMap.put("ratchetProbability", option.ratchetProbability);

				optionMap.put("noteLower", option.noteLower);
				optionMap.put("noteUpper", option.noteUpper);

				optionMap.put("velocityLower", option.velocityLower);
				optionMap.put("velocityUpper", option.velocityUpper);

				optionMap.put("gateTimeLower", option.gateTimeLower);
				optionMap.put("gateTimeUpper", option.gateTimeUpper);
				
				optionsList.add(optionMap);				
			}
		}

		OutputStreamWriter	osw = new OutputStreamWriter(outStream);
	
		try
		{
			// careful here, the static stringifier method doesn't allow tarting up
			OurJSONStringifier	stringifier = new OurJSONStringifier();
			stringifier.setTartUp(true);
			
			stringifier.stringifyObject(sequenceMap, osw, 2);
		}
		finally
		{
			osw.close();
		}
	}

	// PRIVATE DATA
	
	private boolean
	modified = false;

	private int
	optionCount = 0;

	private int
	stepCount = 0;

	private ArrayList<Step>
	steps;
}

