// Sequencer.java
//
// Copyright 2026 Jason Proctor

// PACKAGE

package net.redfish.sequencer;

// IMPORTS

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import hm.mono.JSON;
import hm.mono.MapWrapper;
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
	read(InputStream inStream)
	throws IOException, NoSuchElementException
	{
		Map<String, Object>	sequenceMap = JSON.parse(inStream);
		Object							stepsObject = sequenceMap.get("steps");

		if (stepsObject == null || !(stepsObject instanceof List))
		{
			throw new NoSuchElementException();
		}
		
		List<Object>	stepsList = (List<Object>) stepsObject;

		int	stepsListCount = stepsList.size();

		if (stepsListCount > this.stepCount)
		{
			System.err.println("steps list too big, clipping to " + this.stepCount);
			stepsListCount = this.stepCount;
		}

		for (int stepNumber = 0; stepNumber < stepsListCount; stepNumber++)
		{
			Object	stepObject = stepsList.get(stepNumber);

			if (!(stepObject instanceof Map))
			{
				throw new NoSuchElementException("step object is not a Map");
			}

			Map<String, Object>	stepMap = (Map<String, Object>) stepObject;
			Object							optionsObject = stepMap.get("options");

			if (optionsObject == null || !(optionsObject instanceof List))
			{
				throw new NoSuchElementException();
			}

			List<Object>	optionsList = (List<Object>) optionsObject;

			int	optionsListCount = optionsList.size();

			if (optionsListCount > this.optionCount)
			{
				System.err.println("options list too big, clipping to " + this.optionCount);
				optionsListCount = this.optionCount;
			}

			for (int optionNumber = 0; optionNumber < optionsListCount; optionNumber++)
			{
				Object	optionObject = optionsList.get(optionNumber);

				if (!(optionObject instanceof Map))
				{
					throw new NoSuchElementException("option object is not a Map");
				}

				Map<String, Object>	optionMap = (Map<String, Object>) optionObject;
				MapWrapper	optionWrapper = new MapWrapper(optionMap);

				Option	option = getStep(stepNumber).getOption(optionNumber);

				option.probability = optionWrapper.getIntThrow("probability");
				option.probability = (int) Math.max(option.probability, 0);
				option.probability = (int) Math.min(option.probability, 100);

				option.muteProbability = optionWrapper.getIntThrow("muteProbability");
				option.muteProbability = (int) Math.max(option.muteProbability, 0);
				option.muteProbability = (int) Math.min(option.muteProbability, 100);

				option.tieProbability = optionWrapper.getIntThrow("tieProbability");
				option.tieProbability = (int) Math.max(option.tieProbability, 0);
				option.tieProbability = (int) Math.min(option.tieProbability, 100);

				option.ratchetProbability = optionWrapper.getIntThrow("ratchetProbability");
				option.ratchetProbability = (int) Math.max(option.ratchetProbability, 0);
				option.ratchetProbability = (int) Math.min(option.ratchetProbability, 100);

				option.noteLower = optionWrapper.getIntThrow("noteLower");
				option.noteLower = (int) Math.max(option.noteLower, 0);
				option.noteLower = (int) Math.min(option.noteLower, 127);

				option.noteUpper = optionWrapper.getIntThrow("noteUpper");
				option.noteUpper = (int) Math.max(option.noteUpper, 0);
				option.noteUpper = (int) Math.min(option.noteUpper, 127);
			}
		}
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
	setOption(int inStepNumber, int inOptionNumber, Option inOption)
	{
		getStep(inStepNumber).setOption(inOptionNumber, inOption);
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

