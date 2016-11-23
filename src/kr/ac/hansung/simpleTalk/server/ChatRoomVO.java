package kr.ac.hansung.simpleTalk.server;

import java.util.List;

public class ChatRoomVO {
	private String roomName;
	private List<UserVO> memberList;
	
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public List<UserVO> getMemberList() {
		return memberList;
	}
	public void setMemberList(List<UserVO> memberList) {
		this.memberList = memberList;
	}
	
	public boolean addMember(UserVO userVO){
		return this.memberList.add(userVO);
	}
	public boolean removeMember(UserVO userVO){
		return this.memberList.remove(userVO);
	}
}
