package application;

import java.util.ArrayList;

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
	 * Is the the thread locked and should wait.
	 */
	private boolean locked = false;
	
	/**
	 * Is the the thread locked and should be finished.
	 */
	private boolean interrupted = false;
	
	/**
	 * Class constructor.
	 */
	public BotThread(UrlPool urlPool)
	{
		this.urlPool = urlPool;
	}
	
	/**
	 * The main thread logic.
	 */
	public void run()
	{
		// Do while the thread is not interrupted
		while (!isInterrupted()) {
			
			String url = urlPool.getNextUrlToCheck();
			
			// There are no URL to process yet, so let's wait
			while (null == url) {
				lock();

				System.out.println(getName() + " waiting for a link");
					
				while (isLocked()) {
					if (isInterrupted()) {
						System.out.println(getName() + " interrupted on waiting a link");
						
						return;
					}
				}

				url = urlPool.getNextUrlToCheck();
			}

			// Lock the URL as being checked (still processing)
			urlPool.markUrlAsChecked(url);

			System.out.println(getName() + " accessing " + url);

			String content = Helpers.getUrlContents(url);
			
			// Check if the thread was not locked
			while (isLocked()) {
				if (isInterrupted()) {
					System.out.println(getName() + " interrupted after accesing an URL and being locked");
					
					urlPool.unmarkUrlAsChecked(url);
					
					return;
				}
			}
			
			// Check if the thread stopped (interrupted)
			if (isInterrupted()) {
				System.out.println(getName() + " interrupted after accesing an URL");
				
				urlPool.unmarkUrlAsChecked(url);
				
				return;
			}
		
			// Does the page contain the keyword?
			if (content.contains(App.getFrame().getKeyword())) {
				urlPool.addUrlAssFound(url);
			}

			ArrayList<String> urlsInContent = Helpers.getAllUrlsInString(content);
			
			if (0 < urlsInContent.size()) {
				urlPool.addUrlToCheck(Helpers.getAllUrlsInString(content));
				
				// Notify and unlock threads that there are new URLs to process
				App.getLogic().unlock(urlsInContent.size());
			}
		}
		
		System.out.println(getName() + " interrupted after the main thread method");
	}
	
	/**
	 * Lock the thread.
	 * 
	 * @return if the lock status changed
	 */
	public synchronized boolean lock()
	{
		if (!locked) {
			locked = true;
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Unlock the thread.
	 * 
	 * @return if the lock status changed
	 */
	public synchronized boolean unlock()
	{
		if (locked) {
			locked = false;
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Check is the thread locked.
	 * 
	 * @return is locked
	 */
	public synchronized boolean isLocked()
	{
		return locked;
	}
	
	/**
	 * Check is the thread interrupted.
	 * 
	 * @return is interrupted
	 */
	public synchronized boolean isInterrupted()
	{
		return interrupted;
	}
	
	/**
	 * Interrupt (finish) the thread.
	 */
	public synchronized void interrupt()
	{
		interrupted = true;
	}
}
