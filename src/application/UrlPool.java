package application;

import java.util.ArrayList;
import java.util.LinkedList;

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
	 * URL queue to add new found links and take the links for BotThead.
	 */
	private LinkedList<String> urlQueue;
	
	/**
	 * Class constructor.
	 */
	public UrlPool()
	{
		checkedUrls = new ArrayList<>();
		foundUrls = new ArrayList<>();
		urlQueue = new LinkedList<>();
	}
	
	/**
	 * Get next URL to check.
	 * 
	 * @return the next URL to check or null in case of empty queue
	 */
	public String getNextUrlToCheck()
	{
		return urlQueue.pollFirst();
	}
	
	/**
	 * Add URL to the queue if it has not be check yet.
	 * 
	 * @param url URL to add
	 */
	public void addUrlToCheck(String url)
	{
		if (false == checkedUrls.contains(url) && false == urlQueue.contains(url)) {
			urlQueue.addLast(url);
		}
	}
	
	/**
	 * Add URL list to the queue if it has not be check yet.
	 * 
	 * @param urls URL list to add
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
	 * @param url URL to add
	 */
	public void addUrlAssFound(String url)
	{
		if (false == foundUrls.contains(url)) {
			foundUrls.add(url);
		} else {
			System.out.println("Trying to add URL to found list, but it is already there.");
		}
	}
	
	/**
	 * Mark URL as checked.
	 * 
	 * @param url URL to mark
	 */
	public void markUrlAsChecked(String url) {
		if (false == checkedUrls.contains(url)) {
			checkedUrls.add(url);
		}
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
