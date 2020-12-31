package Client;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.GiamThi;

public class Client extends JFrame implements ActionListener, Runnable {
	/*
	 * Khởi tạo các Component sử dụng cho client
	 */
	JButton btnChooseFile;
	FileDialog dialog;
	String pathFile;
	JTextField path;
	JButton btnSendFile;
	JPanel result;
	JScrollPane sp;
	JTable table;
	JLabel label;
	DefaultTableModel tableModel = new DefaultTableModel();

	// ------------
	/*
	 * Các địa chỉ kết nối với Server
	 */
	static private String _ADDRESS = "127.0.0.1";
	static private int _PORT = 6969;

	// ------------
	public static void main(String[] argv) throws Exception {
		new Client();
	}

	// ------------
	public Client() {
		init();
		getWidget();
	}

	// ------------
	/*
	 * Khởi tạo và Setup các component cho Client
	 */
	public void init() {
		this.btnChooseFile = new JButton("Pick a file");
		btnChooseFile.addActionListener(this);
		this.btnSendFile = new JButton("Get result");
		btnSendFile.addActionListener(this);
		dialog = new FileDialog((Frame) null, "Select your data File");
		dialog.setMode(FileDialog.LOAD);
		dialog.setFilenameFilter(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xlsx");
			}
		});
		label = new JLabel("102170205 - Trần Công Anh - Thực hành Lập trình mạng");
		label.setBounds(650, 5, 350, 50);
		table = new JTable();
		table.setBounds(0, 60, 800, 540);
		sp = new JScrollPane(table);
		sp.setBounds(20, 50, 960, 550);
		table.setModel(tableModel);
		table.setBackground(Color.YELLOW);
		this.add(sp);
		this.add(label);
		path = new JTextField();
		result = new JPanel();
	}

	// ------------
	/*
	 * Khởi tạo các nút và vị trí màu sắc của chúng --> Add vào
	 */
	public void getWidget() {
		this.setTitle("THLTM_Final_102170205");
		this.setLocation(200, 20);
		this.setSize(1000, 1000);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(null);
		this.setResizable(false);
		path.setBounds(20, 20, 350, 20);
		btnChooseFile.setBounds(400, 20, 200, 20);
		btnChooseFile.setBackground(Color.PINK);
		btnSendFile.setBounds(400, 630, 200, 20);
		btnSendFile.setBackground(Color.PINK);
		this.add(btnChooseFile);
		this.add(btnSendFile);
		this.add(path);
		this.setVisible(true);

	}

	// ------------
	/*
	 * Mỗi lần thay đổi Ca --> Table sẽ Reload lại và đẩy các data mới lên
	 */
	public void UpdateTable(ArrayList<GiamThi> listPt) {
		try {
			// Tạo các cột dữ liệu
			tableModel.getDataVector().removeAllElements();
			String[] columnNames = { "Mã giám thị", "Tên giám thị", "Ngày sinh", "Đơn vị công tác", "Phòng thi",
					"Chức vụ" };
			tableModel.setColumnIdentifiers(columnNames);

			// Add các hàng dữ liệu trong list
			for (GiamThi pt : listPt) {
				String rows[] = new String[] { String.valueOf(Double.valueOf(pt.getidGV())), pt.getnameGV(),
						pt.getBirthDay(), pt.getClassName(), pt.getPhongThi(), pt.getChucVu() };
				tableModel.addRow(rows);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ------------
	/*
	 * Chuyển file sang Server để xử lý
	 */
	public boolean sendFile(String urlFile) {

		// Lấy file và khởi tạo
		File file = new File(urlFile);
		byte[] byteArray = new byte[(int) file.length()];
		int arrayByteLength = byteArray.length;
		int length;
		try {
			Socket sock = new Socket(this._ADDRESS, this._PORT);

			// Đọc file ra từ hệ thống File trong máy
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			bis.read(byteArray, 0, arrayByteLength);
			FileInputStream fis = new FileInputStream(file);

			// Đẩy file qua Server -> chờ SV trả kết quả
			OutputStream ops = sock.getOutputStream();
			ops.write(byteArray, 0, arrayByteLength);

			// Nhận kết quả từ Server
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			Object result = ois.readObject();
			ArrayList<GiamThi> listPT = (ArrayList<GiamThi>) result;

			// Trả là listPT và list giám thị
			createResult(listPT);
			UpdateTable(listPT);
		} catch (FileNotFoundException e) {
			System.out.println("Can't read this file!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("have error when get input stream from socket!");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Lỗi khi nhận vào Arraylist");
		}
		return false;
	}

	// ------------
	// Xuất file kết quả
	public void createResult(ArrayList<GiamThi> listGT) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet newSheet = workbook.createSheet("Phân công");

		// Điền các trường tương ứng vào File
		Row row0 = newSheet.createRow(0);
		Cell cell0 = row0.createCell(0);
		cell0.setCellValue("Bảng phân công giám thị");
		Row headRow = newSheet.createRow(2);
		Cell col1 = headRow.createCell(0);
		col1.setCellValue("STT");
		Cell col2 = headRow.createCell(1);
		col2.setCellValue("Mã giám thị");
		Cell col3 = headRow.createCell(2);
		col3.setCellValue("Tên giám thị");
		Cell col4 = headRow.createCell(3);
		col4.setCellValue("Ngày sinh");
		Cell col5 = headRow.createCell(4);
		col5.setCellValue("Đơn vị công tác");
		Cell col6 = headRow.createCell(5);
		col6.setCellValue("Mã phòng");
		Cell col7 = headRow.createCell(6);
		col7.setCellValue("Chức vụ");

		int numRow = 4;
		int count = 0;

		// Lấy data trong kết quả trả từ SV add vào bảng
		for (GiamThi pt : listGT) {
			Row row = newSheet.createRow(numRow);
			Cell cell1 = row.createCell(0);
			cell1.setCellValue(count);
			Cell cell2 = row.createCell(1);
			cell2.setCellValue(Double.valueOf(pt.getidGV()));
			Cell cell3 = row.createCell(2);
			cell3.setCellValue(pt.getnameGV());
			Cell cell4 = row.createCell(3);
			cell4.setCellValue(pt.getBirthDay());
			Cell cell5 = row.createCell(4);
			cell5.setCellValue(pt.getClassName());
			Cell cell6 = row.createCell(5);
			cell6.setCellValue(pt.getPhongThi());
			Cell cell7 = row.createCell(6);
			cell7.setCellValue(pt.getChucVu());
			count++;
			numRow++;
		}
		newSheet.autoSizeColumn(0);
		newSheet.autoSizeColumn(1);
		newSheet.autoSizeColumn(2);
		newSheet.autoSizeColumn(3);
		newSheet.autoSizeColumn(4);
		newSheet.autoSizeColumn(5);
		newSheet.autoSizeColumn(6);
		newSheet.autoSizeColumn(7);

		// Xuất file
		File fileout = new File("./result.xlsx");
		try {
			FileOutputStream out = new FileOutputStream(fileout);
			workbook.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ------------
	// Xử lý kết quả cho các nút sự kiện
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnChooseFile) {
			dialog.setVisible(true);
			String directory = dialog.getDirectory();
			String nameFile = dialog.getFile();
			pathFile = directory + nameFile;
			path.setText(pathFile);
		}
		if (e.getSource() == btnSendFile) {
			try {
				sendFile(pathFile);
			} catch (Exception e2) {
				System.out.println("File không tồn tại hoặc không đúng định dạng");
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}
}
