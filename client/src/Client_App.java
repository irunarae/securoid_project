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
		
		SeedX seed = new SeedX();
		int pdwRoundKey[] = new int[32];
		//round key array for seed algorithm
		
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
				byte[] passwd_Input = new byte[16];
				byte[] passwd_Output= new byte[16];
				String passwd_Hash="";
				String passwd_Send="";
				System.out.println("==============passwd length : " + passwd.length());
				
				Securoid_Hashing hash = new Securoid_Hashing();
				passwd_Hash = hash.MD5(passwd);
				passwd_Input = new java.math.BigInteger(passwd_Hash, 16).toByteArray();
				
				//for(int k=0; k< passwd.length(); k++)
				//	passwd_Input[k]=(byte)passwd.charAt(k);
				
				
				//System.out.println("==========passwd.getBytes length: " +passwd.getBytes().length);
				
				//for(int k=passwd.length();k<16;k++){
				//	passwd_Input[k]=0;//add padding to make 16 bytes. 
				//}
				
				seed.SeedRoundKey(pdwRoundKey, deviceKey);
				seed.SeedEncrypt(passwd_Input, pdwRoundKey, passwd_Output);
				//passwd = seed_encrypt(device_id, passwd);
				
				//for(int k=0; k<16; k++)
				//	passwd_Send+=passwd_Output[k];
				passwd_Send = new java.math.BigInteger(passwd_Output).toString(16);
				System.out.println("=========passwd_Send.length : " + passwd_Send.length());
				System.out.println("-------- Original Password : " + passwd_Send);
				
				
				snd_packet = id + " " + String.valueOf(type) + " " + passwd_Send;

				pw.println(snd_packet);
				type++;
				//type 0 is actual login process
				//initial sending
			}
			
			rcv_packet = br.readLine();
			if(rcv_packet == null)
				continue;
			
			System.out.println(rcv_packet);
			//for test
			
			String[] toks = rcv_packet.split(" ");
			rcv_id = toks[0];
			if(rcv_id != id)
				continue;
			rcv_type = Integer.parseInt(toks[1]);
			//type should be considered after
			rcv_data = toks[2];
			if(toks[3] != null)
				rcv_data2 = toks[3];
			
			if(rcv_type == 1){
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
			}
			else if(rcv_type == 2){
				//OTP Authentication completed
				key = rcv_data;//=seed_decrypt(device_id, rcv_data);
				//finally we get the key for decryption
				success = true;
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
}
