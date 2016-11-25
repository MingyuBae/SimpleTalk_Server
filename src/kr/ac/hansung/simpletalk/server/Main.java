package kr.ac.hansung.simpletalk.server;

public class Main {
	
	public static void main(String args[]){
		
		Server server = new Server();
		
		server.startServer(80);
		
	}
}
