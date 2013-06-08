import java.io.*;
import java.net.*;

public class Client_App {
	public static void main(String[] args) throws IOException{
		String s = "Input to the server";
		//this is just test input
		
		Socket sock = new Socket("163.152.161.155", 1988);
		System.out.printIn("Attempt to make connection to address, 163.152.161.155 with port number 1988");
		//making socket for client with temperature ip addr. and port number 1988 which i defined in socket_server class
		
		System.out.printIn("Will be sent : "+s);
		PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
		//print writer which sends messages via socket
		pw.printIn(s);
		//send the message to server for test
		
		
		BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		//buffered reader which gets messages from socket
		
		String reader = br.readLine();
		System.out.printIn("Rcvd : "+reader);
		//get actual messages from buffered reader
		//and print that
		
		br.close();
		pw.close();
		sock.close();
		//all should be closed after working
	}
}
