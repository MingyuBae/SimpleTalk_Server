package kr.ac.hansung.simpleTalk.transformVO;

import java.io.Serializable;

public class UserProfileVO implements Serializable{
	private static final long serialVersionUID = -6685433305922446988L;
	
	private Integer id;
	private String name;
	private String stateMsg;
	private String imgFileName;
	
	public UserProfileVO() {
		id = -1;
		name = "";
		stateMsg = "";
		imgFileName = "";
	}
	
	
	public UserProfileVO(Integer id, String name, String stateMsg, String imgFileName){
		this.id = id;
		this.name = name;
		this.stateMsg = stateMsg;
		this.imgFileName = imgFileName;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStateMsg() {
		return stateMsg;
	}
	public void setStateMsg(String stateMsg) {
		this.stateMsg = stateMsg;
	}
	public String getImgFileName() {
		return imgFileName;
	}
	public void setImgFileName(String imgFileName) {
		this.imgFileName = imgFileName;
	}
}
