/*
 * 1. �����ͺ��̽� ���� ���� ������ �����ͺ��̽� ���� �ڵ� �������� ���� �ʰ� ����
 * 2. �̱������� ���������ν�, �ν��Ͻ��� ���ʿ��ϰ� ���� ������ �ʾƵ� ��
 * 3. package book;
 * �̱��� �ȿ� Connection ����� �����ϰ� �����Ƿ� connection�� �ѹ��� ���� ����
 * public class DBManager{}
 * */
package book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

	static private DBManager instance;
	private Connection con;	
	
	private String driver="oracle.jdbc.driver.OracleDriver";
	private String url="jdbc:oracle:thin:@localhost:1521:XE";
	private String user="batman";
	private String password="1234";
	
	private DBManager() {
		try {
			Class.forName(driver);
			con=DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}
	public static DBManager getInstance() {
		//�ν��Ͻ��� null�̸� ���� �ٽ� �ǰ� ����
		if(instance==null){
			instance=new DBManager();
		}
		return instance;
	}
	
	public Connection getConnection() {
		return con;
	}

	// �� ��������� ���� ����
	public void disConnect(Connection con){
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
