package application;

import java.net.UnknownHostException;
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
		while (true) {
			String url = urlPool.getNextUrlToCheck();
			
			while (null == url) {
				try {
					synchronized(App.getLogic()) {
						App.getLogic().wait();
					}
				} catch (InterruptedException e) {}

				url = urlPool.getNextUrlToCheck();
			}

			String content = Helpers.getUrlContents(url);
		
			if (content.contains(App.getFrame().getKeyword())) {
				urlPool.addUrlAssFound(url);
			}

			urlPool.markUrlAsChecked(url);

			ArrayList<String> urlsInContent = Helpers.getAllUrlsInString(content);
			
			if (0 < urlsInContent.size()) {
				urlPool.addUrlToCheck(Helpers.getAllUrlsInString(content));
				
				synchronized(App.getLogic()) {
					App.getLogic().notifyAll();
				}
			}
		}
	}
}
