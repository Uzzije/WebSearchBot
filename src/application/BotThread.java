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
				System.out.println(getName() + " waiting for a link");
				
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
					System.out.println(getName() + " interrupted on waiting a link");
					
					return;
				}

				url = urlPool.getNextUrlToCheck();
			}

			// Lock the URL as being checked (still processing)
			urlPool.markUrlAsChecked(url);

			System.out.println(getName() + " accessing " + url);

			String content = Helpers.getUrlContents(url);
			
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
				App.getLogic().notify(urlsInContent.size());
			}
		}
		
		System.out.println(getName() + " interrupted after the main thread method");
	}
}
