package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import database.MySQLConnection;
import entity.Item;
import external.GitHubClient;

/**
 * Servlet implementation class SearchIterm
 */
public class SearchIterm extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchIterm() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//如果想和request互动，可以从request提取参数
//		if(request.getParameter("username") != null) {
//			String username = request.getParameter("username");
//			JSONObject obj = new JSONObject();
//			try {
//				obj.put("username", "xiaoben");
//			}catch(JSONException e){
//				e.printStackTrace();
//			}
//			RpcHelper.writeJsonObject(response, obj);
//		}
//		JSONArray array = new JSONArray();
//		try {
//			array.put(new JSONObject().put("username", "xiaoben"));
//			array.put(new JSONObject().put("username", "daben"));
//		}catch (JSONException e) {
//			e.printStackTrace();
//		}
		
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		
		String userId = request.getParameter("user_id");
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		
		GitHubClient client = new GitHubClient();
		List<Item> items = client.search(lat, lon, null);
		
		MySQLConnection connection = new MySQLConnection();
		Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
		connection.close();
		
		JSONArray array = new JSONArray();
		for(Item job : items) {
			JSONObject obj = job.toJSONObject();
			obj.put("favorite", favoritedItemIds.contains(job.getItemId()));
			array.put(obj);
		}
		RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		doGet(request, response);
	}

}
