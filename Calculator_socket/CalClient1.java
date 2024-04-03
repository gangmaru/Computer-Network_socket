
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class CalClient1 {
	private Socket socket=null;
	private DataInputStream input=null;
	private DataOutputStream output=null;

public CalClient1(String address, int port) {
	try {
		socket=new Socket(address,port); //make a client socket
		System.out.println("Connection established");
		input=new DataInputStream(System.in); //to get reply from server
		output=new DataOutputStream(socket.getOutputStream()); //to send request to server
		
		System.out.println("Enter operation. (ex. x operator y)");
		String s="";
		while(!s.equals("exit")) {
			s=input.readLine();
			output.writeUTF(s);
		}
		input.close();
		output.close();
		socket.close();
	}catch(IOException e) { //catch error
		System.out.println(e);
	}
}
public static void main(String[] args) {
	System.out.println("calculator");
	CalClient1 client=new CalClient1("****", ****); //make a client socket
}
}