package application;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * The main class to store and provide the URLs for the bot.
 */
public class UrlPool
{
	/**
	 * All checked URL list.
	 */
	private ArrayList<String> checkedUrls;
	
	/**
	 * URL list where the keywords was found.
	 */
	private ArrayList<String> foundUrls;
	
	/**
	 * URL of currently processing addresses.
	 */
	private ArrayList<String> processingUrls;
	
	/**
	 * URL queue to add new found links and take the links for BotThead.
	 */
	private LinkedList<String> urlQueue;
	
	/**
	 * A lock to control threads working with UrlPool.
	 */
	private Lock lock;

	/**
	 * A condition to define an empty URL pool.
	 */
	private Condition notEmpty;
	
	/**
	 * Class constructor.
	 */
	public UrlPool(Lock lock)
	{
		checkedUrls = new ArrayList<>();
		foundUrls = new ArrayList<>();
		processingUrls = new ArrayList<>();
		urlQueue = new LinkedList<>();
		
		this.lock = lock;
		notEmpty = lock.newCondition();
	}
	
	/**
	 * Get next URL to check.
	 * 
	 * @return the next URL to check or null in case of empty queue
	 * @throws InterruptedException 
	 */
	public String getNextUrlToCheck() throws InterruptedException
	{
		lock.lock();
		
		String url = urlQueue.pollFirst();
		
		while (null == url) {
			
			notEmpty.await();
			
			url = urlQueue.pollFirst();
		}
		
		processingUrls.add(url);
		
		lock.unlock();
		
		return url;
	}
	
	/**
	 * Add URL to the queue if it has not be check yet.
	 * 
	 * @param url to add
	 */
	public void addUrlToCheck(String url)
	{
		lock.lock();
		
		if (!checkedUrls.contains(url) && !urlQueue.contains(url) && !processingUrls.contains(url)) {
			urlQueue.addLast(url);
			
			notEmpty.signal();
		}
		
		lock.unlock();
	}
	
	/**
	 * Add URL list to the queue if it has not be check yet.
	 * 
	 * @param urls list to add
	 */
	public void addUrlToCheck(ArrayList<String> urls)
	{
		for (String url : urls) {
			addUrlToCheck(url);
		}
	}
	
	/**
	 * Add URL to found list.
	 * 
	 * @param url to add
	 */
	public void markUrlAssFound(String url)
	{
		lock.lock();
		
		if (false == foundUrls.contains(url)) {
			foundUrls.add(url);
		} else {
			System.out.println("-------> Trying to add URL to found list, but it is already there.");
		}
		
		lock.unlock();
	}
	
	/**
	 * Mark URL as checked.
	 * 
	 * @param url to mark
	 */
	public void markUrlAsChecked(String url)
	{
		lock.lock();
		
		if (false == checkedUrls.contains(url)) {
			checkedUrls.add(url);
			processingUrls.remove(url);
		} else {
			System.out.println("-------> Trying to add URL to checked list, but it is already there.");
		}
		
		lock.unlock();
	}
	
	/**
	 * Get total number of checked URLs.
	 * 
	 * @return total checked
	 */
	public int getTotalCheckedUrl()
	{
		return checkedUrls.size();
	}

	/**
	 * Get total number URLs where the keyword was found.
	 * 
	 * @return total found
	 */
	public int getTotalFoundUrl()
	{
		return foundUrls.size();
	}
	
	/**
	 * Get a list of URLs where the keyword was found.
	 * 
	 * @return found URLs
	 */
	public ArrayList<String> getFoundUrls()
	{
		return foundUrls;
	}
}
