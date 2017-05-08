package com.cinlan.xview.bean;

/**
 * 消息实体类
 * 
 * @author Chong
 * 
 */
public class ConfMessage {

	private String IsAutoReply;
	private String MessageID;
	private String FontColor;
	private String FontName;
	private String FontSize;
	private String FontStyle;
	private String NewLine;
	private String FontIndex;
	private String Text;

	private long nGroupID;
	private int nBusinessType;
	private long nFromUserID;
	private long nTime;
	private int nLength;

	public ConfMessage() {
		super();
	}

	public ConfMessage(String isAutoReply, String messageID, String fontColor,
			String fontName, String fontSize, String fontStyle, String newLine,
			String fontIndex, String text, long nGroupID, int nBusinessType,
			long nFromUserID, long nTime, int nLength) {
		super();
		IsAutoReply = isAutoReply;
		MessageID = messageID;
		FontColor = fontColor;
		FontName = fontName;
		FontSize = fontSize;
		FontStyle = fontStyle;
		NewLine = newLine;
		FontIndex = fontIndex;
		Text = text;
		this.nGroupID = nGroupID;
		this.nBusinessType = nBusinessType;
		this.nFromUserID = nFromUserID;
		this.nTime = nTime;
		this.nLength = nLength;
	}

	public String getIsAutoReply() {
		return IsAutoReply;
	}

	public void setIsAutoReply(String isAutoReply) {
		IsAutoReply = isAutoReply;
	}

	public String getMessageID() {
		return MessageID;
	}

	public void setMessageID(String messageID) {
		MessageID = messageID;
	}

	public String getFontColor() {
		return FontColor;
	}

	public void setFontColor(String fontColor) {
		FontColor = fontColor;
	}

	public String getFontName() {
		return FontName;
	}

	public void setFontName(String fontName) {
		FontName = fontName;
	}

	public String getFontSize() {
		return FontSize;
	}

	public void setFontSize(String fontSize) {
		FontSize = fontSize;
	}

	public String getFontStyle() {
		return FontStyle;
	}

	public void setFontStyle(String fontStyle) {
		FontStyle = fontStyle;
	}

	public String getNewLine() {
		return NewLine;
	}

	public void setNewLine(String newLine) {
		NewLine = newLine;
	}

	public String getFontIndex() {
		return FontIndex;
	}

	public void setFontIndex(String fontIndex) {
		FontIndex = fontIndex;
	}

	public String getText() {
		return Text;
	}

	public void setText(String text) {
		Text = text;
	}

	public long getnGroupID() {
		return nGroupID;
	}

	public void setnGroupID(long nGroupID) {
		this.nGroupID = nGroupID;
	}

	public int getnBusinessType() {
		return nBusinessType;
	}

	public void setnBusinessType(int nBusinessType) {
		this.nBusinessType = nBusinessType;
	}

	public long getnFromUserID() {
		return nFromUserID;
	}

	public void setnFromUserID(long nFromUserID) {
		this.nFromUserID = nFromUserID;
	}

	public long getnTime() {
		return nTime;
	}

	public void setnTime(long nTime) {
		this.nTime = nTime;
	}

	public int getnLength() {
		return nLength;
	}

	public void setnLength(int nLength) {
		this.nLength = nLength;
	}

	@Override
	public String toString() {
		return "Message [IsAutoReply=" + IsAutoReply + ", MessageID="
				+ MessageID + ", FontColor=" + FontColor + ", FontName="
				+ FontName + ", FontSize=" + FontSize + ", FontStyle="
				+ FontStyle + ", NewLine=" + NewLine + ", FontIndex="
				+ FontIndex + ", Text=" + Text + "]";
	}

}
