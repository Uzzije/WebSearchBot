package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Main application logic to control the threads and save the results.
 */
public class AppLogic
{
	/*
	 * Application states.
	 */
	private final int STOPPED = 0;
	private final int RUNNING = 1;
	private final int PAUSED = 2;
	
	/**
	 * Current application state.
	 */
	private int state = STOPPED;
	
	/**
	 * Total execution time.
	 */
	private long executionTime;
	
	/**
	 * Waiting threads amount.
	 */
	private int waitingThreads;
	
	/**
	 * Current thread for checking.
	 */
	private int current;
	
	/**
	 * Application start time stamp.
	 */
	private long startTime;
	
	/**
	 * Application pause time stamp.
	 */
	private long pauseTime;
	
	/**
	 * Total paused time.
	 */
	private long pausedTime;
	
	/**
	 * URLs pool.
	 */
	private UrlPool urlPool;
	
	/**
	 * Active threads.
	 */
	private BotThread[] threads;
	
	/**
	 * A lock to control threads.
	 */
	private final Lock lock;
	
	/**
	 * Threads are not paused condition.
	 */
	private Condition notPaused;
	
	/**
	 * Class constructor.
	 */
	public AppLogic()
	{
		lock = new ReentrantLock();
	}
	
	/**
	 * Main application method to control the threads.
	 */
	private void main()
	{
		// Creates and runs the threads first time or after the search was stopped/finished
		if (STOPPED == state) {
			startTime = System.currentTimeMillis();
			pausedTime = 0;
			executionTime = 0;
			
			urlPool = new UrlPool(lock);
			
			urlPool.addUrlToCheck(App.getFrame().getUrl());
			
			threads = new BotThread[App.getFrame().getThreadsNumber()];
			
			notPaused = lock.newCondition();
			
			for (int i = 0; i < threads.length; i++) {
				threads[i] = new BotThread(urlPool, lock, notPaused);
				threads[i].setDaemon(true);
				
				System.out.println(threads[i].getName() + " created");
			}
			
			for (BotThread thread : threads) {
				thread.start();
			}
	
			current = 0;
			waitingThreads = 0;
		}
		
		state = RUNNING;
		
		// Main loop to check if the threads have finished the work
		while (RUNNING == state) {
			executionTime = System.currentTimeMillis() - startTime - pausedTime;
			
			if (executionTime >= App.getFrame().getMaxExecutionTime() * 1000) {
				state = STOPPED;
				
				break;
			}
			
			if (Thread.State.WAITING == threads[current].getState()) {
				waitingThreads++;
			} else {
				waitingThreads = 0;
			}
			
			if (waitingThreads == threads.length) {
				state = STOPPED;
				
				break;
			}
			
			if (current + 1 == threads.length) {
				current = 0;
			} else {
				current++;
			}
		}
		
		// Check is it stopped or paused
		if (STOPPED == state) {
			System.out.println("Stoppping.");
			
			App.getFrame().setButtonEnabled(AppFrame.BUTTON_START, true);
			App.getFrame().setButtonEnabled(AppFrame.BUTTON_RESUME, false);
			App.getFrame().setButtonEnabled(AppFrame.BUTTON_PAUSE, false);
			App.getFrame().setButtonEnabled(AppFrame.BUTTON_STOP, false);
			App.getFrame().setButtonEnabled(AppFrame.BUTTON_SAVE_RESULT, true);
			
			App.getFrame().setStatus("Search finished or stopped");
			
			stopAllThreads();
			
			App.getFrame().log("Info: Search is finished\n\n" + getFormatedResult() + "\n");
		} else if (PAUSED == state) {
			System.out.println("Pausing.");
			
			App.getFrame().setButtonEnabled(AppFrame.BUTTON_START, false);
			App.getFrame().setButtonEnabled(AppFrame.BUTTON_RESUME, true);
			App.getFrame().setButtonEnabled(AppFrame.BUTTON_PAUSE, false);
			App.getFrame().setButtonEnabled(AppFrame.BUTTON_STOP, true);
			App.getFrame().setButtonEnabled(AppFrame.BUTTON_SAVE_RESULT, true);

			pauseTime = System.currentTimeMillis();
		} else {
			System.out.println("Main logic loop has unknown state instead of STOPPED or PAUSED");
		}
	}
	
	/**
	 * Stop all threads (interrupt).
	 */
	public void stopAllThreads()
	{
		// For keeping references because it may be changed after start button is pressed
		BotThread[] threads = this.threads;
		
		for (BotThread thread : threads) {
			thread.interrupt();
			
			System.out.println(thread.getName() + " stopped");
		}
	}
	
	/**
	 * Get state of the application logic.
	 * 
	 * @return state
	 */
	public int getState() {
		return state;
	}
	
	/**
	 * Get formated result for the output log in GUI.
	 * 
	 * @return formated result
	 */
	private String getFormatedResult()
	{
		return String.format(
			"Keyword \"%s\" with primary URL %s\n"
			+ "Execution time %d/%ds using %d threads\n"
			+ "The keyword found in %d URLs out of %d total checked",
			App.getFrame().getKeyword(),
			App.getFrame().getUrl(),
			executionTime / 1000,
			App.getFrame().getMaxExecutionTime(),
			App.getFrame().getThreadsNumber(),
			urlPool.getTotalFoundUrl(),
			urlPool.getTotalCheckedUrl()
		);
	}
	
	/**
	 * Start the main thread controlling loop.
	 */
	public void start()
	{
		state = STOPPED;
		
		main();
	}
	
	/**
	 * Pause the main thread controlling loop.
	 */
	public void pause()
	{
		state = PAUSED;
		
		App.getFrame().setStatus(
			String.format("Paused at %ds", (System.currentTimeMillis() - startTime - pausedTime) / 1000)
		);
	}
	
	/**
	 * Stop the main thread controlling loop.
	 */
	public void stop()
	{
		state = STOPPED;
		
		stopAllThreads();
	}
	
	/**
	 * Resume the main thread controlling loop.
	 */
	public void resume()
	{
		try {
			lock.lock();
			
			notPaused.signalAll();
		} finally {
			lock.unlock();
		}
		
		pausedTime += System.currentTimeMillis() - pauseTime;
		
		main();
	}
	
	/**
	 * Save the result.
	 * 
	 * @param file to save in
	 */
	public void save(File file)
	{
		PrintWriter writer;
		
		try {
			App.getFrame().setStatus("Saving in " + file.getAbsolutePath());
			App.getFrame().log("Info: Saving in " + file.getAbsolutePath());
			
			writer = new PrintWriter(file);
			
			writer.println(getFormatedResult());
			writer.println();
			
			for (String url : urlPool.getFoundUrls()) {
				writer.println(url);
			}
			
			writer.close();
			
			App.getFrame().setStatus("Result saved in " + file.getAbsolutePath());
			App.getFrame().log("Info: Saved in " + file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Is search paused?
	 * 
	 * @return is paused
	 */
	public boolean isPaused()
	{
		return PAUSED == state;
	}
}
