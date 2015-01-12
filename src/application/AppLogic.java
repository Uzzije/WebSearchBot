package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.Thread.State;

/**
 * Main application logic to control the threads and save the results.
 */
public class AppLogic
{
	private final int STOPPED = 0;
	private final int RUNNING = 1;
	private final int PAUSED = 2;
	
	private int state = STOPPED;
	private long executionTime;
	private int waitingThreads;
	private int current;
	private long startTime;
	private long pauseTime;
	private long pausedTime;
	
	/**
	 * URLs pool.
	 */
	private UrlPool urlPool;
	
	private Thread[] threads;
	
	private void main()
	{		
		if (STOPPED == state) {
			startTime = System.currentTimeMillis();
			pausedTime = 0;
			executionTime = 0;
			
			urlPool = new UrlPool();
			
			urlPool.addUrlToCheck(App.getFrame().getUrl());
			
			threads = new Thread[App.getFrame().getThreadsNumber()];
			
			for (int i = 0; i < App.getFrame().getThreadsNumber(); i++) {
				threads[i] = new Thread(new BotThread(urlPool));
				threads[i].setDaemon(true);
				threads[i].start();
			}
	
			current = 0;
			waitingThreads = 0;
		}
		
		state = RUNNING;
		
		while (RUNNING == state) {
			executionTime = System.currentTimeMillis() - startTime - pausedTime;
			
			if (executionTime >= App.getFrame().getMaxExecutionTime() * 1000) {
				break;
			}
			
			if (State.WAITING == threads[current].getState()) {
				waitingThreads++;
			} else {
				waitingThreads = 0;
			}
			
			if (waitingThreads == App.getFrame().getThreadsNumber()) {
				break;
			}
			
			if (current + 1 == App.getFrame().getThreadsNumber()) {
				current = 0;
			} else {
				current++;
			}
		}
		
		System.out.println("STOPPED");
		
		if (STOPPED == state) {
			stopAllThreads();
			
			System.out.println("Log");
			
			App.getFrame().log("Info: Search is finished\n\n" + getFormatedResult() + "\n");
		}
	}
	
	public int getState() {
		return state;
	}
	
	private void stopAllThreads()
	{
		for (Thread thread : threads) {
			thread.interrupt();
			
			System.out.println("Thread stop");
			
		}
	}
	
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
	
	public void start() {
		main();
	}
	
	public void pause() {
		state = PAUSED;
		
		for (int i = 0; i < threads.length; i++) {
			try {
				synchronized (threads[i]) {
					threads[i].wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		pauseTime = System.currentTimeMillis();
		
		App.getFrame().setStatus(
			String.format("Paused at %ds", (System.currentTimeMillis() - startTime - pausedTime) / 1000)
		);
	}
	
	public void stop() {
		state = STOPPED;
	}
	
	public void resume() {
		for (int i = 0; i < threads.length; i++) {
			synchronized (threads[i]) {
				threads[i].notify();
			}
		}
		
		pausedTime += System.currentTimeMillis() - pauseTime;
		
		main();
	}
	
	public void save(File file) {
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
}
