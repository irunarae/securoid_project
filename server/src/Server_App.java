
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
	//variables for diffie helman argorithm
	
	public static void main(String[] args) throws IOException
	{
			String Secret_key = null;
			byte Master_Key[] = {(byte)0x00, (byte)0x19, (byte)0xD1, (byte)0x4E,
					(byte)0xF5, (byte)0xC9, (byte)0x86, (byte)0xF2,
					(byte)0xC1, (byte)0x2A, (byte)0x4C, (byte)0xEB,
					(byte)0x72, (byte)0x50, (byte)0x8D, (byte)0x42};
			//initial variables for original key
			
			Connection conn = null;
			Statement stmt = null;
			ResultSet rq = null;
			//classes for database process
					
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
			//classes for socket communication
			//they has another explanation on the other positions 
			
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
			//string variable for packet that will be sent to the client
			
			String rcv_id;
			int rcv_type;
			String rcv_packet;
			String rcv_data;
			//variables for received packet

			User user1 = null;
			//temp user
			
			int cnt = 0;
						
			while(true){
				snd_packet = "";
				
				if(cnt > 100){
					System.out.println("Connection for user1 is going to be closed and new connection will be held");
					break;
				}
				//after 100 check terminate a connection with a client
				//that means after termination, the server will be waiting for another client
				
				
				rcv_packet = br.readLine();
				//get the packet from socket
				
				if(rcv_packet == null){
					cnt++;
					continue;
				}
				//if there is no packet, count be added
				
				
				cnt = 0;
				System.out.println(rcv_packet);
				
				String[] toks = rcv_packet.split(" ");
				rcv_id = toks[0];
				rcv_type = Integer.parseInt(toks[1]);

				rcv_data = toks[2];
				//packet is splited into three positions
				//1. client's ID
				//2. packet's type
				// 0 : pre-handshake
				// 1 : log-in process
				// 2 : otp process
				//3. packet's data
				
				//packet's type is dependent to the client's state
				//pre-handshake for diffie hellman key exchange
				if(rcv_type == 0){
					int client_rnd_seed = Integer.parseInt(rcv_data);
					
					// --------------------------------------------------------------------------------------------------------------------
					// Key generatioin check code start
					int server_rnd_seed = random_server_rnd_seed();
					server_rnd_seed = (int) Math.pow(alpha,server_rnd_seed)%p;								
					snd_packet = rcv_id + " " + "0" + " " + server_rnd_seed;
					// --------------------------------------------------------------------------------------------------------------------
					// Key generatioin check code end
					
					pw.println(snd_packet);
					//for check the packet to the client
					
					Secret_key = Diffie_Hellman_Key(client_rnd_seed, server_rnd_seed);
					//generating the key for security on the whole process with one client
				}
				
				//log-in process starting
				else if(rcv_type == 1){
					String user_pass = "";
					String user_device_id = "";
					String user_key = "";
					
					//partition of sql query will be started
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
								user_pass = rq.getString(3);
								user_device_id = rq.getString(4);
								user_key = rq.getString(5);
								//get user information from sql
								
								System.out.println(user_pass);
								System.out.println(user_device_id);
								System.out.println(user_key);
								//show user information for check
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
					//partition of sql query has been ended
					
					user1 = new User(rcv_id, user_pass, user_device_id, user_key);
					//user1 is determined by the information that comes from sql
					
					System.out.println("------------------- RCV_DATA ORIGINAL : " + rcv_data);
					
					Securoid_Hashing hash = new Securoid_Hashing();
					//class for hashing
					
					byte[] decrypt_Output = new byte[16];
					decrypt_Output = SeedDecryption(hexToByteArray(rcv_data),hexToByteArray(Secret_key));
					//rcv_data is hashed and seed encrypted usr passwd
					
					String rcv_pass="";
					rcv_pass = byteArrayToHex(decrypt_Output);
					//rcv_pass is decrypted but yet hashed usr passwd
					
					System.out.println("RCV_PASS returned : " + rcv_pass);
					
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
						System.out.println("valid user in log-in process!! Welcome!!");
						int r = random_r();
						String otp_key = random_otp_key();
						//r, otp_key random generate
						
						user1.set_r(r);
						user1.set_otp_key(otp_key);
						//r, otp_key generation partition ended
						
						String tmp_r = String.format("%32s", String.valueOf(r)).replace(' ', '0');
						tmp_r = byteArrayToHex(SeedEncryption(hexToByteArray(tmp_r), hexToByteArray(Secret_key)));
						String tmp_otp_key = byteArrayToHex(SeedEncryption(hexToByteArray(otp_key), hexToByteArray(Secret_key)));
						//r, otp_key seed encrypt partition
						
						snd_packet = user1.id + " " + "1" + " " + tmp_r + " " + tmp_otp_key;
						pw.println(snd_packet);
						//send
					}
					//log-in process ended
				}

				//otp process starting
				else if(rcv_type == 2){
					
					String tmp_otp_key;
					int user_r;
					tmp_otp_key = user1.get_otp_key();
					user_r = user1.get_r();
					//user's own r, otp_key are randomely generated and stored in log-in process
					
					Securoid_Hashing hash = new Securoid_Hashing();
					
					for(int i = 0 ; i < user_r ; i ++)
						tmp_otp_key = hash.MD5(tmp_otp_key);
					String user_otp_key_hash = tmp_otp_key;
					//r times hashed otp_key from server's data
					
					String rcv_hash = byteArrayToHex(SeedDecryption(hexToByteArray(rcv_data),hexToByteArray(Secret_key)));
					//r times hashed otp_key from client
					
					if(!rcv_hash.equals(user_otp_key_hash)){
						System.out.println("otp_key hash : " + user_otp_key_hash);
						//invalid user
						snd_packet = user1.id + " " + "4";
						System.out.println("invalid user");
						pw.println(snd_packet);
						break;
					}
					else{
						//valid user
						System.out.println("valid user in otp-process!! Welcome!!");
						
						String key = user1.key;
						byte[] tmp_data = SeedDecryption(hexToByteArray(key),Master_Key);
						//key which was stored in database is encrypted with master key by seed algorithm
						
						tmp_data = SeedEncryption(tmp_data, hexToByteArray(user1.get_otp_key()));
						String tmp_key = byteArrayToHex(SeedEncryption(tmp_data, hexToByteArray(Secret_key))); 
						//key to be sent is encrypted with otp_key
						
						snd_packet = user1.id + " " + "2" + " " + tmp_key; 
						pw.println(snd_packet);
						System.out.println("this is has Success log");
						System.out.println("Photo_Decrpytion Key(Encrypted) :" + tmp_key);
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
		char[] tmp = new char[32];
		int num_max = 57;
		int num_min = 48;
		int char_max = 102;
		int char_min = 97;
		String otp_key = null;
		
		for(int i = 0 ; i < 32 ; i ++){
			if(Math.random()>0.5)
				tmp[i] = (char)((int)(Math.random()*(num_max-num_min+1))+num_min);
			else
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


