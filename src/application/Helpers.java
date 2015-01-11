package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helpers
{
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
		} catch (UnknownHostException e) {
			
		} catch (MalformedURLException e) {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
      
		return content.toString();
	}
	
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
