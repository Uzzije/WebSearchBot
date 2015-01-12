package application;

import java.util.ArrayList;

public class BotThread extends Thread
{
	private UrlPool urlPool;
	private boolean locked = false;
	
	public BotThread(UrlPool urlPool)
	{
		this.urlPool = urlPool;
	}
	
	public void run()
	{
		while (!isInterrupted()) {
			
			String url = urlPool.getNextUrlToCheck();
			
			while (null == url) {
				lock();

				System.out.println(getName() + " waiting for a link");
					
				while (locked) {
					if (isInterrupted()) {
						System.out.println(getName() + " interrupted on waiting a link");
						
						return;
					}
				}

				url = urlPool.getNextUrlToCheck();
			}

			urlPool.markUrlAsChecked(url);

			System.out.println(getName() + " accessing " + url);
			
			String content = Helpers.getUrlContents(url);
			
			while (locked) {
				if (isInterrupted()) {
					System.out.println(getName() + " interrupted after accesing an URL and being locked");
					
					return;
				}
			}
			
			if (isInterrupted()) {
				System.out.println(getName() + " interrupted after accesing an URL");
				
				urlPool.unmarkUrlAsChecked(url);
				
				return;
			}
		
			if (content.contains(App.getFrame().getKeyword())) {
				urlPool.addUrlAssFound(url);
			}

			ArrayList<String> urlsInContent = Helpers.getAllUrlsInString(content);
			
			if (0 < urlsInContent.size()) {
				urlPool.addUrlToCheck(Helpers.getAllUrlsInString(content));
				
				synchronized (this) {
					App.getLogic().unlock(urlsInContent.size());
				}
			}
		}
		
		System.out.println(getName() + " interrupted after the main thread method");
	}
	
	public synchronized boolean lock()
	{
		if (!isLocked()) {
			locked = true;
			
			return true;
		}
		
		return false;
	}
	
	public synchronized boolean unlock()
	{
		if (isLocked()) {
			locked = false;
			
			return true;
		}
		
		return false;
	}
	
	public synchronized boolean isLocked()
	{
		return locked;
	}
}
