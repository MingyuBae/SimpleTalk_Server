package kr.ac.hansung.simpletalk.transformVO;

import java.io.Serializable;

public class MessageVO implements Serializable{
	private static final long serialVersionUID = -6090068067415581658L;
	
	public static final String MSG_TYPE_TEXT = "TEXT";
	public static final String MSG_TYPE_STICKER = "STICKER";
	public static final String MSG_TYPE_IMAGE = "IMG";
	public static final String MSG_TYPE_MAKEROOM = "MAKEROOM";
	public static final String MSG_TYPE_EXITROOM = "EXITROOM";
	public static final String MSG_TYPE_QUIT = "QUIT";
	public static final String MSG_TYPE_CHANGE_PROFILE ="CHANGE_PROFILE";
	public static final String MSG_TYPE_ADD_CHATROOM_USER = "ADD_CHATROOM_USER";
	
	public static final String MSG_SPLIT_CHAR = "|";
	public static final String MSG_SUCCESS = "SUCCESS";
	public static final String MSG_ERROR = "ERROR";
	
	private Integer senderId;
	private String type;
	private Integer roomIdx;
	private String data;
	private Serializable object;
	
	public Integer getSenderId() {
		return senderId;
	}
	public void setSenderId(Integer senderId) {
		this.senderId = senderId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getRoomIdx() {
		return roomIdx;
	}
	public void setRoomIdx(Integer roomIdx) {
		this.roomIdx = roomIdx;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Serializable getObject() {
		return object;
	}
	public void setObject(Serializable object) {
		this.object = object;
	}
	
	@Override
	public String toString() {
		return "MessageVO [type=" + type + ", roomIdx=" + roomIdx + ", data=" + data + ", object=" + object + "]";
	}
}
