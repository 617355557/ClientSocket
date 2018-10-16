
package client;


public class SocketMessage {
	
	private byte[]	message;
	
	private byte[]	head;
	
	private byte[]	len;
	
	/**
	 * 自增长，唯一
	 */
	private byte[]	tag;
	
	private byte[]	cmd;
	
	private byte[]	ut;
	
	private byte[]	s1;
	
	private byte[]	s2;
	
	private byte[]	s3;
	
	private byte[]	data;
	
	public SocketMessage(int length) {
		
		super();
		this.message = new byte[length];
	}
	
	public byte[] getMessage() {
		
		return message;
	}
	
	public void setMessage(byte[] message) {
		
		this.message = message;
	}
	
	public byte[] getHead() {
		
		return head;
	}
	
	public void setHead(byte[] head) {
		
		this.head = head;
	}
	
	public byte[] getLen() {
		
		return len;
	}
	
	public void setLen(byte[] len) {
		
		this.len = len;
	}
	
	public byte[] getTag() {
		
		return tag;
	}
	
	public void setTag(byte[] tag) {
		
		this.tag = tag;
	}
	
	public byte[] getCmd() {
		
		return cmd;
	}
	
	public void setCmd(byte[] cmd) {
		
		this.cmd = cmd;
	}
	
	public byte[] getUt() {
		
		return ut;
	}
	
	public void setUt(byte[] ut) {
		
		this.ut = ut;
	}
	
	public byte[] getS1() {
		
		return s1;
	}
	
	public void setS1(byte[] s1) {
		
		this.s1 = s1;
	}
	
	public byte[] getS2() {
		
		return s2;
	}
	
	public void setS2(byte[] s2) {
		
		this.s2 = s2;
	}
	
	public byte[] getS3() {
		
		return s3;
	}
	
	public void setS3(byte[] s3) {
		
		this.s3 = s3;
	}
	
	public byte[] getData() {
		
		return data;
	}
	
	public void setData(byte[] data) {
		
		this.data = data;
	}
	
	public void initMessage() {
		
		message[0] = head[0];
		message[1] = head[1];
		
		message[2] = len[0];
		message[3] = len[1];
		message[4] = len[2];
		message[5] = len[3];
		
		message[6] = tag[0];
		message[7] = tag[1];
		message[8] = tag[2];
		message[9] = tag[3];
		
		message[10] = cmd[0];
		message[11] = cmd[1];
		
		message[12] = ut[0];
		
		message[13] = s1[0];
		message[14] = s2[0];
		message[15] = s3[0];
		
		if (data != null) {
			for (int i = 0; i < data.length; i++) {
				message[16 + i] = data[i];
			}
		}
	}
}
