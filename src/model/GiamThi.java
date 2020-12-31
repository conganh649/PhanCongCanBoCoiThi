package model;

import java.io.Serializable;

public class GiamThi implements Serializable {

	private String idGV;
	private String nameGV;
	private String birthDay;
	private String address;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	private String chucVu = "Giam thi hanh lang";
	private String PhongThi = "";

	public GiamThi(String id, String name, String birthDay, String className) {
		this.idGV = id;
		this.nameGV = name;
		this.birthDay = birthDay;
		this.address = className;
	}

	public String getidGV() {
		return idGV;
	}

	public String getnameGV() {
		return nameGV;
	}

	public String getBirthDay() {
		return birthDay;
	}

	public String getClassName() {
		return address;
	}

	public void setidGV(String idGV) {
		this.idGV = idGV;
	}

	public void setnameGV(String nameGV) {
		this.nameGV = nameGV;
	}

	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}

	public void setClassName(String className) {
		this.address = className;
	}

	public String getChucVu() {
		return chucVu;
	}

	public void setChucVu(String chucVu) {
		this.chucVu = chucVu;
	}

	public String getPhongThi() {
		return PhongThi;
	}

	public void setPhongThi(String phongThi) {
		PhongThi = phongThi;
	}

}
