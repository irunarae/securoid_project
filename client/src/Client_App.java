import java.io.*;
import java.net.*;
import static java.lang.System.*;

public class Client_App {
	
	private static int alpha = 456;
	private static int p = 6732;
	private static int tmp_seed;
	private static String Secret_key;
	
	public static void main(String[] args) throws IOException{
		System.out.println("Welcome to Securoid!");
		
		Socket sock = new Socket("14.63.198.240", 1988);
		System.out.println("Attempt to make connection to address, 14.63.198.240 with port number 1988");
		//making socket for client with temperature ip addr. and port number 1988 which i defined in socket_server class
				
		PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
		//print writer which sends messages via socket
		BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		//buffered reader which gets messages from socket
		
		System.out.println("Success");
		
		String id = "securoid";
		int type = 0;
		String passwd = "securoid";
		String device_id = "1234567891011121";
		byte deviceKey[] = device_id.getBytes("KSC5601");
		
		int r = 0;
		String otp_key = null;
		
		byte pbUserKey[] = {(byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03,
				(byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07,
				(byte)0x08, (byte)0x09, (byte)0x0A, (byte)0x0B,
				(byte)0x0C, (byte)0x0D, (byte)0x0E, (byte)0x0F};
		//picture encryption key
		
		String key = String.valueOf(pbUserKey);
		
		//tmp initialization
		//should get from the user's input or arguments
		
		String snd_packet = "";
		byte data[] = new byte[16];

		String rcv_packet = "";
		String rcv_id;
		int rcv_type;
		String rcv_data, rcv_data2 = "";
				
		Boolean success = false;
		int cnt = 0;
		//initial partition
		
		while(!success){
			if(cnt > 3)
				break;
			
			if(type == 0){
				
				tmp_seed = random_tmp_seed();
				
				snd_packet = id + " " + String.valueOf(type) + " " + tmp_seed;
				pw.println(snd_packet);
				
				type++;
				
			}//first handshake
			
			rcv_packet = br.readLine();
			if(rcv_packet == null)
				continue;
			
			String[] toks = rcv_packet.split(" ");
			rcv_id = toks[0];
			
			if(!rcv_id.equals(id))
				continue;
			
			rcv_type = Integer.parseInt(toks[1]);
						
			//type should be considered after
			rcv_data = toks[2];
						
			if(toks.length>=4 && toks[3] != null)
				rcv_data2 = toks[3];
			
			if(rcv_type == 0){
				//TODO: 
				int server_tmp_seed = Integer.parseInt(rcv_data);
				//You should do the seed_key generate operation in here!!!!!!!!!!!!!!!!!!!!!!!!!!
				Secret_key = Diffie_Hellman_Key(server_tmp_seed,tmp_seed);
								
				//-------------------------------------------------------------------------------------------------------------------- Key generatioin check code start
				System.out.println("server_tmp_seed : " + server_tmp_seed);
				System.out.println("client_tmp_seed : " + tmp_seed);
				System.out.println("generated_Key : " + Secret_key);
				//-------------------------------------------------------------------------------------------------------------------- Key generatioin check code end
				
				String passwd_Hash="";
				String passwd_Send="";
				
				Securoid_Hashing hash = new Securoid_Hashing();
				passwd_Hash = hash.MD5(passwd);
				
				byte[] passwd_Output = new byte[16];				
				passwd_Output = SeedEncryption(hexToByteArray(passwd_Hash),hexToByteArray(Secret_key));
				
				passwd_Send = byteArrayToHex(passwd_Output);
		        
				System.out.println("=========passwd_Send.length : " + passwd_Send.length());
				System.out.println("-------- Original Password : " + passwd_Send);
					
				snd_packet = id + " " + String.valueOf(type) + " " + passwd_Send;

				pw.println(snd_packet);
				type++;
				//type 0 is actual login process
				//initial sending
			}
			else if(rcv_type == 1){
				//OTP Process
				r = Integer.parseInt(rcv_data);
				//r = seed_decrypt(device_id, r);
				otp_key = rcv_data2;
				//otp_key = seed_decrypt(device_id, otp_key);
				
				Securoid_Hashing hash = new Securoid_Hashing();
				
				String tmp;
				tmp = otp_key;
				for(int i = 0 ; i < r ; i ++)
					tmp = hash.MD5(tmp);
				//with user's id, find the user's own r, otp_key from the user class
				
				String hashed_key = tmp;
				//hashed_key = seed_encrypt(device_id, tmp);
				snd_packet = id + " " + String.valueOf(type) + " " + hashed_key;
				pw.println(snd_packet);
				type++;
				System.out.println("Now Securoid enter rcv_type1");
			}
			else if(rcv_type == 2){
				//OTP Authentication completed
				key = rcv_data;//=seed_decrypt(device_id, rcv_data);
				//finally we get the key for decryption
				success = true;
				System.out.println("Got password : " + key);
				System.out.println("Photo_Original_Key : " + pbUserKey );
			}
			else{
				//OTP Failure
				//restart?
				type = 0;
				cnt++;
				//retry trois
			}
		}
		
		br.close();
		pw.close();
		sock.close();
		//all should be closed after working
	}
	
	public static int random_tmp_seed(){
		int r;
		int max = 100000;
		int min = 1000;
		
		r = (int)(Math.random()*(max-min+1))+min;
		
		return r;
	}//for handshake 0(sharing key)
	
	
	// hex to byte[]
	public static byte[] hexToByteArray(String hex) {
	    if (hex == null || hex.length() == 0) {
	        return null;
	    }
	 
	    byte[] ba = new byte[hex.length() / 2];
	    for (int i = 0; i < ba.length; i++) {
	        ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
	    }
	    return ba;
	}
	 
	// byte[] to hex
	public static String byteArrayToHex(byte[] ba) {
	    if (ba == null || ba.length == 0) {
	        return null;
	    }
	 
	    StringBuffer sb = new StringBuffer(ba.length * 2);
	    String hexNumber;
	    for (int x = 0; x < ba.length; x++) {
	        hexNumber = "0" + Integer.toHexString(0xff & ba[x]);
	 
	        sb.append(hexNumber.substring(hexNumber.length() - 2));
	    }
	    return sb.toString();
	}
	
	public static byte[] SeedEncryption(byte[] passwd_Input, byte[] encrypt_key) {
		
		byte[] passwd_Output= new byte[16];
		int pdwRoundKey[] = new int[32];
			
		SeedX seed = new SeedX();
		//round key array for seed algorithm
				
		seed.SeedRoundKey(pdwRoundKey, encrypt_key);
		seed.SeedEncrypt(passwd_Input, pdwRoundKey, passwd_Output);
		
		return passwd_Output;
	}
	
	public static byte[] SeedDecryption(byte[] decrypt_Input, byte[] decrypt_key) {
		byte[] decrypt_Output= new byte[16];
		int pdwRoundKey[] = new int[32];
			
		SeedX seed = new SeedX();
		//round key array for seed algorithm
				
		seed.SeedRoundKey(pdwRoundKey, decrypt_key);
		seed.SeedDecrypt(decrypt_Input, pdwRoundKey, decrypt_Output);
		
		return decrypt_Output;
	}
	
	public static String Diffie_Hellman_Key(int key1, int key2){
		double DH_key = Math.pow(Math.pow(alpha, key1),key2)%p;
		
		Securoid_Hashing hash = new Securoid_Hashing();
		String key = hash.MD5(String.valueOf(DH_key));
				
		return key; 
	}
}
