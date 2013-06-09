import static java.lang.System.*;

public class User {
	int id;
	int passwd;
	int device_id;
	int r;
	int otp_key;
	int key;
	
	User(int id, int passwd, int device_id, int key){
		this.id = id;
		this.passwd = passwd;
		this.device_id = device_id;
		this.key = key;
	}//Constructor
	
	public void set_r(int r){
		this.r = r;
	}
	
	public void set_otp_key(int otp_key){
		this.otp_key = otp_key;
	}
	
	public int get_r(){
		return this.r;
	}
	
	public int get_otp_key(){
		return this.otp_key;
	}
	//methods for r and otp_key
}
