package livingston.object;

public class LucentRecord {
	public String sProdId;
	public String sClassValue;
	public String sClassifierDate; 
	public String sClassifiedBy;
	public String sLastModifiedDate;
	public String sLastModifiedBy;
	public String sProdStatus;
	public String sDescExpand;
	public String sDescCtry;
	public String sProductNote;
	
	public LucentRecord() {	}
	
	public LucentRecord(String sProdId, String sClassValue, String sClassifierDate, String sClassifiedBy,
			String sLastModifiedDate, String sLastModifiedBy, String sProdStatus, String sDescExpand,
			String sDescCtry, String sProductNote) {
		super();
		this.sProdId = sProdId;
		this.sClassValue = sClassValue;
		this.sClassifierDate = sClassifierDate;
		this.sClassifiedBy = sClassifiedBy;
		this.sLastModifiedDate = sLastModifiedDate;
		this.sLastModifiedBy = sLastModifiedBy;
		this.sProdStatus = sProdStatus;
		this.sDescExpand = sDescExpand;
		this.sDescCtry = sDescCtry;
		this.sProductNote = sProductNote;
	}
	
	public String getsProdId() {
		return sProdId;
	}
	public void setsProdId(String sProdId) {
		this.sProdId = sProdId;
	}
	public String getsClassValue() {
		return sClassValue;
	}
	public void setsClassValue(String sClassValue) {
		this.sClassValue = sClassValue;
	}
	public String getsClassifierDate() {
		return sClassifierDate;
	}
	public void setsClassifierDate(String sClassifierDate) {
		this.sClassifierDate = sClassifierDate;
	}
	public String getsClassifiedBy() {
		return sClassifiedBy;
	}
	public void setsClassifiedBy(String sClassifiedBy) {
		this.sClassifiedBy = sClassifiedBy;
	}
	public String getsLastModifiedDate() {
		return sLastModifiedDate;
	}
	public void setsLastModifiedDate(String sLastModifiedDate) {
		this.sLastModifiedDate = sLastModifiedDate;
	}
	public String getsLastModifiedBy() {
		return sLastModifiedBy;
	}
	public void setsLastModifiedBy(String sLastModifiedBy) {
		this.sLastModifiedBy = sLastModifiedBy;
	}
	public String getsProdStatus() {
		return sProdStatus;
	}
	public void setsProdStatus(String sProdStatus) {
		this.sProdStatus = sProdStatus;
	}
	public String getsDescExpand() {
		return sDescExpand;
	}
	public void setsDescExpand(String sDescExpand) {
		this.sDescExpand = sDescExpand;
	}
	public String getsDescCtry() {
		return sDescCtry;
	}
	public void setsDescCtry(String sDescCtry) {
		this.sDescCtry = sDescCtry;
	}
	public String getsProductNote() {
		return sProductNote;
	}
	public void setsProductNote(String sProductNote) {
		this.sProductNote = sProductNote;
	}

	public String toString() {
		return "LucentRecord [sProdId=" + sProdId + ", sClassValue=" + sClassValue + ", sClassifierDate="
				+ sClassifierDate + ", sClassifiedBy=" + sClassifiedBy + ", sLastModifiedDate=" + sLastModifiedDate
				+ ", sLastModifiedBy=" + sLastModifiedBy + ", sProdStatus=" + sProdStatus + ", sDescExpand="
				+ sDescExpand + ", sDescCtry=" + sDescCtry + ", sProductNote=" + sProductNote + "]";
	}
	
	public String toExcel() {
		return "\"" + sProdId + "\"," 
				+ "\"" + sClassValue + "\","
				+ "\"" + sClassifierDate + "\","
				+ "\"" + sClassifiedBy + "\","
				+ "\"" + sLastModifiedDate + "\","
				+ "\"" + sLastModifiedBy + "\","
				+ "\"" + sProdStatus + "\","
				+ "\"" + sDescExpand + "\","
				+ "\"" + sDescCtry + "\","
				+ "\"" + sProductNote + "\"";
	}
}
