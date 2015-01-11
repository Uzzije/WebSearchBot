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
			
			if (null == url) {
				// Check if there is any working BotThread
				break;
			}
			
			try {
				String content = Helpers.getUrlContents(url);
				
				if (content.contains(App.getFrame().getKeyword())) {
					urlPool.addUrlAssFound(url);
				}
	
				urlPool.markUrlAsChecked(url);
				
				ArrayList<String> urlsInContent = null;
				urlPool.addUrlToCheck(urlsInContent);
			} catch (UnknownHostException e) {
				App.getFrame().log("Error: Cannot connect to the Internet!");
			}
		}
	}
}
