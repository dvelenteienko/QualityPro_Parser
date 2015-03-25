package ua.com.vza.DBUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.com.vza.ParseDbf.Parse;
import ua.com.vza.XmlSettings.ParseXmlSettings;

public class DBProcess {

	private String db_driver;
	private String db_connection;
	private String db_username;
	private String db_password;

	private String selectName;
	private String updateName;
	private String selectSpGOST;
	private String updateSpGOST;
	private String selectSpOST;
	private String updateSpOST;
	private String selectSpTU;
	private String updateSpTU;
	private String selectSpDSTU;
	private String updateSpDSTU;
	private String selectProductType;
	private String updateProductType;
	private String selectCountRecord;
	private String updateCountRecord;

	private Connection dbConnection;
	private Statement preparedStatementSelect;
	private PreparedStatement preparedStatementInsert;
	private PreparedStatement preparedStatementUpdate;

	private Parse fromParser;

	public DBProcess() {
		this.db_connection = "";
		this.db_driver = "";
		this.db_password = "";
		this.db_username = "";
		readConfig();
		if (dbConnection != null) {
			try {
				closeConnection();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void readConfig() {
		String key = "";
		ArrayList<String> value = new ArrayList<String>();
		HashMap<String, ArrayList<String>> mapSettings = new HashMap<String, ArrayList<String>>(
				ParseXmlSettings.readXML("db", "name", "p"));
		for (Map.Entry<String, ArrayList<String>> entry : mapSettings
				.entrySet()) {
			key = entry.getKey();
			value = new ArrayList<String>(entry.getValue());
			switch (key) {
			case "DBDriver":
				db_driver = value.get(0);
				break;
			case "DBConnectionString":
				db_connection = value.get(0);
				break;
			case "DBUserName":
				db_username = value.get(0);
				break;
			case "DBPassword":
				db_password = value.get(0);
				break;
			case "insertName":
				selectName = value.get(0);
				break;
			case "updateName":
				updateName = value.get(0);
				break;
			case "selectSpGOST":
				selectSpGOST = value.get(0);
				break;
			case "insertSpGOST":
				updateSpGOST = value.get(0);
				break;
			case "selectSpOST":
				selectSpOST = value.get(0);
				break;
			case "insertSpOST":
				updateSpOST = value.get(0);
				break;
			case "selectSpTU":
				selectSpTU = value.get(0);
				break;
			case "insertSpTU":
				updateSpTU = value.get(0);
				break;
			case "selectSpDSTU":
				selectSpDSTU = value.get(0);
				break;
			case "insertSpDSTU":
				updateSpDSTU = value.get(0);
				break;
			case "selectProductType":
				selectProductType = value.get(0);
				break;
			case "insertProductType":
				updateProductType = value.get(0);
				break;
			case "selectCountRecord":
				selectCountRecord = value.get(0);
				break;
			case "updateCountRecord":
				updateCountRecord = value.get(0);
				break;
			}
		}
	}

	private void loadDriver() {
		try {
			Class.forName(db_driver);
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		}
	}

	private void getConnection() {
		dbConnection = null;
		loadDriver();
		try {
			dbConnection = DriverManager.getConnection(db_connection,
					db_username, db_password);
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	private void closeConnection() throws SQLException {
		if (preparedStatementInsert != null) {
			preparedStatementInsert.close();
		}

		if (preparedStatementSelect != null) {
			preparedStatementSelect.close();
		}

		if (preparedStatementUpdate != null) {
			preparedStatementUpdate.close();
		}

		if (dbConnection != null) {
			dbConnection.close();
		}
	}

	private void updateValueOfCountRows(String query, String[] params)
			throws SQLException {
		closeConnection();
		getConnection();
		try {
			dbConnection.setAutoCommit(false);
			preparedStatementUpdate = dbConnection.prepareStatement(query);
			for (int i = 1; i <= params.length; i++) {
				preparedStatementUpdate.setInt(i,
						Integer.valueOf(params[i - 1]));
			}
			preparedStatementUpdate.executeUpdate();
			dbConnection.commit();

		} catch (SQLException ex) {
			dbConnection.rollback();
			ex.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	private void insertValues(String query, ArrayList<String> updData)
			throws SQLException {
		closeConnection();
		getConnection();
		try {
			dbConnection.setAutoCommit(false);
			preparedStatementInsert = dbConnection.prepareStatement(query);
			for (String ins : updData) {
				preparedStatementInsert.setString(1, ins);
				preparedStatementInsert.executeUpdate();
			}
			dbConnection.commit();
		} catch (SQLException e) {
			dbConnection.rollback();
			e.printStackTrace();
		}

	}

	public ArrayList<String> selectValues(String query, String typeFields,
			String[] namesOfFileds) throws SQLException {
		closeConnection();
		getConnection();
		
		preparedStatementSelect = null;
		ArrayList<String> tmp = new ArrayList<String>();
		try{
		preparedStatementSelect = (Statement) dbConnection.createStatement();
		ResultSet set1 = preparedStatementSelect.executeQuery(query);
		while (set1.next()) {
			switch (typeFields) {
			case "string":
				if (namesOfFileds.length > 1) {
					for (int i = 0; i < namesOfFileds.length; i++) {
						tmp.add(set1.getString(namesOfFileds[i]));
					}
				} else {

					tmp.add(set1.getString(namesOfFileds[0]));
				}
				break;

			case "int":
				if (namesOfFileds.length > 1) {
					for (int i = 0; i < namesOfFileds.length; i++) {
						tmp.add(String.valueOf(set1.getInt(namesOfFileds[i])));
					}
				} else {

					tmp.add(String.valueOf(set1.getString(namesOfFileds[0])));
				}
				break;
			}
		}
		}catch()
		closeConnection();
		return tmp;
	}

	public void updateData() throws SQLException {
		closeConnection();
		getConnection();
	}
}