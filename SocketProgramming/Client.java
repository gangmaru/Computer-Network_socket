import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.io.*;

class Client extends JFrame implements ActionListener,Runnable{
	private JTextArea output; 
	private JTextField input; 
	private JButton sendBtn;
	private Socket socket;
	private ObjectInputStream reader=null;
	private ObjectOutputStream writer=null; 
	private String nickName;
	
	public Client() {
		//create TextArea in the center
		output = new JTextArea();
		output.setFont(new Font("맑은 고딕",Font.BOLD,15));
		output.setEditable(false);
		JScrollPane scroll = new JScrollPane(output);
		//The scrollbar always floats vertically
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		//Insert button and TextArea at the bottom
		JPanel bottom = new JPanel();
		bottom.setLayout(new BorderLayout()); 
		input = new JTextField();
		
		sendBtn = new JButton("send");
		
		bottom.add("Center",input);  //center
		bottom.add("East",sendBtn);  //east
		//container
		Container c = this.getContentPane();
		c.add("Center", scroll);  //center
		c.add("South", bottom);  //south
		//set the window 
		setBounds(300,300,300,300);
		setVisible(true);

		//window event
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){ 
				try{
					InfoDTO dto = new InfoDTO();
					dto.setNickName(nickName);
					dto.setCommand(Info.EXIT);
					writer.writeObject(dto);
					writer.flush();
				}catch(IOException io){
					io.printStackTrace();
				}
			}
		});
	}
	
	public static void main(String[] args) {
		new Client().service();
	}
	
	//ActionPerformed
	@Override
	public void actionPerformed(ActionEvent e){
			try{
				//Sent to server
				//Send JTextField value to server
				//Empty Buffer
				String msg=input.getText();
				InfoDTO dto = new InfoDTO();
				if(msg.equals("exit")){
					dto.setCommand(Info.EXIT);
				} else {
					dto.setCommand(Info.SEND);
					dto.setMessage(msg);
					dto.setNickName(nickName);
				}
				writer.writeObject(dto);
				writer.flush();
				input.setText("");
			}catch(IOException io){
				io.printStackTrace();
			}
	}
	public void service(){
		//Get server IP input
		String serverIP= JOptionPane.showInputDialog(this,"type Server IP","****");
		//Window off if no value is entered
		if(serverIP==null || serverIP.length()==0){  
			System.out.println("Server IP is not entered.");
			System.exit(0);
		}
		//get nickname
		nickName= JOptionPane.showInputDialog(this,"Please enter nickname","nickname" ,JOptionPane.INFORMATION_MESSAGE);
		if(nickName == null || nickName.length()==0){
			nickName="guest";
		}
		try{
			socket = new Socket(serverIP,****);
			//error
			reader= new ObjectInputStream(socket.getInputStream());
			writer = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("전송 준비 완료!"); 
			
		} catch(UnknownHostException e ){
			System.out.println("서버를 찾을 수 없습니다.");
			e.printStackTrace();
			System.exit(0);
		} catch(IOException e){
			System.out.println("서버와 연결이 안되었습니다.");
			e.printStackTrace();
			System.exit(0);
		}
		try{
			//Send nicknames to servers
			InfoDTO dto = new InfoDTO();
			dto.setCommand(Info.JOIN);
			dto.setNickName(nickName);
			writer.writeObject(dto);
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
		//create thread
		Thread t = new Thread(this);
		t.start();
		input.addActionListener(this);
		sendBtn.addActionListener(this);
	}
	
	//thread override
	@Override
	public void run(){
		//Receiving data from a server
		InfoDTO dto= null;
		while(true){
			try{
				dto = (InfoDTO) reader.readObject();
				//Shut down when I receive my own exit from the server
				if(dto.getCommand()==Info.EXIT){
					reader.close();
					writer.close();
					socket.close();
					System.exit(0);
				} else if(dto.getCommand()==Info.SEND){
					output.append(dto.getMessage()+"\n");
					int pos=output.getText().length();
					output.setCaretPosition(pos);
				}
			}catch(IOException e){
				e.printStackTrace();
			}catch(ClassNotFoundException e){
				e.printStackTrace();
			}	
		}
	}
}
