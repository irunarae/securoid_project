import java.io.*;
import java.net.*;
import static java.lang.System.*;

public class Client_App {
	public static void main(String[] args) throws IOException{
		System.out.println("Welcome to Securoid!");
		
		Socket sock = new Socket("14.63.198.240", 1988);
		System.out.println("Attempt to make connection to address, 14.63.198.240 with port number 1988");
		//making socket for client with temperature ip addr. and port number 1988 which i defined in socket_server class
		
		//System.out.println("Will be sent : "+s);
		PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
		//print writer which sends messages via socket
		BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		//buffered reader which gets messages from socket
		System.out.println("Success");
		
		int id = 0;
		int type = 0;
		int passwd = 0;
		//tmp initialization
		//should get from the user's input or arguments
		
		String snd_packet = "";
		String data = "";

		int rcv_id;
		int rcv_type;
		String rcv_packet = "";
		String rcv_data = "";
		
		
		Boolean success = false;
		//initial partition
		
		while(success){
			if(type == 0){
				data = String.valueOf(passwd);
				snd_packet = String.valueOf(id) + " " + String.valueOf(type) + " " + data;
				pw.println(snd_packet);
				type++;
			}
			
			rcv_packet = br.readLine();
			if(rcv_packet == null)
				continue;
			
			System.out.println(rcv_packet);
			//for test
			
			String[] toks = rcv_packet.split(" ");
			rcv_id = Integer.parseInt(toks[0]);
			if(rcv_id != id)
				continue;
			rcv_type = Integer.parseInt(toks[1]);
			//type에 대한 검사도 할지 고민해야함.
			rcv_data = toks[2];
			
			if(rcv_type == 1){
				//seed 뭐 하고 뭐하고..
			}
			else if(rcv_type == 2){
				//뭐 하고 뭐하고
			}
			else if(rcv_type == 3){
				//나나나나
			}
			else{
				//예예예예
			}
		}
		
		//initial sending
		//1st : sending log-in process
		
		//System.out.println("Rcvd : "+reader);
		//get actual messages from buffered reader
		//and print that
		
		br.close();
		pw.close();
		sock.close();
		//all should be closed after working
	}
}
