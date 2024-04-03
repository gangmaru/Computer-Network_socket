import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//Process the socket and contain information about the socket
class Handler extends Thread{ 
	private ObjectInputStream reader;
	private ObjectOutputStream writer;
	private Socket socket;
	private List <Handler> list;
	//constructor
	public Handler(Socket socket, List <Handler> list) throws IOException {
		this.socket = socket;
		this.list = list;
		writer = new ObjectOutputStream(socket.getOutputStream());
		reader = new ObjectInputStream(socket.getInputStream());
		//If the order is reversed, a situation occurs in which no values are entered
		//You must create the writer first
		
	}
	public void run(){
		InfoDTO dto = null;
		String nickName;
		try{
			while(true){
				dto=(InfoDTO)reader.readObject();
				nickName=dto.getNickName();
				
				//If the user disconnects
				//send an exit message to the remaining users
				if(dto.getCommand()==Info.EXIT){
					InfoDTO sendDto = new InfoDTO();
					//Send an answer to a client who sent an exit to leave
					sendDto.setCommand(Info.EXIT);
					writer.writeObject(sendDto);
					writer.flush();
					reader.close();
					writer.close();
					socket.close();
					//Send an exit message to the remaining clients
					list.remove(this);
					sendDto.setCommand(Info.SEND);
					sendDto.setMessage(nickName+"님 퇴장하였습니다");
					broadcast(sendDto);
					break;
				} else if(dto.getCommand()==Info.JOIN){
					//All clients must be sent an entry message
					InfoDTO sendDto = new InfoDTO();
					sendDto.setCommand(Info.SEND);
					sendDto.setMessage(nickName+"님 입장하였습니다");
					broadcast(sendDto);
				} else if(dto.getCommand()==Info.SEND){
					InfoDTO sendDto = new InfoDTO();
					sendDto.setCommand(Info.SEND);
					sendDto.setMessage("["+nickName+"]"+ dto.getMessage());
					broadcast(sendDto);
				}
			}
		} catch(IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	//Send a full message to another client
	public void broadcast(InfoDTO sendDto) throws IOException {
		for(Handler handler: list){
			handler.writer.writeObject(sendDto); //Send values to the writer inside the handler
			handler.writer.flush();  //Empty the value of the writer in the handler
		}
	}
}