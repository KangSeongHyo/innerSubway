package com.intern.comment;

import java.sql.Date;
import java.sql.Timestamp;

public class CommentVO {

	private String scode;
	private int entryNum;
	private int commentSeq;
	private String content;
	private String writer;
	private Timestamp registrationDate;

	public CommentVO() {
		super();
	}

	public CommentVO(String scode, int entryNum, int commentSeq, String content, String writer,
		Timestamp registrationDate) {
		super();
		this.scode = scode;
		this.entryNum = entryNum;
		this.commentSeq = commentSeq;
		this.content = content;
		this.writer = writer;
		this.registrationDate = registrationDate;
	}


	public Timestamp getRegistrationDate() {
		return registrationDate;
	}



	public void setRegistrationDate(Timestamp registrationDate) {
		this.registrationDate = registrationDate;
	}



	public String getScode() {
		return scode;
	}

	public void setScode(String scode) {
		this.scode = scode;
	}

	public int getEntryNum() {
		return entryNum;
	}

	public void setEntryNum(int entryNum) {
		this.entryNum = entryNum;
	}

	public int getCommentSeq() {
		return commentSeq;
	}

	public void setCommentSeq(int commentSeq) {
		this.commentSeq = commentSeq;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	
	


}
