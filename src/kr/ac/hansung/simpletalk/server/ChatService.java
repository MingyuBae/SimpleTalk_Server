package kr.ac.hansung.simpletalk.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kr.ac.hansung.simpletalk.transformVO.MessageVO;
import kr.ac.hansung.simpletalk.transformVO.UserProfileVO;

public class ChatService {
	private Map<Integer, UserVO> userMap = Collections.synchronizedMap(new HashMap<>());
	private Map<Integer, ChatRoomVO> chatRoomMap = Collections.synchronizedMap(new HashMap<>());
	
	private Integer userIdIncrement = 0;
	private Integer chatRoomIdIncrement = 0;
	
	
	/**
	 * 새로 접속한 클라이언트를 등록
	 * @param socket 새로 접속한 클라이언트의 소캣
	 */
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
			
			clientListenerThread(userInfo);
			
		} catch (IOException e) {
			System.err.println("[사용자 등록] 오류 발생");
			e.printStackTrace();
		}
	}
	
	/**
	 * 메시지 전송
	 * @param sendUserInfo 메시지를 전송한 유저 정보
	 * @param msgData 보낼 메시지 데이터
	 * @return 메시지 처리 결과
	 */
	public MessageVO sendMessage(UserVO sendUserInfo, MessageVO msgData){
		MessageVO returnMsg = new MessageVO();
		returnMsg.setType(MessageVO.MSG_TYPE_TEXT);
		
		ChatRoomVO roomInfo = chatRoomMap.get(msgData.getRoomIdx());
		List<UserVO> chatRoomMemberList;
		
		if(roomInfo == null || !roomInfo.getMemberList().contains(sendUserInfo)){
			// 채팅방에 해당 유저가 없는 경우
			returnMsg.setData(MessageVO.MSG_ERROR);
			return returnMsg;
		}
		
		chatRoomMemberList = roomInfo.getMemberList();
		
		int sendCount = 0;
		for(UserVO chatUser : chatRoomMemberList){
			/* 채팅에 참여하고 있는 사용자에게 메시지 전송 */
			ObjectOutputStream oos = chatUser.getOos();
			
			try {
				oos.writeObject(msgData);
				System.out.println("[메시지 전송] 성공 - 수신자: " 
						+ chatUser + ", MessageData: " + msgData);
				sendCount++;
			} catch (IOException e) {
				System.out.println("[메시지 전송] 오류 발생 - UserInfo: " 
										+ chatUser + ", MessageData: " + msgData);
				e.printStackTrace();
			}
		}
		System.out.println("[메시지 전송] 성공 - 전송자: " 
				+ sendUserInfo + ", MessageData: " + msgData);
		
		returnMsg.setData(MessageVO.MSG_SUCCESS);
		returnMsg.setObject(sendCount);
		return returnMsg;
	}
	
	//TODO 리턴값을 불린 타입보다 채팅 정보를 줘서 클라이언트가 어떤 사용자가
	// 채팅방에 참여됬는지 알려주는게 좋을듯?
	/**
	 * 채팅방 생성
	 * @param msgData 생성할 채팅방 정보가 들어있는 메시지
	 * @return 생성된 채팅방에 대한 메시지
	 */
	public MessageVO makeRoom(MessageVO msgData){
		MessageVO returnMsg = new MessageVO();
		returnMsg.setType(MessageVO.MSG_TYPE_MAKEROOM);
		
		if(! MessageVO.MSG_TYPE_MAKEROOM.equals(msgData.getType())){
			returnMsg.setData(MessageVO.MSG_ERROR);
			return returnMsg;
		}
		
		String roomName = msgData.getData();
		String userIdListString = (String)msgData.getObject();
		String[] userIdArray = userIdListString.split(MessageVO.MSG_SPLIT_CHAR);
		
		ChatRoomVO chatRoomData = new ChatRoomVO();
		chatRoomData.setRoomName(roomName);
		
		int enterUserCount = 0;
		
		for(String userIdString: userIdArray){
			UserVO userInfo = null;
			
			try{
				Integer userId = Integer.parseInt(userIdString);
				userInfo = userMap.get(userId);
			} catch (NumberFormatException e) {
				System.err.println("[채팅방 생성] 실패 - ID가 숫자가 아님 (" + userIdString + ")");
				continue;
			}
			
			
			if(userInfo == null){
				System.err.println("[채팅방 생성] 실패 - 해당 사용자를 찾을 수 없음 (" + userIdString +")");
				continue;
			}
			
			chatRoomData.addMember(userInfo);
			userInfo.addEnterChatRoom(chatRoomData);
			enterUserCount ++;
		}
		
		if(enterUserCount < 1){
			System.out.println("[채팅방 생성] 실패 - 채팅 참가자가 1명 이하입니다. (chatInfo: " + chatRoomData + ")");
			returnMsg.setData(MessageVO.MSG_ERROR);
			return returnMsg;
		}
		Integer chatId = chatRoomIdIncrement++;
		chatRoomData.setChatRoomId(chatId);
		chatRoomMap.put(chatId, chatRoomData);
		
		System.out.println("[채팅방 생성] 완료 (chatInfo: " + chatRoomData + ")");
		
		returnMsg.setData(MessageVO.MSG_SUCCESS);
		returnMsg.setRoomIdx(chatId);
		
		return returnMsg;
	}
	
	/**
	 * 채팅방에 사용자 추가
	 * @param userData 추가시키는 사용자 데이터
	 * @param msgData 채팅방에 추가시킬 대상의 정보가 들어있는 메시지
	 * @return
	 */
	public boolean addChatRoomUser(UserVO userData, MessageVO msgData){
		if(! MessageVO.MSG_TYPE_ADD_CHATROOM_USER.equals(msgData.getType())){
			return false;
		}
		
		ChatRoomVO chatRoomData = chatRoomMap.get(msgData.getRoomIdx());
		
		if(chatRoomData == null ){// || !chatRoomData.getMemberList().contains(userData)){
			return false;
		}
		
		String addUserIdArray[] = msgData.getData().split(MessageVO.MSG_SPLIT_CHAR);
		
		int addUserCount = 0;
		
		for(String addUserIdString : addUserIdArray){
			try{
				UserVO addUserData = userMap.get(Integer.parseInt(addUserIdString));
				
				if(addUserData == null || addUserData.getEnterChatRoomList().contains(chatRoomData)){
					System.out.println("[채팅방 사용자 추가] 실패 - 사용자를 찾을 수 없거나 이미 채팅방에 사용자가 참가중임" 
									+ "(ChatRoomID: " + chatRoomData.getChatRoomId() + ", UserID: " + addUserIdString + ")");
					continue;
				}
				
				/* 채팅방에 사용자 등록 처리 및 사용자 정보에 참가중인 채팅방에 추가 */
				chatRoomData.addMember(addUserData);
				userData.addEnterChatRoom(chatRoomData);
				System.out.println("[채팅방 사용자 추가] 성공" 
						+ "(ChatRoomID: " + chatRoomData.getChatRoomId() + ", UserID: " + addUserIdString + ")");
				addUserCount++;
			} catch (NumberFormatException e) {
				System.err.println("[채팅방 사용자 추가] 실패 - ID가 숫자가 아님 (" + addUserIdString + ")");
				continue;
			}
		}
		
		if(addUserCount <= 0){
			return false;
		}
		
		/* 채팅방에 접속된 사용자에게 유저 명단이 변경됬다고 알려줄 메시지 생성 및 발송 */
		
		
		return true;
	}
	
	/**
	 * 클라이언트가 보내는 메시지를 수신하는 스레드 생성
	 * @param userInfo
	 */
	public void clientListenerThread(UserVO userInfo){
		Thread th = new Thread(new Runnable() { 
			@Override
			public void run() {
				/* 사용자 정보를 수신 (이름, 상태메시지, 사진 등등) */
				UserProfileVO userProfile = null;
				try {
					System.out.println("[사용자 등록 과정] 시작");
					userProfile = (UserProfileVO)userInfo.getOis().readObject();
					userProfile.setId(userIdIncrement++);
					userInfo.setUserProfile(userProfile);
					
					userMap.put(userProfile.getId(), userInfo);
					
					MessageVO sendMsg = new MessageVO();
					sendMsg.setType(MessageVO.MSG_TYPE_CHANGE_PROFILE);
					sendMsg.setObject(userProfile);
					
					/* 설정된 사용자 정보를 클라이언트에 전송 */
					userInfo.getOos().writeObject(sendMsg);
					
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
						
						System.out.println(msg);
						
						switch (msg.getType()) {
						case MessageVO.MSG_TYPE_TEXT:
							sendMessage(userInfo, msg);
							break;
						case MessageVO.MSG_TYPE_MAKEROOM:
							userInfo.getOos().writeObject(makeRoom(msg));
							break;
						case MessageVO.MSG_TYPE_ADD_CHATROOM_USER:
							addChatRoomUser(userInfo, msg);
							break;
						default:
							System.err.println("[메시지 수신] 알려지지 않은 타입: " + msg.getType());
							break;
						}
						
					} catch (IOException e) {
						// TODO 클라이언트 접속 종료시 처리해야 될 부분 (소캣 끊기, 채팅방에서 나가기 처리, 사용자 리스트에 제외)
						System.out.println("[사용자 접속] 끊어짐 - userInfo " + userInfo);
						allExitChatRoom(userInfo);
						
						try {
							userInfo.getOis().close();
							userInfo.getOos().close();
							userInfo.getIs().close();
							userInfo.getOs().close();
							
							userInfo.getSocket().close();
						} catch (IOException e1) {}
						
						userMap.remove(userProfile.getId());
						break;
					} catch (ClassNotFoundException e) {
						System.err.println("[메시지 수신] 수신받은 데이터 객체가 알 수 없는 타입니다.");
						e.printStackTrace();
					}
				}
			}
		});
		th.start();
	}
	
	/**
	 * 사용자가 속해있는 채팅방에서 모두 퇴장처리 
	 * @param userInfo 사용자 정보
	 */
	private void allExitChatRoom(UserVO userInfo){
		List<ChatRoomVO> enterChatRoomList = userInfo.getEnterChatRoomList();
		
		for(ChatRoomVO roomInfo : enterChatRoomList){
			exitChatRoom(roomInfo, userInfo);
		}
	}
	
	/**
	 * 채팅방에서 회원 퇴장 처리
	 * @param userInfo 퇴장시킬 사용자 정보
	 * @param roomInfo 채팅방 정보
	 */
	private void exitChatRoom(ChatRoomVO roomInfo, UserVO userInfo){
		boolean r1 = roomInfo.removeMember(userInfo);
		boolean r2 = userInfo.removeEnterChatRoom(roomInfo);
		System.out.println("[채팅방 퇴장] 상태 r1: " + r1 + ", r2: " + r2);
	}
}
