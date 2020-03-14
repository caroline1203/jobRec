package rpc;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import database.MySQLConnection;
import entity.Item;

/**
 * Servlet implementation class FavouriteItem
 */
public class FavouriteItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FavouriteItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		
		String userId = request.getParameter("user_id");
		
		MySQLConnection connection = new MySQLConnection();
		Set<Item> items = connection.getFavoriteItems(userId);
		connection.close();

		JSONArray array = new JSONArray();
		for (Item item : items) {
			JSONObject obj = item.toJSONObject();
			obj.put("favorite", true);
			array.put(obj);
		}
		RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// parse request body, get item and user information
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		
		JSONObject object = RpcHelper.readJSONObject(request);
		String userId = object.getString("user_id");
		Item item = RpcHelper.parseFavoriteItem(object.getJSONObject("favorite"));
		
		// call setfavourite
		MySQLConnection connection = new MySQLConnection();
		connection.setFavoriteItems(userId, item);
		connection.close();
		
		//return ok
		RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// parse request body, get item and user information
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		
		JSONObject object = RpcHelper.readJSONObject(request);
		String userId = object.getString("user_id");
		Item item = RpcHelper.parseFavoriteItem(object.getJSONObject("favorite"));
		
		// call unsetfavourite
		MySQLConnection connection = new MySQLConnection();
		connection.unsetFavoriteItems(userId, item.getItemId());
		connection.close();
		
		//return ok
		RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
	}
	

}
