import java.io.*;
import java.net.*;
import static java.lang.System.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Server_App {

	public static void main(String[] args) throws IOException
	{
			Connection conn = null;
					
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
			
			while(true){
				snd_packet = "";
				
				if(cnt > 100)
					break;
				//after 100 check terminate
				
				rcv_packet = br.readLine();
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
					//tmp
					String tmp_pass = "passwd";
					String tmp_device_id = "device_id";
					String tmp_key = "key";
					//sql query
					try{
						Statement stmt = conn.createStatement();
						String resultQuery = "SELECT userpassword, device, key FROM securoid WHERE username = rcv_id";
						ResultSet rq = stmt.executeQuery(resultQuery);
						try{
							while(rq.next()){
							tmp_pass = rq.getString("userpassword");
							tmp_device_id = rq.getString("device");
							tmp_key = rq.getString("key");
						
							}
						}finally{
							try{
								rq.close();
							}catch(Throwable ignore){
								
							}
						}
					}finally{
						try{
							Connection stmt;
							stmt.close();
						}
						catch(Throwable ignore){
							
						}
					}
					
					
					user1 = new User(rcv_id, tmp_pass, tmp_device_id, tmp_key);
					
					
					//seed decryption for rcv_data(passwd) with user.device_id
					if(rcv_data != user1.passwd){
						//invalid user
						snd_packet = String.valueOf(user1.id) + " " + "4";
					}
					else{
						//valid user
						int tmp_r = 0;
						String tmp_otp_key = "otp_key";
						//r, otp_key random generate
						
						user1.set_r(tmp_r);
						user1.set_otp_key(tmp_otp_key);
						//r, otp_key generation partition ended
						
						snd_packet = String.valueOf(user1.id) + " " + "1" + " " + String.valueOf(tmp_r) + " " + tmp_otp_key;
						pw.println(snd_packet);
					}
					//user
				}
				else if(rcv_type == 1){
					//user null check should be done
					
					//String tmp;
					//tmp = otp_key
					//for(int i = 0 ; i < r ; i ++)
					//	tmp = hash(tmp)
					//
					//
					//with user's id, find the user's own r, otp_key from the user class
					String tmp_hash = "tmp";
					
					if(rcv_data != tmp_hash){
						//invalid user
						snd_packet = user1.id + " " + "4"; 
					}
					else{
						String key = user1.key;
						String tmp_key = key;// = seed(otp_key, key)
						
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
}
