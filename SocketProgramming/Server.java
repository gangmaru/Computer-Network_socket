import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

class Server {
	private ServerSocket serverSocket;
	private List <Handler> list;
	public Server(){
		try{
			serverSocket= new ServerSocket (****);
			System.out.println("서버 준비 완료");
			list = new  ArrayList<Handler>();
			while(true){
				//Same as the thread created
				Socket socket = serverSocket.accept();
				Handler handler = new  Handler(socket,list);
				handler.start();  //Start Thread - Run Thread
				list.add(handler);  //The number of lists is the total number of clients
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new Server();
	}
}
