package application;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Thread to process the URL fetching.
 */
public class BotThread extends Thread
{
	/**
	 * URL pool.
	 */
	private UrlPool urlPool;
	
	/**
	 * A lock to control threads.
	 */
	private Lock lock;
	
	/**
	 * Threads are not paused condition.
	 */
	private Condition notPaused;
	
	/**
	 * Class constructor.
	 */
	public BotThread(UrlPool urlPool, Lock lock, Condition notPaused)
	{
		this.urlPool = urlPool;
		this.lock = lock;
		this.notPaused = notPaused;
	}
	
	/**
	 * The main thread logic.
	 */
	public void run()
	{
		// Do while the thread is not interrupted
		while (!isInterrupted()) {
			
			String url;
			
			try {
				url = urlPool.getNextUrlToCheck();
			} catch (InterruptedException e1) {
				System.out.println(getName() + " interrupted on waiting a link");
				
				return;
			}
			
			try {
				checkIfPause();
			} catch (InterruptedException e) {
				System.out.println(getName() + " interrupted on pause");
				
				return;
			}

			System.out.println(getName() + " accessing " + url);

			String content = Helpers.getUrlContents(url);
			
			// Check if the thread stopped (interrupted)
			if (isInterrupted()) {
				System.out.println(getName() + " interrupted after accesing an URL");
				
				return;
			}
			
			try {
				checkIfPause();
			} catch (InterruptedException e) {
				System.out.println(getName() + " interrupted on pause");
				
				return;
			}
		
			// Does the page contain the keyword?
			if (content.contains(App.getFrame().getKeyword())) {
				urlPool.markUrlAssFound(url);
			}
			
			urlPool.markUrlAsChecked(url);

			ArrayList<String> urlsInContent = Helpers.getAllUrlsInString(content);
			
			if (0 < urlsInContent.size()) {
				urlPool.addUrlToCheck(Helpers.getAllUrlsInString(content));
			}
		}
		
		System.out.println(getName() + " interrupted after the main thread method");
	}
	
	/**
	 * Checks if the thread was paused.
	 * 
	 * @throws InterruptedException 
	 */
	private void checkIfPause() throws InterruptedException
	{
		while (App.getLogic().isPaused()) {
			lock.lock();
			
			try {
				notPaused.await();

			} finally {
				lock.unlock();
			}
		}
	}
}
