package kr.ac.hansung.simpleTalk.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kr.ac.hansung.simpleTalk.transformVO.MessageVO;
import kr.ac.hansung.simpleTalk.transformVO.UserProfileVO;

public class ChatService {
	private List<UserVO> userList = new LinkedList<>();
	private Map<Integer, UserVO> userMap = new HashMap<>();
	private List<ChatRoomVO> chatRoomList = new LinkedList<>();
	private Integer userIdIncrement = 0;
	
	
	public void addUser(Socket socket){
		UserVO userInfo = new UserVO();
		
		try {
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			
			userInfo.setSocket(socket);
			userInfo.setIs(is);
			userInfo.setOis(new ObjectInputStream(is));
			userInfo.setOs(os);
			userInfo.setOos(new ObjectOutputStream(os));
			
			userList.add(userInfo);
			
			makeClientThread(userInfo);
			
		} catch (IOException e) {
			System.err.println("[사용자 등록] 오류 발생");
			e.printStackTrace();
		}
	}
	
	public boolean sendMessage(UserVO sendUserInfo, MessageVO msgData){
		ChatRoomVO roomInfo = chatRoomList.get(msgData.getRoomIdx());
		List<UserVO> chatRoomMemberList;
		
		if(roomInfo == null || !roomInfo.getMemberList().contains(sendUserInfo)){
			// 채팅방에 해당 유저가 없는 경우
			return false;
		}
		
		chatRoomMemberList = roomInfo.getMemberList();
		
		for(UserVO chatUser : chatRoomMemberList){
			/* 채팅에 참여하고 있는 사용자에게 메시지 전송 */
			ObjectOutputStream oos = chatUser.getOos();
			
			try {
				oos.writeObject(msgData);
			} catch (IOException e) {
				System.out.println("[메시지 전송] 오류 발생 - UserInfo: " 
										+ chatUser + ", MessageData: " + msgData);
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	//TODO 리턴값을 불린 타입보다 채팅 정보를 줘서 클라이언트가 어떤 사용자가
	// 채팅방에 참여됬는지 알려주는게 좋을듯?
	public boolean makeRoom(MessageVO msgData){
		if(! MessageVO.MSG_TYPE_MAKEROOM.equals(msgData.getType())){
			return false;
		}
		
		String roomName = msgData.getData();
		String userIdListString = (String)msgData.getObject();
		String[] userIdArray = userIdListString.split(MessageVO.MSG_SPLIT_CHAR);
		
		ChatRoomVO chatRoomData = new ChatRoomVO();
		chatRoomData.setRoomName(roomName);
		
		int enterUserCount = 0;
		
		for(String userId: userIdArray){
			UserVO userInfo = userMap.get(userId);
			
			if(userInfo == null){
				System.err.println("[채팅방 생성] 해당 사용자를 찾을 수 없음 - " + userId);
				continue;
			}
			
			chatRoomData.addMember(userInfo);
			userInfo.addEnterChatRoom(chatRoomData);
			enterUserCount ++;
		}
		
		if(enterUserCount < 1){
			return false;
		}
		
		chatRoomList.add(chatRoomData);
		
		return true;
	}
	
	public void makeClientThread(UserVO userInfo){
		Thread th = new Thread(new Runnable() { 
			@Override
			public void run() {
				// TODO 사용자 정보를 수신 (이름, 상태메시지, 사진 등등)
				UserProfileVO userProfile = null;
				try {
					userProfile = (UserProfileVO)userInfo.getOis().readObject();
					userProfile.setId(userIdIncrement++);
					userInfo.setUserProfile(userProfile);
					
					userMap.put(userProfile.getId(), userInfo);
					
					/* 설정된 사용자 정보를 클라이언트에 전송 */
					userInfo.getOos().writeObject(userProfile);
					
					System.out.println("[사용자 등록] 성공 - " + userProfile);
				} catch (ClassNotFoundException | IOException e2) {
					System.err.println("[사용자 등록 과정] 예외 발생");
					e2.printStackTrace();
					return;
				}
				
				while (true) {
					try{
						// TODO 사용자가 보내는 메시지 처리 (채팅방 개설, 메시지 변경, 프로필 변경 등)
						MessageVO msg = (MessageVO)userInfo.getOis().readObject();
						
						switch (msg.getType()) {
						case MessageVO.MSG_TYPE_TEXT:
							
							break;
						case MessageVO.MSG_TYPE_MAKEROOM:
							userInfo.getOos().writeObject(makeRoom(msg));

						default:
							System.err.println("[메시지 수신] 알려지지 않은 타입: " + msg.getType());
							break;
						}
						
					} catch (IOException e) {
						// TODO 클라이언트 접속 종료시 처리해야 될 부분 (소캣 끊기, 채팅방에서 나가기 처리, 사용자 리스트에 제외)
						allExitChatRoom(userInfo);
						
						try {
							userInfo.getOis().close();
							userInfo.getOos().close();
							userInfo.getIs().close();
							userInfo.getOs().close();
							
							userInfo.getSocket().close();
						} catch (IOException e1) {}
						
						
						userList.remove(userInfo);
					} catch (ClassNotFoundException e) {
						System.err.println("[메시지 수신] 수신받은 데이터 객체가 알 수 없는 타입니다.");
						e.printStackTrace();
					}
				}
			}
		});
		th.start();
	}
	
	private void allExitChatRoom(UserVO userInfo){
		List<ChatRoomVO> enterChatRoomList = userInfo.getEnterChatRoomList();
		
		for(ChatRoomVO roomInfo : enterChatRoomList){
			exitChatRoom(userInfo, roomInfo);
		}
	}
	
	private void exitChatRoom(UserVO userInfo, ChatRoomVO roomInfo){
		roomInfo.removeMember(userInfo);
		userInfo.removeEnterChatRoom(roomInfo);
	}
}
