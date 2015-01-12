package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Methods-helpers for search application.
 */
public class Helpers
{
	/**
	 * Get page content by provided URL address.
	 * 
	 * @param urlString to fetch the source
	 * @return page source
	 */
	public static String getUrlContents(String urlString)
	{
		StringBuilder content = new StringBuilder();
		
		try {
			URL url = new URL(urlString);
	
			URLConnection urlConnection = url.openConnection();
	 
			// Hardcoded encoding
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
	 
			String line;
	 
			while ((line = bufferedReader.readLine()) != null) {
				content.append(line + "\n");
			}
			
			bufferedReader.close();
		} catch (Exception e) {}
      
		return content.toString();
	}
	
	/**
	 * Validate URL.
	 * 
	 * @param url to validate
	 * @return is URL valid
	 */
	public static boolean isUrlValid(String url)
	{	
		if (7 < url.length() && url.matches(getValidUrlRegex())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Get a regular expression to check URL validity.
	 * @see <a href="https://mathiasbynens.be/demo/url-regex">https://mathiasbynens.be/demo/url-regex</a>
	 * 
	 * @return regex
	 */
	private static String getValidUrlRegex()
	{
		return "https?://[^\\s/$.?#].[^\\s\"\']*";
	}
	
	/**
	 * Search URLs in provided string.
	 * 
	 * @param string to search in
	 * @return urls list found in string
	 */
	public static ArrayList<String> getAllUrlsInString(String string)
	{
		ArrayList<String> urls = new ArrayList<>();
		
        Pattern pattern = Pattern.compile(getValidUrlRegex());
        Matcher matcher = pattern.matcher(string);
        
		while (matcher.find()) {
			urls.add(matcher.group());
		}
		
		return urls;
	}
}
