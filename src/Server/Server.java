package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.GiamThi;
import model.PhongThi;

public class Server {

	// ------------
	private final int _PORT = 6969;
	ArrayList<GiamThi> listGV = new ArrayList<>();
	ArrayList<PhongThi> listPT = new ArrayList<>();
	HashMap<String, PhongThi> listExits = new HashMap<String, PhongThi>();
	boolean isFirst = true;
	Statement stmt;

	// ------------
	public static void main(String[] args) throws IOException {
		new Server();
	}

	// ------------
	public Server() {

		/*
		 * Connect to DataBase
		 */
		try {
			ServerSocket serverSocket = new ServerSocket(_PORT);
			System.out.println("<----- Server is running with " + _PORT + "----->");
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				String url = "jdbc:sqlserver://localhost;databaseName=THLTM;integratedSecurity=true";
				Connection conn = DriverManager.getConnection(url, "sa", "sa");
				stmt = conn.createStatement();
				System.out.println("Connect to DB successfully");
			} catch (Exception e) {
				System.out.println(e.toString());
			}

			/*
			 * Accept connection from Client
			 */
			while (true) {
				// Tạo socket khi kết nối với Client
				Socket sock = serverSocket.accept();
				InputStream ips = sock.getInputStream();
				XSSFWorkbook workbook = new XSSFWorkbook(ips);
				XSSFSheet sheet = workbook.getSheetAt(0);
				XSSFSheet sheet1 = workbook.getSheetAt(1);
				// Lấy danh sách giám thị và phòng thi
				if (isFirst) {
					// Nếu là chia lần đầu tiên thì chia từ trên xuống dưới
					getGiamThi(sheet);
					getPhongThi(sheet1);
					saveDatabase();
					getFirst();
					isFirst = false;
				} else {
					// Nếu lấy từ lần thứ 2 trở đi --> dùng thuật toán
					getresult();
				}

				// Sau khi xử lý xong thì trả về cho client danh sách
				ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
				oos.writeObject(listGV);
				workbook.close();
				ips.close();
				sock.close();
			}

		} catch (Exception e) {
			System.out.println("Error while running Server !");
			e.printStackTrace();
		}
	}

	// ------------
	// Hàm này set tất cả giám thị là giám thị hành lang
	public void reset() {
		for (GiamThi gt : listGV) {
			gt.setChucVu("Giam Thi Hanh Lang");
			gt.setPhongThi("");
		}
	}

	int co = 0;

	// ------------
	// Lấy thông tin các giám thị
	public void getGiamThi(XSSFSheet sheet) {
		for (Row row : sheet) {
			String id = "", Ten = "", ngaySinh = "", CongTac = "";
			if (co == 2989)
				break;
			// Đọc ID
			Cell cellID = row.getCell(1);
			if (cellID.getCellType() == CellType.NUMERIC) {
				id = String.valueOf(cellID.getNumericCellValue());
			} else if (cellID.getCellType() == CellType.STRING) {
				id = cellID.getStringCellValue();
			}

			// Đọc họ tên
			cellID = row.getCell(2);
			if (cellID.getCellType() == CellType.NUMERIC) {
				Ten = String.valueOf(cellID.getNumericCellValue());
			} else if (cellID.getCellType() == CellType.STRING) {
				Ten = cellID.getStringCellValue();
			}

			// Đọc ngày sinh
			cellID = row.getCell(3);
			if (cellID.getCellType() == CellType.NUMERIC) {
				ngaySinh = String.valueOf(cellID.getNumericCellValue());
			} else if (cellID.getCellType() == CellType.STRING) {
				ngaySinh = cellID.getStringCellValue();
			}

			// Đọc đơn vị công tác
			cellID = row.getCell(4);
			if (cellID.getCellType() == CellType.NUMERIC) {
				CongTac = String.valueOf(cellID.getNumericCellValue());
			} else if (cellID.getCellType() == CellType.STRING) {
				CongTac = cellID.getStringCellValue();
			}

			// Thêm giám thị vào trong list
			listGV.add(new GiamThi(id, Ten, ngaySinh, CongTac));
			co++;
		}
		System.out.println("----");
		// Xóa tiêu các tên cột đi
		listGV.remove(0);
	}

	// ------------
	// Đọc thông tin phòng thi tương tự
	public void getPhongThi(XSSFSheet sheet) {
		for (Row row : sheet) {
			String id = "", diaDiem = "", ghiChu = "";

			Cell cellID = row.getCell(1);
			if (cellID.getCellType() == CellType.NUMERIC) {
				id = String.valueOf(cellID.getNumericCellValue());
			} else if (cellID.getCellType() == CellType.STRING) {
				id = cellID.getStringCellValue();
			}
			cellID = row.getCell(2);
			if (cellID.getCellType() == CellType.NUMERIC) {
				diaDiem = String.valueOf(cellID.getNumericCellValue());
			} else if (cellID.getCellType() == CellType.STRING) {
				diaDiem = cellID.getStringCellValue();
			}
			cellID = row.getCell(3);
			if (cellID != null) {
				if (cellID.getCellType() == CellType.NUMERIC) {
					ghiChu = String.valueOf(cellID.getNumericCellValue());
				} else if (cellID.getCellType() == CellType.STRING) {
					ghiChu = cellID.getStringCellValue();
				}
			}
			listPT.add(new PhongThi(id, diaDiem, ghiChu));
		}
		listPT.remove(0);
	}

	// ------------
	// Lấy lần đầu tiên thì chia từ trên xuống dưới
	public void getFirst() {
		int length = listPT.size();
		int count = 0;
		for (int i = 0; i < length; i++) {
			listPT.get(i).setGiamthi1(listGV.get(count).getidGV());
			listGV.get(count).setChucVu("Giam Thi 1");
			listGV.get(count).setPhongThi(listPT.get(i).getPhongThi());
			count += 1;
			listPT.get(i).setGiamthi2(listGV.get(count).getidGV());
			listGV.get(count).setChucVu("Giam Thi 2");
			listGV.get(count).setPhongThi(listPT.get(i).getPhongThi());
			count += 1;
			String key = listPT.get(i).getPhongThi() + String.valueOf(count - 1) + "," + String.valueOf(count - 2);
			listExits.put(key, listPT.get(i));
		}
	}

	// ------------
	// Thuật toán tô màu
	public void getresult() {
		reset();
		Random random = new Random();

		// Duyệt các phòng thi trong danh sách phòng thi
		for (PhongThi pt : listPT) {
			int index1 = 0;
			int index2 = 0;
			boolean check = false;
			do {
				// Vì các phòng thi là có mức độ ưu tiên như nhau
				// Random 1 con số từ 0 -> số giám thị -> chọn giám thị 1
				index1 = random.nextInt(listGV.size());

				// Chọn giám thị 2 là 1 số từ 0 -> index giám thị 1
				index2 = randomGT(index1);

				/*
				 * Kiểm tra xem 2 giám thị trước có có coi chung phòng và coi lại phòng cũ hay
				 * không
				 */
				check = checkExits(pt.getPhongThi(), index1, index2);
			} while (check);

			// Nếu không xung đột thì thêm 2 giám thị đó vào phòng thi
			pt.setGiamthi1(listGV.get(index1).getidGV());
			listGV.get(index1).setChucVu("Giam Thi 1");
			listGV.get(index1).setPhongThi(pt.getPhongThi());
			pt.setGiamthi2(listGV.get(index2).getidGV());
			listGV.get(index2).setChucVu("Giam thi 2");
			listGV.get(index2).setPhongThi(pt.getPhongThi());
			String key = pt.getPhongThi() + String.valueOf(index1) + "," + String.valueOf(index2);
			listExits.put(key, pt);
			// Đánh dấu 2 giám thị vừa thêm vào để chia tiếp các phòng sau
		}
	}

	// ------------
	// Kiểm tra điều kiện giám thị
	public boolean checkExits(String idRoom, int index1, int index2) {
		String key1 = idRoom + String.valueOf(index1) + "," + String.valueOf(index2);
		String key2 = idRoom + String.valueOf(index2) + "," + String.valueOf(index1);
		if (listExits.containsKey(key1)) {
			return true;
		} else if (listExits.containsKey(key2)) {
			return true;
		}
		return false;
	}

	// ------------
	public int randomGT(int i) {
		Random r = new Random();
		int index = r.nextInt(listGV.size() - 1);
		do {
			index = r.nextInt(listGV.size() - 1);
		} while (index == i);

		return index;
	}

	// ------------
	public void saveDatabase() {
		System.out.println("Thêm vào DB nè hihi");

		try {
			stmt.executeUpdate("DELETE FROM GIAMTHI;");
			stmt.executeUpdate("DELETE FROM PHONGTHI;");

		} catch (Exception e) {
			// TODO: handle exception
		}

		for (int i = 0; i < listGV.size(); i++) {
			String sql = "INSERT INTO GIAMTHI(idGV, nameGV, birthDay, DVCT) VALUES (N'" + listGV.get(i).getidGV()
					+ "',N'" + listGV.get(i).getnameGV() + "',N'" + listGV.get(i).getBirthDay() + "',N'"
					+ listGV.get(i).getAddress() + "');";
			try {
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (int i = 0; i < listPT.size(); i++) {
			String sql = "INSERT INTO PHONGTHI(PhongThi, GhiChu) VALUES (N'" + listPT.get(i).getPhongThi() + "',N'')";
			try {
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
