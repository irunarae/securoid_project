import java.io.*;
import java.net.*;
import static java.lang.System.*;
import java.sql.*;


public class Server_App {

	public static void main(String[] args) throws IOException
	{
			Connection conn = null;
			try{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "securoid");
				System.out.println("DB Connected");
				conn.close();
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
			String data;
			
			int rcv_id;
			int rcv_type;
			String rcv_packet;
			String rcv_data;

			User user1 = null;
			//temp user
			int cnt = 0;
			
			while(true){
				snd_packet = "";
				data = "";
				
				if(cnt > 100)
					break;
				
				rcv_packet = br.readLine();
				if(rcv_packet == null){
					cnt++;
					continue;
				}
				
				cnt = 0;
				System.out.println(rcv_packet);
				//for test
				
				String[] toks = rcv_packet.split(" ");
				rcv_id = Integer.parseInt(toks[0]);
				rcv_type = Integer.parseInt(toks[1]);
				//type should be considered after
				rcv_data = toks[2];
				
				if(rcv_type == 0){
					//tmp
					int tmp_pass = 0;
					int tmp_device_id = 0;
					int tmp_key = 0;
					user1 = new User(rcv_id, tmp_pass, tmp_device_id, tmp_key);
					//sql query
					
					//seed decryption for rcv_data(passwd) with user.device_id
					if(Integer.parseInt(rcv_data) != user1.passwd){
						//invalid user
						snd_packet = String.valueOf(user1.id) + " " + "4";
					}
					else{
						//valid user
						//r, otp_key random generate
						int tmp_r = 0;
						int tmp_otp_key = 0;
						user1.set_r(tmp_r);
						user1.set_otp_key(tmp_otp_key);
						//r, otp_key generation partition ended
						
						snd_packet = String.valueOf(user1.id) + " " + "1" + " " + String.valueOf(tmp_r) + " " + String.valueOf(tmp_otp_key);
						pw.println(snd_packet);
					}
					//user
				}
				else if(rcv_type == 1){
					//user null check should be done
					
					//rcv_data = hash(r, otp_key)
					//with user's id, find the user's own r, otp_key from the user class
					int tmp_hash = 0;//=hash(user1.r, user1.otp_key)
					
					if(Integer.parseInt(rcv_data) != tmp_hash){
						//invalid user
						snd_packet = String.valueOf(user1.id) + " " + "4"; 
					}
					else{
						int key = user1.key;
						int tmp_key = key;// = seed(otp_key, key)
						
						snd_packet = String.valueOf(user1.id) + " " + "2" + " " + String.valueOf(tmp_key); 
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
}
