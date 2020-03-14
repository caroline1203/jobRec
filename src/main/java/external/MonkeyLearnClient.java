package external;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.monkeylearn.ExtraParam;
import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;


public class MonkeyLearnClient {
	private static final String API_KEY = "0dccaa856d8df695be7f7989e89310dbc9ed4b40";
	
	public static void main(String[] args) {
		
		String[] textList = {
				"Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit’s full-body look.", };
		List<List<String>> words = extractKeywords(textList);
		for (List<String> ws : words) {
			for (String w : ws) {
				System.out.println(w);
			}
			System.out.println();
		}
	}

	public static List<List<String>> extractKeywords(String[] text) {
		// Use the API key from your account
		MonkeyLearn ml = new MonkeyLearn(API_KEY);

		// Use the keyword extractor
		ExtraParam[] extraParams = { new ExtraParam("max_keywords", "3") };
		MonkeyLearnResponse response;
		try {
			response = ml.extractors.extract("ex_YCya9nrn", text, extraParams);
			JSONArray resultArray = response.arrayResult;
			return getKeywords(resultArray);//支持batch processing, 批处理
		} catch (MonkeyLearnException e) {// it’s likely to have an exception
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	private static List<List<String>> getKeywords(JSONArray resultArray) {
		List<List<String>> topKeywords = new ArrayList<>();
		for (int i = 0; i < resultArray.size(); i++) {
			List<String> keywords = new ArrayList<>();
			JSONArray keywordsArray = (JSONArray) resultArray.get(i);
			for(int j = 0; j < keywordsArray.size(); j++) {
				JSONObject keywordObject = (JSONObject) keywordsArray.get(j);
				// We just need the keyword, excluding other fields.
				String keyword = (String) keywordObject.get("keyword");
				keywords.add(keyword);
			}
			topKeywords.add(keywords);
		}
		return topKeywords;
	}
}