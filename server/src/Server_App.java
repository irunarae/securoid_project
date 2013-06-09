
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

	public static void main(String[] args) throws IOException
	{
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
		
			System.out.println("Waiting...");
			ServerSocket ss = new ServerSocket(1988);
			//making socket for server with port number 1988 which is my birth year kk sorry to the young
			
			Socket sock = ss.accept();
			//wait until completion of making connection with client
			System.out.println("Server has connected "+sock.getInetAddress()+
					"to the client with port number "+sock.getLocalPort());
			//connection with user
			
			//connection partition completed
			
			
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			//buffered reader which gets messages from socket
			PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
			//print writer which sends messages via socket
			
			String snd_packet;
			
			String rcv_id;
			int rcv_type;
			String rcv_packet;
			String rcv_data;

			User user1 = null;
			//temp user
			int cnt = 0;
			
			SeedX seed = new SeedX();
			int pdwRoundKey[] = new int[32];
			//round key array for seed algorithm
			
			while(true){
				snd_packet = "";
				
				if(cnt > 10000000){
					System.out.println("bye");
					break;
				}
				//after 100 check terminate
				
				rcv_packet = br.readLine();
				if(rcv_packet == null){
					cnt++;
					if(cnt % 10000 == 0)
						System.out.println(String.valueOf(cnt));
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
					System.out.println("rcv_type_0_if_statement?");
					//tmp
					String tmp_pass = "";
					String tmp_device_id = "";
					try{
						byte deviceKey[] = tmp_device_id.getBytes("KSC5601");
					}catch(UnsupportedEncodingException e){
						e.printStackTrace();
					}
					
					byte pbUserKey[] = {(byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03,
							(byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07,
							(byte)0x08, (byte)0x09, (byte)0x0A, (byte)0x0B,
							(byte)0x0C, (byte)0x0D, (byte)0x0E, (byte)0x0F};
					String tmp_key = String.valueOf(pbUserKey);
					
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
						conn.close();
					}
					catch(SQLException ex){
						System.err.println("SQL Error_2");
					}
					System.out.println("Here?");
					user1 = new User(rcv_id, tmp_pass, tmp_device_id, tmp_key);
					System.out.println("Here?1");
					char[] decrypt_Input = new char[16];
					char[] decrypt_Output = new char[16];
					
					for(int k=0; k<rcv_data.length(); k++)
						decrypt_Input[k]= rcv_data.charAt(k);
					
					for(int k=rcv_data.length(); k<16; k++)
						decrypt_Input[k]= 0;
					seed.SeedRoundKey(pdwRoundKey, user1.device_id.getBytes("KSC5601"));
					seed.SeedDecrypt(decrypt_Input, pdwRoundKey, decrypt_Output);
					System.out.println("Here?2");
					String rcv_pass="";
					
					for(int k=0; k<16; k++)
						rcv_pass += decrypt_Output[k];//= seed_decrypt(user1.device_id, rcv_data);
					System.out.println("Here?3");
					//seed decryption for rcv_data(passwd) with user.device_id
					System.out.println(rcv_pass);
					if(!rcv_pass.equals(user1.passwd)){
						//invalid user
						snd_packet = user1.id + " " + "4";
						System.out.println("bye invalid user");
					}
					else{
						//valid user
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
						System.out.println(snd_packet);
						System.out.println("send complete");
					}
					System.out.println("Here?4");
					//user
				}
				else if(rcv_type == 1){
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
					
					if(rcv_hash != tmp_hash){
						//invalid user
						snd_packet = user1.id + " " + "4"; 
					}
					else{
						String key = user1.key;
						String tmp_key = key;// = seed_encrypt(otp_key, key)
						
						snd_packet = user1.id + " " + "2" + " " + tmp_key; 
						pw.println(snd_packet);
					}
				}
			}
			
			pw.close();
			br.close();
			sock.close();
			ss.close();
			//all should be closed after working
	
	}	
	
	public static int random_r(){
		int r;
		int max = 1000;
		int min = 10;
		
		r = (int)(Math.random()*(max-min+1))+min;
		
		return r;
	}
	
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
}


