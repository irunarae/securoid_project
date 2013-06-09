import static java.lang.System.*;

public class User {
	String id;
	String passwd;
	String device_id;
	int r;
	String otp_key;
	String key;
	
	User(String id, String passwd, String device_id, String key){
		this.id = id;
		this.passwd = passwd;
		this.device_id = device_id;
		this.key = key;
	}//Constructor
	
	public void set_r(int r){
		this.r = r;
	}
	
	public void set_otp_key(String otp_key){
		this.otp_key = otp_key;
	}
	
	public int get_r(){
		return this.r;
	}
	
	public String get_otp_key(){
		return this.otp_key;
	}
	//methods for r and otp_key
}
