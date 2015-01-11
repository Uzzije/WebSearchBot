package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Helpers
{
	public static String getUrlContents(String urlString) throws UnknownHostException
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
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
      
		return content.toString();
	}
	
	public static boolean isUrlValid(String urlString)
	{
		if (7 < urlString.length() && urlString.substring(0, 7).equals("http://")) {
			return true;
		}
		
		return false;
	}
	
	public static ArrayList<String> getAllUrlsInString(String string)
	{
		ArrayList<String> urls = new ArrayList<>();
		
		return urls;
	}
}
