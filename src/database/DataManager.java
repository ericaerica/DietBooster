package database;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import model.UserBean;

public class DataManager {
	//TODO riordinare i metodi e controllare che per ogni save ci sia un get e viceversa
	private static Connection connection = null;
	
	private static void setConnection(Connection con) {
		connection = con;
	}
	
	/**
	 * This method connects the system with the DB.
	 * 
	 * @return If it was able to connect, returns true, otherwise false.
	 */
	public static boolean connect() {
		boolean result = false;
		try {// Try driver
			Class.forName("org.postgresql.Driver");
			try {// Try connection
				setConnection(DriverManager.getConnection(
						"jdbc:postgresql://alcor.inf.unibz.it:5432/RSC",
						"etomaselli", "uniDradcliffe1!"));
				result = true;
			} catch (SQLException e) {
				System.err.println("Connection Failed! Check output console");
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			System.err.println("MISSING PostgreSQL JDBC Driver");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * This method closes the connection with the DB.
	 * 
	 * @return If it succeeds returns true, otherwise false.
	 */
	public static boolean disconnect() {
		boolean result = false;

		try {
			connection.close();
			result = true;
		} catch (SQLException e) {
			System.err.println("Couldn't close connection");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param user
	 * @return True if the user exists in the DB, false if it doesn't
	 */
	public static boolean userExists(UserBean user) {
		boolean opResult = false;

		Statement st1 = null;
		ResultSet rs = null;

		if (connection != null) {
			try {
				st1 = connection.createStatement();
				rs = st1.executeQuery("SELECT * FROM userbean WHERE userbean.email = "
						+ "'" + user.getEmail() + "'" + ";");
				if (rs.next()) {
					opResult = true;
					//System.out.println(rs.getString(1));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return opResult;
	}
	
	//TODO javadoc
	public static boolean saveUser(UserBean user) {
		boolean opResult = false;
		if (connection != null) {
			try {
				if (userExists(user)) {
					Statement st = connection.createStatement();
					st.executeUpdate("UPDATE userbean SET " + "email=" + "'"
							+ user.getEmail() + "'" + "," + "username=" + "'"
							+ user.getUsername() + "'" + "," + "password="
							+ "'" + user.getPassword() + "'" + "," + "gender="
							+ "'" + user.getGender() + "'" + "," + "age="
							+ user.getAge() + "," + "height="
							+ user.getHeight() + "," + "weight="
							+ user.getWeight() + "," + "waist="
							+ user.getWaist() + "WHERE userbean.email=" + "'"
							+ user.getEmail() + "'" + ";");
					ResultSet rs = st.executeQuery("SELECT id FROM userbean WHERE userbean.email=" 
							+ "'" + user.getEmail() + "'" + ";");
					if(rs.next())
						user.setId(rs.getInt(1));
					opResult = true;
					st.close();
				} else {
					System.out.println("INSERTING");
					Statement st = connection.createStatement();
					st.executeUpdate("INSERT INTO userbean VALUES (" + "'"
							+ user.getEmail() + "'" + "," + "'"
							+ user.getUsername() + "'" + "," + "'"
							+ user.getPassword() + "'" + "," + "'"
							+ user.getGender() + "'" + "," + user.getAge()
							+ "," + user.getHeight() + "," + user.getWeight()
							+ "," + user.getWaist() + ");");
					ResultSet rs = st.executeQuery("SELECT id FROM userbean WHERE userbean.email=" 
							+ "'" + user.getEmail() + "'" + ";");
					if(rs.next())
						user.setId(rs.getInt(1));
					opResult = true;
					st.close();
				}
			} catch (SQLException e) {
				System.err.println("ERROR in the query!");
				e.printStackTrace();
			}

		}
		return opResult;
	}

	//TODO javadoc
	public static UserBean getUser(String username, String password) {
		UserBean user = null;
		Statement st = null;
		ResultSet rs = null;

		if (connection != null) {
			try {
				st = connection.createStatement();
				String query = "SELECT * " + "FROM userbean "
						+ "WHERE userbean.username=" + "'" + username + "'"
						+ " AND userbean.password = " + "'" + password + "'"
						+ ';';
				rs = st.executeQuery(query);

				if (rs.next()) {
					user = new UserBean();
					user.setEmail(rs.getString(1));
					user.setUsername(rs.getString(2));
					user.setPassword(rs.getString(3));
					user.setGender(rs.getString(4));
					user.setAge(rs.getInt(5));
					user.setHeight(rs.getDouble(6));
					user.setWeight(rs.getDouble(7));
					user.setWaist(rs.getDouble(8));
					user.setId(rs.getInt(9));
				}
				st.close();

			} catch (SQLException e) {
				System.err.println("ERROR in the query!");
				e.printStackTrace();
			}

		}

		return user;
	}
	
	/**
	 * This methods makes a query to the database to obtain a list of food descriptions that contain the partial name
	 * inserted by the user.
	 * 
	 * @param partialName	the partial name of the food searched by the user
	 * @return	ArrayList<String>	list of food descriptions
	 */
	public static ArrayList<String> getFood(String partialName){
		ArrayList<String> foodList = new ArrayList<String>();
		Statement st = null;
		ResultSet rs = null;
		
		String[] words = partialName.split("\\s+");
		
		if (connection != null) {
			try {
				st = connection.createStatement();
				System.out.println("init query");
				String query = "SELECT long_desc " + "FROM food_des ";
				int i = 0;
				for(String s : words){
					if (i==0)query+="WHERE ";
					if (i>=1)query+="AND ";
					query+="UPPER(food_des.long_desc) LIKE UPPER('%" + s + "%') ";
					i++;
				}
				query+=";";
				System.out.println(query);
				rs = st.executeQuery(query);
				while (rs.next()) {
					foodList.add((rs.getString(1)));
				}
				st.close();
			} catch (SQLException e) {
				System.err.println("ERROR in the query!");
				e.printStackTrace();
			}

		}
		
		return foodList;
	}
	
	/**
	 * This method saves or updates the tags chosen by the user in the database.
	 * 
	 * @param newTags	ArrayList of tags to save
	 */
	public static void saveTags(UserBean user, ArrayList<String> newTags){
		System.out.println("inside saveTags");
		if (connection != null) {
			try {
				if (userExists(user)) {
					ArrayList<String> oldTags = getTags(user);
					
					if(oldTags.isEmpty()){		//there are no tags saved for this user
						System.out.println("oldtags is empty");
						Statement st = connection.createStatement();
						//save the tags
						for(String tag : newTags){
							String query1 = "SELECT tag.id FROM tag WHERE tag.name=" + "'" + tag + "'" + ";";
							System.out.println(query1);
							ResultSet rs = st.executeQuery(query1);
							int tagId = 0;
							while(rs.next()){
								tagId = rs.getInt(1);
							}
							
							String query2 = "INSERT INTO userxtag VALUES (" + tagId + "," + user.getId() + ");";
							System.out.println(query2);
							st.executeUpdate(query2);
							System.out.println("after while");
						}
						user.setTags(newTags);	//set the tags of the userbean with the new list
						st.close();
					} else {					//there are already some tags saved for this user
						System.out.println("there are already some tags");
						/*Statement st = connection.createStatement();
						//update the tags
						st.executeUpdate("UPDATE userbean SET email=" + "'"
								+ user.getEmail() + "'" + "WHERE userbean.email=" + "'"
								+ user.getEmail() + "'" + ";");
						//TODO user.setTags(query to database for oldtags+newtags);
						st.close();
						*/
					}
				}
			} catch (SQLException e) {
				System.err.println("ERROR in the query!");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method returns the list of tags of the user.
	 * 
	 * @param user	UserBean
	 * @return	ArrayList<String>
	 */
	public static ArrayList<String> getTags(UserBean user) {
		System.out.println("inside getTags");
		ArrayList<String> tags = new ArrayList<String>();
		Statement st = null;
		ResultSet rs = null;

		if (connection != null) {
			try {
				st = connection.createStatement();
				String joinQuery = "SELECT userxtag.tag, tag.name, userxtag.user FROM tag INNER JOIN userxtag "
						+ "ON tag.id = userxtag.tag WHERE userxtag.user=" + user.getId() + ";";
				rs = st.executeQuery(joinQuery);
				while (rs.next()) {
					tags.add((rs.getString(2)));
				}
			} catch (SQLException e) {
				System.out.println("error in joinQuery");
				e.printStackTrace();
			}
		}
		return tags;
	}
	
	/**
	 * TODO
	 * 
	 * @param foods
	 * @return
	 */
	public static ArrayList<String> getFoodIDfromFoodName(String[] foods){
		connect();
		ArrayList<String> foodsId = new ArrayList<String>();
		for (String food : foods) {
			Statement st = null;
			ResultSet rs = null;

			if (connection != null) {
				try {
					st = connection.createStatement();
					String joinQuery = "SELECT food_des.ndb_no FROM food_des "
							+ "WHERE food_des.long_desc='" + food + "';";
					System.out.println(joinQuery);
					rs = st.executeQuery(joinQuery);
					while (rs.next()) {
						foodsId.add(rs.getString(1));
					}
				} catch (SQLException e) {
					System.out.println("error in joinQuery");
					e.printStackTrace();
				}
			}
		}
		return foodsId;
	}
	
	/**
	 * TODO
	 * 
	 * @param user
	 * @param date
	 * @param food
	 * @param amount
	 * @return
	 */
	public static boolean saveMealPlan(UserBean user, String date, String[] food, String[] amount) {
		boolean opResult = false;
		if (connection != null) {
			try {
				String foodToAdd = "'{";		//begin to construct the array of food for the database
				String amountToAdd = "'{";		//begin to construct the array of food amounts for the database
				int i = 0;
				for (String f : food) {
					if (i != 0)
						foodToAdd += ",";		//if it is not the first food, add ","
					foodToAdd += '"' + f + '"';	//add the food to the array under construction
					i++;
				}
				foodToAdd += "}'";				//close the array
				
				int j = 0;
				for (String a : amount) {
					if (j != 0)
						amountToAdd += ",";
					amountToAdd += '"' + a + '"';
					j++;
				}
				amountToAdd += "}'";
				
				if (mealPlanExists(user.getId(), date)) {
					Statement st = connection.createStatement();
					String query = "UPDATE mealplan SET foodlist=" + foodToAdd
							+ "," + "amountlist=" + amountToAdd
							+ " WHERE mealplan.user=" + user.getId()
							+ " AND mealplan.date='" + date + "';";
					System.out.println(query);
					st.executeUpdate(query);
					opResult = true;
					st.close();
					//TODO call calculateNutrients
				} else {
					Statement st = connection.createStatement();

					String query = "INSERT INTO mealplan VALUES ("
							+ user.getId() + ",'" + date + "'," + foodToAdd
							+ "," + amountToAdd + ");";
					System.out.println(query);
					st.executeUpdate(query);
					opResult = true;
					st.close();
					//TODO call calculateNutrients
				}

				userCrono(user.getId(), food);
				calculateNutrients(food, amount);
			} catch (SQLException e) {
				System.err.println("ERROR in the query!");
				e.printStackTrace();
			}

		}
		return opResult;
	}
	
	/**
	 * TODO
	 * 
	 * @param id
	 * @param date
	 * @return
	 */
	public static boolean mealPlanExists(int id, String date) {
		boolean opResult = false;

		Statement st1 = null;
		ResultSet rs = null;

		if (connection != null) {
			try {
				st1 = connection.createStatement();
				rs = st1.executeQuery("SELECT * FROM mealplan WHERE mealplan.user = "+ id + "AND mealplan.date='"+date+"';");
				if (rs.next()) {
					opResult = true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return opResult;
	}
	
	/**
	 * TODO
	 * 
	 * @param id
	 * @param foods
	 */
	public static void userCrono(int id, String[] foods){
		Statement st2 = null;
		ResultSet rs2 = null;

		if (connection != null) {
			try {
					st2 = connection.createStatement();
					rs2 = st2.executeQuery("SELECT foodlist FROM userxfoodcronology WHERE userxfoodcronology.user = "+id+";");
					if (rs2.next()) {
						ArrayList<String> oldFood = (ArrayList<String>)Arrays.asList((String[])(rs2.getArray("foodlist")).getArray());
					    for (String f : foods){
					    	if(!oldFood.contains(f)){
					    		oldFood.add(f);
					    	}
					    }
					    
					    String foodToAdd = "'{";
						int i = 0;
						for(String f:oldFood){
							if (i!=0)foodToAdd+=",";
							foodToAdd+='"'+f+'"';
							i++;
						}foodToAdd+="}'";
					    
					    Statement st = connection.createStatement();
						String query = "UPDATE userxfoodcronology SET foodlist="+foodToAdd+" WHERE userxfoodcronology.user="+id+")";
						System.out.println(query);
						st.executeUpdate(query);
						st.close();
					} else{
						String foodToAdd = "'{";
						int i = 0;
						for(String f:foods){
							if (i!=0)foodToAdd+=",";
							foodToAdd+='"'+f+'"';
							i++;
						}foodToAdd+="}'";
						Statement st = connection.createStatement();
						String query = "INSERT INTO userxfoodcronology VALUES("+id+","+foodToAdd+")";
						System.out.println(query);
						st.executeUpdate(query);

						st.close();
					}
										
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * TODO
	 * 
	 * @param user
	 * @param date
	 * @return
	 */
	public static ArrayList<String[]> getMealPlanFromDate(UserBean user, String date){
		connect();
		String[] foodId = new String[200];
		String[] foodAmount = new String[200];
		ArrayList<String[]> output = new ArrayList<String[]>();
			Statement st = null;
			ResultSet rs = null;

			if (connection != null) {
				try {
					st = connection.createStatement();
					String query = "SELECT foodlist, amountlist FROM mealplan WHERE "+'"'+"user"+'"'+"="+ + user.getId() +" AND "+'"'+"date"+'"'+"='" +date + "';";
					System.out.println(query);
					rs = st.executeQuery(query);
					
					if (rs.next()) {
						Array y = rs.getArray("foodlist");
						foodId = ((String[])(y.getArray()));
						y.free();
						y = rs.getArray("amountlist");
						for (int i = 0; i < ((String[])(y).getArray()).length; i++) {
							System.out.println(((String[])(y).getArray())[i]);
						}
						foodAmount = ((String[])(y).getArray());					
					}
					
				} catch (SQLException e) {
					System.err.println("error in query");
					e.printStackTrace();
				}
			}
	
			output.add(foodId);
			output.add(foodAmount);
			
		return output;
	}
	
	/**
	 * 
	 * @return
	 */
	public static ArrayList<String> calculateNutrients(String[] foods, String[] amounts){
		ArrayList<String> nutrients = new ArrayList<String>();
		
		return nutrients;
	}
	
	//**********************************************************************************************************//
	//***************************************** RECOMMENDATION METHODS *****************************************//
	//**********************************************************************************************************//

	/*
	 * � per ogni cibo mangiato dall'utente, vedere quello che ne contiene di pi� [cibo,nutriente]
	 * � per ogni utente con gli stessi tag, per ogni cibo mangiato da quel utente,
	 * vedere quello che ne contiene di pi�[tag,utente,cibo,nutriente]
	 */

	/**
	 * This recommender method finds the food containing the greatest amount of the given nutrient among the list of
	 * foods eaten by the user in the past.
	 * 
	 * @param nutrient
	 * @param user
	 * @return
	 */
	public static void getBestEatenFood(UserBean user, String nutrient){

	}

	/**
	 * This recommender method finds the foods containing the greatest amount of the given nutrient eaten by other users
	 * with the same tags as the user.
	 * 
	 * @param user
	 * @param nutrient
	 */
	public static void getPeerFood(UserBean user, String nutrient){

	}
	
	/**
	 * This recommender method finds the food containing the greatest amount of the given nutrient among the whole
	 * list of foods in the database. If there is more than one food with the same amount, one is chosen randomly.
	 * 
	 * @param nutrient
	 * @return ArrayList<String>	containing the food ID of the food found and the amount of nutrient per 100g
	 */
	public static ArrayList<String> getBestFood(String nutrient){
		ArrayList<String> food_NutrAmount = new ArrayList<String>();
		ArrayList<String> bestFoods = new ArrayList<String>();
		ArrayList<Double> amounts = new ArrayList<Double>();
		String foodId = "";
		String amount = "";
		Statement st = null;
		ResultSet rs = null;
		
		if (connection != null) {
			try {
				st = connection.createStatement();
				String query = "SELECT ndb_no, nutr_val FROM highest_nutr WHERE nutr_no=" + "'" + nutrient + "'" + ";";
				rs = st.executeQuery(query);
				
				while (rs.next()) {
					bestFoods.add(rs.getString(1));
					amounts.add(rs.getDouble(2));
				}
				
				if(bestFoods.size() > 1){						//there is more than one food with the same amount
					Random rnd = new Random();
					int index = rnd.nextInt(bestFoods.size());	//choose randomly one food
					foodId = bestFoods.get(index);
					amount += amounts.get(index);
				} else {
					foodId = bestFoods.get(0);
					amount += amounts.get(0);
				}
			} catch (SQLException e) {
				System.out.println("error in best food query");
				e.printStackTrace();
			}
		}
		food_NutrAmount.add(foodId);
		food_NutrAmount.add(amount);
		System.out.println(food_NutrAmount.get(0) + "\t" + food_NutrAmount.get(1));
		return food_NutrAmount;
	}

	
	public static void database(){
		/*try {
			connect();
			Statement st = connection.createStatement();
			ResultSet rs = null;
			rs = st.executeQuery("SELECT nutr_no FROM nutr_def");
			ArrayList<String> list1 = new ArrayList<String>(); //150 nutrients
			ArrayList<String> list2 = new ArrayList<String>(); //nutrients
			ArrayList<String> list3 = new ArrayList<String>(); //food
			ArrayList<Double> list4 = new ArrayList<Double>(); //values
			
			while(rs.next())
				list1.add(rs.getString(1));
			for(String s : list1){
				String query = "SELECT nutr_no, ndb_no, nutr_val FROM nut_data WHERE nutr_val="
						+ "(SELECT MAX(nutr_val) FROM nut_data WHERE nutr_no=" + "'" + s + "'" + ")"
						+ " AND nutr_no=" + "'" + s + "'" + " ORDER BY nutr_no ASC;";
				ResultSet rs1 = st.executeQuery(query);
				while(rs1.next()){
					list2.add(rs1.getString(1));
					list3.add(rs1.getString(2));
					list4.add(rs1.getDouble(3));
				}
			}
			
			for(int i=0; i<list2.size(); i++){

				if(i<100){
					System.out.println("INSERT INTO highest_nutr (nutr_no, ndb_no, nutr_val) VALUES ("
							+ "'" + list2.get(i) + "'" + ", " + "'" + list3.get(i) + "'" + ", " + list4.get(i) + ");");
				}

				st.executeUpdate("INSERT INTO highest_nutr (nutr_no, ndb_no, nutr_val) VALUES ("
						+ "'" + list2.get(i) + "'" + ", " + "'" + list3.get(i) + "'" + ", " + list4.get(i) + ");");
			}
			
			st.close();
		} catch (SQLException e) {
			System.out.println("ERROR ERROR");
			e.printStackTrace();
		}*/
	}
	
}
