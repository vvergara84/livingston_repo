package livingston.object;

public class TsUser {
	public String sUserId;
	public String sPassword;
	
	public TsUser() {	}
	
	public TsUser(String sUserId, String sPassword) {
		super();
		this.sUserId = sUserId;
		this.sPassword = sPassword;
	}

	public String getsUserId() {
		return sUserId;
	}

	public void setsUserId(String sUserId) {
		this.sUserId = sUserId;
	}

	public String getsPassword() {
		return sPassword;
	}

	public void setsPassword(String sPassword) {
		this.sPassword = sPassword;
	}

	public String toString() {
		return "TsUser [sUserId=" + sUserId + ", sPassword=" + sPassword + "]";
	}
	
	public String toExcel() {
		return "\"" + sUserId + "\"," 
				+ "\"" + sPassword + "\"";
	}
}
