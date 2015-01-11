package application;

import java.lang.Thread.State;

/**
 * Main application logic to control the threads and save the results.
 */
public class AppLogic
{
	/**
	 * URLs pool.
	 */
	private UrlPool urlPool;
	
	BotThread[] threads;
	
	/**
	 * Class constructor.
	 */
	public AppLogic()
	{
		urlPool = new UrlPool();
	}
	
	public void start()
	{
		long startTime = System.currentTimeMillis();
		long executionTime;
		
		urlPool.addUrlToCheck(App.getFrame().getUrl());
		
		threads = new BotThread[App.getFrame().getThreadsNumber()];
		
		for (int i = 0; i < App.getFrame().getThreadsNumber(); i++) {
			threads[i] = new BotThread(urlPool);
			threads[i].start();
		}

		int i = 0;
		int waitingThreads = 0;
		
		while (true) {
			executionTime = System.currentTimeMillis() - startTime;
			
//			System.out.println(executionTime);
//			System.out.println(App.getFrame().getMaxExecutionTime() * 1000);
//			System.out.println();
			
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
		
		interuptAllBotThreads();
		
		App.getFrame().log(String.format(
			"Info: Search is finished\n\n"
			+ "Keyword \"%s\" with primary URL %s\n"
			+ "Execution time %d/%d using %d threads\n"
			+ "The keyword found in %d URLs out of %d total checked",
			App.getFrame().getKeyword(),
			App.getFrame().getUrl(),
			executionTime / 1000,
			App.getFrame().getMaxExecutionTime(),
			App.getFrame().getThreadsNumber(),
			urlPool.getTotalFoundUrl(),
			urlPool.getTotalCheckedUrl()
		));
	}
	
	private void interuptAllBotThreads()
	{
		for (BotThread thread : threads) {
			thread.interrupt();
		}
	}
}
