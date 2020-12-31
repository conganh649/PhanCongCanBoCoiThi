import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class dbTest {
	public static void main(String[] args) {
		System.out.println("Ket noi CSDL");
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String url = "jdbc:sqlserver://localhost;databaseName=THLTM;integratedSecurity=true";
			Connection conn = DriverManager.getConnection(url, "sa", "sa");
			Statement stmt = conn.createStatement();
			System.out.println("Thanh cong roi ne");
			String sql = "Select * from Persons";
			ResultSet rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int socot = rsmd.getColumnCount();
			for (int i = 1; i <= socot; i++) {
				System.out.println(rsmd.getColumnLabel(i) + " ");
			}
			System.out.println("Ket noi CSDL");
			while (rs.next()) {
				System.out.println("\n");
				for (int j = 1; j <= socot; j++) {
					System.out.println(rs.getObject(j) + " ");
				}
			}
			System.out.println("done---");
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
