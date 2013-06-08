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
				System.out.println("해당 클래스를 찾을 수 없습니다."+cnfe.getMessage());
			}
			
			catch(SQLException se){
				System.out.println(se.getMessage());
			}
		
			System.out.println("Waiting...");
			ServerSocket ss = new ServerSocket(1988);
			//making socket for server with port number 1988 which is my birth year kk sorry to the young
			
			Socket sock = ss.accept();
			//wait until completion of making connection with client
			System.out.println("Server has connected "+sock.getInetAddress()+
					"to the client with port number "+sock.getLocalPort());
			//connection complete
			
			
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			//buffered reader which gets messages from socket
			PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
			//print writer which sends messages via socket
			
			String snd_packet;
			String data;

			int username = 0;
			int userpassword = 0;
			
			int rcv_id;
			int rcv_type;
			String rcv_packet;
			String rcv_data;
			
			//User user1;
			//temp user
			int cnt = 0;
			
			while(true){
				snd_packet = "";
				data = "";
				
				if(cnt > 10)
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
				//type�� ���� �˻絵 ���� ����ؾ���.
				rcv_data = toks[2];
				
				if(rcv_type == 0){
					//tmp
					int tmp_pass = 0;
					int tmp_device_id = 0;
					//user1 = new User(rcv_id, tmp_pass, tmp_device_id);
					//sql query
					
					//seed decryption for rcv_data(passwd) with user.device_id
					if(Integer.parseInt(rcv_data) != userpassword){
						//invalid user
						//type 4 snd_pakcet
					}
					else{
						//valid user
						//r, otp_key random generate
						int tmp_r = 0;
						int tmp_otp_key = 0;
						snd_packet = String.valueOf(username) + " " + "1" + " " + String.valueOf(tmp_r) + " " + String.valueOf(tmp_otp_key);
						pw.println(snd_packet);
					}
					//user�� ���� ���� ���� �ٲ� ��
				}
				else if(rcv_type == 1){
					//
					
				}
				else if(rcv_type == 2){
					
				}
				else if(rcv_type == 3){
					
				}
			}
			
			
			String reader = br.readLine();
			System.out.println("Rcvd : "+reader);
			//get actual messages from buffered reader
			//and print that
			
			pw.println(reader);
			System.out.println("Sent : "+reader);
			//send the message from the client to test
			
			pw.close();
			br.close();
			sock.close();
			ss.close();
			//all should be closed after working
	
	}	
}
