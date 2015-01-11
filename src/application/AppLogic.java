package application;

import java.net.UnknownHostException;

public class AppLogic
{
	private UrlPool urlPool;
	
	public AppLogic()
	{
		urlPool = new UrlPool();
	}
	
	public void start()
	{		
		urlPool.addUrlToCheck(App.getFrame().getUrl());
		
		BotThread thread;
		
		for (int i = 0; i < App.getFrame().getThreadsNumber(); i++) {
			thread = new BotThread(urlPool);
			thread.start();
		}
	}
}
