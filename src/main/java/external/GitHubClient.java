package external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

public class GitHubClient {
	private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
	private static final String DEFAULT_KEYWORD = "developer";
	
	public List<Item> search(double lat, double lon, String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try{
			keyword = URLEncoder.encode(keyword, "UTF-8");//" " to "20%"
		}catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String url = String.format(URL_TEMPLATE, keyword, lat, lon);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CloseableHttpResponse response = httpClient.execute(new HttpGet(url));
			if (response.getStatusLine().getStatusCode() != 200) {
				return new ArrayList<>();
			}
			HttpEntity entity = response.getEntity();//read response body
			if(entity == null) {
				return new ArrayList<>();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));//getContent 是个 stream data;InputStreamReader 可以一个一个读， 或者一段一段读（需要给定长度）； buffer比较智能
			StringBuilder responseBody = new StringBuilder();
			String line = new String();
			while((line = reader.readLine()) != null) {
				responseBody.append(line);
			}
			JSONArray array = new JSONArray(responseBody.toString());
			return getItemList(array);
		}catch(ClientProtocolException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	private List<Item> getItemList(JSONArray array) {
		List<Item> itemList = new ArrayList<>();
		List<String> descriptionList = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			
			builder.setItemId(getStringFieldOrEmpty(object, "id"));
			builder.setName(getStringFieldOrEmpty(object, "title"));
			builder.setAddress(getStringFieldOrEmpty(object, "location"));
			builder.setUrl(getStringFieldOrEmpty(object, "url"));
			builder.setImageUrl(getStringFieldOrEmpty(object, "company_logo"));
			
			// We need to extract categories from description since GitHub API
			// doesn't return keywords.
			
			if (object.getString("description").equals("\n")) {
				descriptionList.add(object.getString("title"));
			} else {
				descriptionList.add(object.getString("description"));
			}
			

			Item item = builder.build();
			itemList.add(item);
		}
		String[] descriptionArray = descriptionList.toArray(new String[descriptionList.size()]);
		List<List<String>> keywords = MonkeyLearnClient.extractKeywords(descriptionArray);
		for (int i = 0; i < keywords.size(); ++i) {
			List<String> list = keywords.get(i);
			Set<String> set = new HashSet<String>(list);
			itemList.get(i).setKeywords(set);
		}
		
		// We need to get keywords from multiple text in one request since
		// MonkeyLearnAPI has a limitation on request per minute.
		
		return itemList;
	}
	
	private String getStringFieldOrEmpty(JSONObject obj, String field) {
		return obj.isNull(field) ? "" : obj.getString(field);
	}


	public static void main(String[] args) {
		GitHubClient client = new GitHubClient();
		List<Item> jobs = client.search(37.38, -122.08, null);
		for (Item job : jobs) {
			JSONObject jsonObject = job.toJSONObject();
			System.out.println(jsonObject);
		}
	}

}

