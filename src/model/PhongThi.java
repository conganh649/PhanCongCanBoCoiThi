package model;

import java.io.Serializable;

public class PhongThi implements Serializable {
	private String phongThi;
	private String Diadiem;
	private String ghiChu;
	private String giamthi1;
	private String giamthi2;

	public PhongThi (String pt, String Dd, String gc) {
		this.phongThi = pt;
		this.Diadiem = Dd;
		this.ghiChu = gc;
	}
	public String getPhongThi() {
		return phongThi;
	}
	public String getDiadiem() {
		return Diadiem;
	}
	public String getGhiChu() {
		return ghiChu;
	}
	public void setPhongThi(String phongThi) {
		this.phongThi = phongThi;
	}
	public void setDiadiem(String diadiem) {
		Diadiem = diadiem;
	}
	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}
	public String getGiamthi1() {
		return giamthi1;
	}
	public String getGiamthi2() {
		return giamthi2;
	}
	public void setGiamthi1(String giamthi1) {
		this.giamthi1 = giamthi1;
	}
	public void setGiamthi2(String giamthi2) {
		this.giamthi2 = giamthi2;
	}
	
}
