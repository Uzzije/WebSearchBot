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
	private static final int STOPED = 0;
	private static final int RUNNING = 1;
	private static final int PAUSED = 2;
	
	private int state = 0;
	private long executionTime;
	
	/**
	 * URLs pool.
	 */
	private UrlPool urlPool;
	
	Thread[] threads;
	
	private void main()
	{		
		long startTime = System.currentTimeMillis();
		executionTime = 0;
		
		urlPool = new UrlPool();
		
		urlPool.addUrlToCheck(App.getFrame().getUrl());
		
		threads = new Thread[App.getFrame().getThreadsNumber()];
		
		for (int i = 0; i < App.getFrame().getThreadsNumber(); i++) {
			threads[i] = new Thread(new BotThread(urlPool));
			threads[i].setDaemon(true);
			threads[i].start();
		}

		int i = 0;
		int waitingThreads = 0;
		
		while (RUNNING == state) {
			executionTime = System.currentTimeMillis() - startTime;
			
			if (executionTime >= App.getFrame().getMaxExecutionTime() * 1000) {
				break;
			}
			
			if (State.WAITING == threads[i].getState()) {
				waitingThreads++;
			} else {
				waitingThreads = 0;
			}
			
			if (waitingThreads == App.getFrame().getThreadsNumber()) {
				break;
			}
			
			if (i + 1 == App.getFrame().getThreadsNumber()) {
				i = 0;
			} else {
				i++;
			}
		}
		
		stopAllBotThreads();
		
		App.getFrame().log("Info: Search is finished\n\n" + getFormatedResult() + "\n");
	}
	
	private void stopAllBotThreads()
	{
		for (Thread thread : threads) {
			thread.stop();
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
		state = RUNNING;
		
		main();
	}
	
	public void pause() {
		state = PAUSED;
	}
	
	public void stop() {
		state = STOPED;
	}
	
	public void save(File file) {
		PrintWriter writer;
		
		try {
			App.getFrame().setStatus("Saving in " + file.getAbsolutePath());
			
			writer = new PrintWriter(file);
			
			writer.println(getFormatedResult());
			writer.println();
			
			for (String url : urlPool.getFoundUrls()) {
				writer.println(url);
			}
			
			writer.close();
			
			App.getFrame().setStatus("Result saved in " + file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
