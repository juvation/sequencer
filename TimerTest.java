// TimerTest.java

import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

// periods and delays are in ms

public class TimerTest
	extends TimerTask
{
	public static void main(String[] inArgs)
	{
		Timer	timer = new Timer();
	
		// 24ppqn at 60bpm = 1/24 second period
		// 24ppqn at 120bpm = 1/48 second period
		// 24ppqn at 180bpm = 1/72 second period
	
		// let's test at 180bpm
		double	periodFloat = (1.0 / 72.0) * 1000;
		long	period = Math.round(periodFloat);
	
		System.out.println("timer period is " + period + " (" + periodFloat + ")");

		// we give the timer to the task so it can stop it after a configurable number of ticks
		TimerTest	task = new TimerTest(timer, 100);
	
		timer.schedule(task, 0, period);
	
	}
	
	public TimerTest(Timer inTimer, int inMaxTicks)
	{
		mTimer = inTimer;
		mMaxTicks = inMaxTicks;

		mTickNumber = 0;
		mLastTickTimestamp = 0;
		mTickIntervals = new long[inMaxTicks];
	}

	public boolean cancel()
	{
		System.out.println("Task.cancel() called");

		return super.cancel();
	}

	public void run()
	{
		// long	tickTimestamp = Instant.now().toEpochMilli();
		long	tickTimestamp = System.currentTimeMillis();
		
		if (mLastTickTimestamp == 0)
		{
			mLastTickTimestamp = tickTimestamp;
		}
		else
		{
			mTickIntervals[mTickNumber++] = tickTimestamp - mLastTickTimestamp;
			mLastTickTimestamp = tickTimestamp;
		}

		if (mTickNumber == mMaxTicks)
		{
			System.out.println("ticks = " + mMaxTicks + ", cancelling timer");

			long	tickIntervalTotal = 0;
	
			for (int i = 0; i < mMaxTicks; i++)
			{
				System.out.println("tick interval [" + i + "] = " + mTickIntervals[i]);

				tickIntervalTotal += mTickIntervals[i];
			}
	
			long tickIntervalAverage = tickIntervalTotal / mMaxTicks;
	
			System.out.println("tick interval average = " + tickIntervalAverage);
	
			mTimer.cancel();
		}
	}

	private int		mMaxTicks;
	private int		mTickNumber;
	private long	mLastTickTimestamp;
	private long	mTickIntervals[];
	private Timer	mTimer;
	
}

