import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Securoid_Hasing{
  public String MD5(String str){
		String MD5 = ""; 
		try{
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			md.update(str.getBytes()); 
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			MD5 = sb.toString();
		
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			MD5 = null; 
		}
		return MD5;
	}
	
	public String SHA256(String str){
		String SHA = ""; 
		try{
			MessageDigest sh = MessageDigest.getInstance("SHA-256"); 
			sh.update(str.getBytes()); 
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();
			
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			SHA = null; 
		}
		return SHA;
	}
	
	/*
	public static void main(String[] args) {
		Securoid_Hasing SH = new Securoid_Hasing();
		String input = new String("input data for test");
		String output_SHA256 = SH.SHA256(input);
		String output_MD5 = SH.MD5(input);
		
		System.out.print("Input : ");System.out.println(input);
		System.out.print("Output(SHA256) : ");System.out.println(output_SHA256);				
		System.out.print("Output(MD5) : ");System.out.println(output_MD5);
	}
	*/
}
