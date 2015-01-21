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
			
			String url;
			
			try {
				url = urlPool.getNextUrlToCheck();
			} catch (InterruptedException e1) {
				System.out.println(getName() + " interrupted on waiting a link");
				
				return;
			}

			System.out.println(getName() + " accessing " + url);

			String content = Helpers.getUrlContents(url);
			
			// Check if the thread stopped (interrupted)
			if (isInterrupted()) {
				System.out.println(getName() + " interrupted after accesing an URL");
				
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
}
