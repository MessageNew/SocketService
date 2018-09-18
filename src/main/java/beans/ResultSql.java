package beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ResultSql {
	private Statement st;
	private ResultSet rs;
	private PreparedStatement pstmt;
	
	
	public PreparedStatement getPstmt() {
		return pstmt;
	}
	public void setPstmt(PreparedStatement pstmt) {
		this.pstmt = pstmt;
	}
	public Statement getSt() {
		return st;
	}
	public void setSt(Statement st) {
		this.st = st;
	}
	public ResultSet getRs() {
		return rs;
	}
	public void setRs(ResultSet rs) {
		this.rs = rs;
	}

}
