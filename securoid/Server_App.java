import java.io.*;
import java.net.*;

public class Server_App {
	public static void main(String[] args) throws IOException
	{
		ServerSocket ss = new ServerSocket(1988);
		//making socket for server with port number 1988 which is my birth year kk sorry to the young
		
		Socket sock = ss.accept();
		//wait until completion of making connection with client
		System.out.printIn("Server has connected "+sock.getInetAddress()+
				"to the client with port number "+sock.getLocalPort());
		
		BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream());
		//buffered reader which gets messages from socket
		
		PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
		//print writer which sends messages via socket
		
		
		String reader = br.readLine();
		System.out.printIn("Rcvd : "+reader);
		//get actual messages from buffered reader
		//and print that
		
		pw.printIn(reader);
		System.out.printIn("Sent : "+reader);
		//send the message from the client to test
		
		pw.close();
		br.close();
		sock.close();
		ss.close();
		//all should be closed after working
	}
}
