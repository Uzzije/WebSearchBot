package application;

import java.util.ArrayList;

public class BotThread extends Thread
{
	UrlPool urlPool;
	
	public BotThread(UrlPool urlPool)
	{
		this.urlPool = urlPool;
	}
	
	public void run()
	{
		while (!isInterrupted()) {
			System.out.println("Thread waiting for link");
			
			String url = urlPool.getNextUrlToCheck();
			
			while (null == url) {
				try {
					synchronized (App.getFrame()) {
						App.getFrame().wait();
					}
				} catch (InterruptedException e) {
					System.out.println("Thread interrupted on waiting");
					
					return;
				}

				url = urlPool.getNextUrlToCheck();
			}

			urlPool.markUrlAsChecked(url);

			System.out.println("Thread accessing " + url);
			
			String content = Helpers.getUrlContents(url);
			
			if (isInterrupted()) {
				System.out.println("Thread interrupted after accesing an URL");
				
				urlPool.unmarkUrlAsChecked(url);
				
				return;
			}
		
			if (content.contains(App.getFrame().getKeyword())) {
				urlPool.addUrlAssFound(url);
			}

			ArrayList<String> urlsInContent = Helpers.getAllUrlsInString(content);
			
			if (0 < urlsInContent.size()) {
				urlPool.addUrlToCheck(Helpers.getAllUrlsInString(content));
				
				synchronized (App.getFrame()) {
					App.getFrame().notifyAll();
				}
			}
		}
		
		System.out.println("Thread interrupted after the main thread method");
	}
	
	public void pause()
	{
		try {
			synchronized (App.getLogic()) {
				System.out.println("Waiting");
				App.getLogic().wait();
				System.out.println("Resumed");
			}
		} catch (InterruptedException e) {}
	}
}
