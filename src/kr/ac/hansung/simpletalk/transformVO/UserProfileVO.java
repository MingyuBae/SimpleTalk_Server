package kr.ac.hansung.simpletalk.transformVO;

import java.io.Serializable;

public class UserProfileVO implements Serializable{
	private static final long serialVersionUID = -6685433305922446988L;

	private Integer id;
	private String name;
	private String stateMsg;
	private String imgFileName;
	private Boolean enable;

	public UserProfileVO() {
		id = -1;
		name = "";
		stateMsg = "";
		imgFileName = "";
		enable = false;
	}

	public UserProfileVO(Integer id, String name, String stateMsg, String imgFileName, Boolean enable){
		this.id = id;
		this.name = name;
		this.stateMsg = stateMsg;
		this.imgFileName = imgFileName;
		this.enable = enable;
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
	public Boolean getEnable() {
		return enable;
	}
	public void setEnable(Boolean enable) {
		this.enable = enable;
	}


	@Override
	public String toString() {
		return "UserProfileVO [id=" + id + ", name=" + name + ", stateMsg=" + stateMsg + ", imgFileName=" + imgFileName
				+ ", enable=" + enable + "]";
	}
}
