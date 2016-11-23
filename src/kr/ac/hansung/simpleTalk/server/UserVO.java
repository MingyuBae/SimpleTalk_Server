package kr.ac.hansung.simpleTalk.server;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import kr.ac.hansung.simpleTalk.transformVO.UserProfileVO;

public class UserVO implements Serializable{
	private static final long serialVersionUID = -5733194577087509091L;
	
	private UserProfileVO userProfile;
	private List<ChatRoomVO> enterChatRoomList = new LinkedList<>();
	private Socket socket;
	
	private InputStream is;
	private OutputStream os;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	

	public UserProfileVO getUserProfile() {
		return userProfile;
	}
	public void setUserProfile(UserProfileVO userProfile) {
		this.userProfile = userProfile;
	}
	public List<ChatRoomVO> getEnterChatRoomList() {
		return enterChatRoomList;
	}
	public void setEnterChatRoomList(List<ChatRoomVO> enterChatRoomList) {
		this.enterChatRoomList = enterChatRoomList;
	}
	public boolean addEnterChatRoom(ChatRoomVO chatRoomVO){
		return this.enterChatRoomList.add(chatRoomVO);
	}
	public boolean removeEnterChatRoom(ChatRoomVO chatRoomVO){
		return this.enterChatRoomList.remove(chatRoomVO);
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public InputStream getIs() {
		return is;
	}
	public void setIs(InputStream is) {
		this.is = is;
	}
	public OutputStream getOs() {
		return os;
	}
	public void setOs(OutputStream os) {
		this.os = os;
	}
	public ObjectInputStream getOis() {
		return ois;
	}
	public void setOis(ObjectInputStream ois) {
		this.ois = ois;
	}
	public ObjectOutputStream getOos() {
		return oos;
	}
	public void setOos(ObjectOutputStream oos) {
		this.oos = oos;
	}
}
