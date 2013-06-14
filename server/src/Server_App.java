
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
//import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.User;

import java.sql.*;


public class Server_App {
	private static int alpha = 2521;
	private static int p = 10859;
	public static void main(String[] args) throws IOException
	{
			String Secret_key = null;
			Connection conn = null;
			Statement stmt = null;
			ResultSet rq = null;
					
			try{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "securoid");
				System.out.println("DB Connected");
				
			}
			catch(ClassNotFoundException cnfe){
				System.out.println("cannot find that class"+cnfe.getMessage());
			}
			catch(SQLException se){
				System.out.println(se.getMessage());
			}
			//connection with sql server
			
			ServerSocket ss;
			Socket sock;
			BufferedReader br;
			PrintWriter pw;
			
			while(true){
				
			System.out.println("Waiting...");
			
			ss = new ServerSocket(1988);
			//making socket for server with port number 1988 which is my birth year kk sorry to the young
			
			sock = ss.accept();
			//wait until completion of making connection with client
			System.out.println("Server has connected "+sock.getInetAddress()+
					"to the client with port number "+sock.getLocalPort());
			//connection with user
			
			//connection partition completed
			
			
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			//buffered reader which gets messages from socket
			pw = new PrintWriter(sock.getOutputStream(), true);
			//print writer which sends messages via socket
			
			String snd_packet;
			
			String rcv_id;
			int rcv_type;
			String rcv_packet;
			String rcv_data;

			User user1 = null;
			//temp user
			int cnt = 0;
						
			while(true){
				snd_packet = "";
				
				if(cnt > 100){
					
					//all should be closed after working
					//hi
					System.out.println("Connection for user1 is going to be closed and new connection will be held");
					break;
				}
				//after 100 check terminate
				//System.out.println("=========TESTING1==========");
				rcv_packet = br.readLine();
				//System.out.println("=========TESTING2==========");
				if(rcv_packet == null){
					cnt++;
					continue;
				}
				
				
				cnt = 0;
				System.out.println(rcv_packet);
				//for test
				
				String[] toks = rcv_packet.split(" ");
				rcv_id = toks[0];
				rcv_type = Integer.parseInt(toks[1]);

				//type should be considered after
				rcv_data = toks[2];
				
				
				if(rcv_type == 0){
					int client_rnd_seed = Integer.parseInt(rcv_data);
					//You should do the seed_key generate operation in here!!!!!!!!!!!!!!!!!!!!!!!!!!
					int server_rnd_seed = random_server_rnd_seed();
					server_rnd_seed = (int) Math.pow(alpha,server_rnd_seed)%p;								
					snd_packet = rcv_id + " " + "0" + " " + server_rnd_seed;
					pw.println(snd_packet);
					
					Secret_key = Diffie_Hellman_Key(client_rnd_seed, server_rnd_seed);
				}
				else if(rcv_type == 1){
					System.out.println("rcv_type_0_if_statement?");
					//tmp
					String tmp_pass = "";
					String tmp_device_id = "";
					
					byte pbUserKey[] = {(byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03,
							(byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07,
							(byte)0x08, (byte)0x09, (byte)0x0A, (byte)0x0B,
							(byte)0x0C, (byte)0x0D, (byte)0x0E, (byte)0x0F};
					String tmp_key = byteArrayToHex(pbUserKey);
					
					//sql query
					System.out.println("Here");
					try{
						

						String resultQuery = "SELECT * FROM securoid.securoid WHERE username = '" + rcv_id + "'";

						stmt = conn.createStatement();
							
						try{
							rq = stmt.executeQuery(resultQuery);
						}
						catch(SQLException ex){
							int errorCode = ex.getErrorCode();
							System.out.println(errorCode);
						}


						try{
							while(rq.next()){
								tmp_pass = rq.getString(3);
								tmp_device_id = rq.getString(4);
								
								System.out.println(tmp_pass);
								System.out.println(tmp_device_id);
							}
						}
						catch(SQLException ex){
							System.out.println("SQL Error_1");
						}
						rq.close();
						stmt.close();
						//conn.close();
					}
					catch(SQLException ex){
						System.err.println("SQL Error_2");
					}
					
					System.out.println("Here?");
					user1 = new User(rcv_id, tmp_pass, tmp_device_id, tmp_key);
					System.out.println("Here?1");
					                                                                                                                                                                                                                                                                                                                                                                                                         
					System.out.println("------------------- Error Detector : " + rcv_data.length());
					System.out.println("------------------- RCV_DATA ORIGINAL : " + rcv_data);
					
					Securoid_Hashing hash = new Securoid_Hashing();
					
					byte[] decrypt_Output = new byte[16];
					decrypt_Output = SeedDecryption(hexToByteArray(rcv_data),hexToByteArray(Secret_key));
					
					System.out.println("Here?2");
					String rcv_pass="";
					
					//for(int k=0; k<16; k++)
					//	rcv_pass += decrypt_Output[k];//= seed_decrypt(user1.device_id, rcv_data);
					rcv_pass = byteArrayToHex(decrypt_Output);
					
					System.out.println("RCV_PASS returned : " + rcv_pass);
					
					System.out.println("Here?3");
					//System.out.println("rcvd password(decrypted) : " + rcv_pass + " length : " + rcv_pass.length());
					//System.out.println("user1.pass : " + user1.passwd + "length : " + user1.passwd.length());
					//System.out.println("rcv_pass.equals(user1.passwd) : " + rcv_pass.equals(user1.passwd));
					//seed decryption for rcv_data(passwd) with user.device_id
					if(!rcv_pass.equals(hash.MD5(user1.passwd))){
						//invalid user
						System.out.println("passwd hash : " + hash.MD5(user1.passwd));
						snd_packet = user1.id + " " + "4";
						System.out.println("invalid user");
						pw.println(snd_packet);
						break;
					}
					else{
						//valid user
						System.out.println("valid user!! Welcome!!");
						int r = random_r();
						String otp_key = random_otp_key();
						//r, otp_key random generate
						
						user1.set_r(r);
						user1.set_otp_key(otp_key);
						//r, otp_key generation partition ended
						
						
						//tmp_r = seed_encrypt(device_id, r);
						//tmp_otp_key = seed_encrypt(device_id, otp_key);
						
						snd_packet = user1.id + " " + "1" + " " + String.valueOf(r) + " " + otp_key;
						pw.println(snd_packet);
					}
					System.out.println("Here?4");
					//user
				}
				else if(rcv_type == 2){
					//user null check should be done
					
					String tmp;
					int tmp_r;
					tmp = user1.get_otp_key();
					tmp_r = user1.get_r();
					
					Securoid_Hashing hash = new Securoid_Hashing();
					
					for(int i = 0 ; i < tmp_r ; i ++)
						tmp = hash.MD5(tmp);
					//with user's id, find the user's own r, otp_key from the user class

					String tmp_hash = tmp;
					String rcv_hash = rcv_data;
					//rcv_hash = seed_decrypt(device_id, rcv_hash);
					
					if(!rcv_hash.equals(tmp_hash)){
						//invalid user
						snd_packet = user1.id + " " + "4";
						System.out.println("this is has failed log");
						pw.println(snd_packet);
						break;
					}
					else{
						String key = user1.key;
						String tmp_key = key;// = seed_encrypt(otp_key, key)
						
						snd_packet = user1.id + " " + "2" + " " + tmp_key; 
						pw.println(snd_packet);
						System.out.println("this is has Success log");
						System.out.println("Photo_Decrpytion Key :" + tmp_key);
						break;

					}
					
					
				}
			}
			pw.close();
			br.close();
			sock.close();
			ss.close();
			}
	}	
	
	public static int random_server_rnd_seed(){
		int r;
		int max = 100000;
		int min = 1000;
		
		r = (int)(Math.random()*(max-min+1))+min;
		
		return r;
	}//for handshake 0(sharing key)
	
	public static int random_r(){
		int r;
		int max = 1000;
		int min = 10;
		
		r = (int)(Math.random()*(max-min+1))+min;
		
		return r;
	}//will use it for handshake 2(otp)
	
	public static String random_otp_key(){
		char[] tmp = new char[16];
		int char_max = 255;
		int char_min = 0;
		String otp_key = null;
		
		for(int i = 0 ; i < 16 ; i ++){
			tmp[i] = (char)((int)(Math.random()*(char_max-char_min+1))+char_min);
		}
		
		otp_key = new String(tmp);
		
		return otp_key;
	}
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
		double DH_key = Math.pow(key1,key2)%p;
		
		Securoid_Hashing hash = new Securoid_Hashing();
		String key = hash.MD5(String.valueOf(DH_key));
				
		return key; 
	}
}


