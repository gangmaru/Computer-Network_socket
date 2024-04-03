import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CalServer1 {
	private Socket socket=null;
	private DataInputStream input=null;
	private ServerSocket server=null;
	
	public double operation(double a, double b, char op) {
		switch(op) {
		case '+':
			return a+b;
		case '-':
			return a-b;
		case '*':
			return a*b;
		case '/':
			return a/b;
		default:
			System.out.println("\nIncorrect:\nInvalid Operation\n"); //error message
		}
		return -1;
	}
	public CalServer1(int port) {
		try {
			server=new ServerSocket(port);
			System.out.println("Server started..\n");
			socket=server.accept(); //connection with client
			System.out.println("Client accepted..");
			input=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			String s="";
			while(true) {
				s=input.readUTF();
				if(!s.equals("exit")&&s.length()>4) {
					String[]str=s.split("\\s+");
					double a=Integer.parseInt(str[0]); //a
					char o=str[1].charAt(0); //operation
					double b=Integer.parseInt(str[2]); //b
					double res=operation(a, b, o);
					System.out.printf("Answer: %.0f\n",res); //answer a operation b
				}
				else {
					if(s.length()<5&&!s.equals("exit")) { //error message
						System.out.println("\nIncorrect:\nToo many arguments\n");
					}else {
						break;
					}
				}
			}
			System.out.println("Closing Connection\n"); //close connection
			input.close();
			socket.close();
		}catch(IOException e) {
			System.out.println(e);
		}
	}
	
	public static void main(String[] args) {
		CalServer1 server=new CalServer1(****); //server
	}
}

