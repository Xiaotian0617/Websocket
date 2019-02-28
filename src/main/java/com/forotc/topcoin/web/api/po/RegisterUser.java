package com.forotc.topcoin.web.api.po;

public class RegisterUser {
	
	 private String uid;
	 private String tcid;
	 
	 public void setUid(String uid) {
		 this.uid = uid;
	 }

	 public String getUid() {
		 return uid;
	 }
	 
	 public void setTcid(String tcid) {
		 this.tcid = tcid;
	 }
	 
	 public String getTcid() {
		 return tcid;
	 }
}
