package kr.ac.hansung.simpletalk.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private ServerSocket serverSocket;
	private ChatService chatService;
	
	public void startServer(int portNumber){
		try {
			serverSocket = new ServerSocket(portNumber);
			chatService = new ChatService();
			
			Connection();
			
			System.out.println("[서버 준비 완료] - port: " + portNumber);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("서버 소켓 생성중 문제 발생");
		}
		
	}
	
	
	private void Connection() {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket soc;
						soc = serverSocket.accept();
						chatService.addUser(soc);
						
					} catch (IOException e) {
						System.err.println("클라이언트 Accept 중 오류 발생");
					} 
				}
			}
		});
		th.start();
	}
}
