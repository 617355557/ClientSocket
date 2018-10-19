
package client.nb;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import com.alibaba.fastjson.JSONObject;

import client.ClientSocketUtil;
import client.Int2ByteUtil;

/**
 * 客户端读取数据类 用来下发命令和调用北向api
 * 
 * @author Administrator
 *
 */
public class ClientReadThread implements Runnable {
    
    
    private Socket                        socket = null;
    private BufferedInputStream         reader = null;
    private LinkedBlockingQueue<byte[]> queue = null;
    private LinkedBlockingQueue<JSONObject> sendQue = null;
    
    private LinkedBlockingQueue<DataMessage> dataMessageQue = new LinkedBlockingQueue<DataMessage>();
    
    //缓冲区1M
    private byte[] tempBytes = new byte[1024 * 1024];
    private int tempBegin = 0;
    //每次读取数据长度1k
    private int readDataLen = 1024;
    private int siglePackageLen = 0;//提取出包的长度
    private int sequenceLen = 0;//当前缓冲区内数据长度
    
    /**
     * 粘包处理
     * @param RecvLen 读取了多少字节
     * @param ReceiveData 读取到的数据
     * @throws InterruptedException 
     * @throws UnsupportedEncodingException 
     * @throws Exception
     */
    private void receivedPackage(int recvLen,byte[] receiveData) throws UnsupportedEncodingException, InterruptedException {
        long startTime = System.currentTimeMillis();
        //将收到的数据copy拼接至缓冲区
        System.arraycopy(receiveData, 0, tempBytes, sequenceLen, recvLen);
        //记录当前缓冲区长度
        sequenceLen += recvLen;
        long endTime = System.currentTimeMillis();
        System.out.println("将收到的数据copy拼接至缓冲区耗时:"+(endTime-startTime));
        
        //是否能提取出下一包数据的长度，这一步保证后续操作能解析出这一包数据的长度，当前场景head+len=6
        //解析头部
        if(sequenceLen < 6) {
            return;
        }
        while(siglePackageLen <= sequenceLen) {
            
            int tempEnd = tempBegin+6;
            if(tempEnd>sequenceLen) {
                //头部不足
                System.out.println("头部不足");
                byte[] b = Arrays.copyOfRange(tempBytes, tempBegin, sequenceLen);
                tempBytes = new byte[256 * 1024];
                System.arraycopy(b, 0, tempBytes, 0, b.length);
                siglePackageLen=0;
                sequenceLen = b.length;//新的缓存区数据长度
                tempBegin = 0;
                return;
            }
            byte[] head = Arrays.copyOfRange(tempBytes, tempBegin, tempEnd);
            
            // 读取完头部 [下一次起始位置] 从 [上一次的结束位置] 开始
            tempBegin = tempEnd;
            byte[] len = new byte[4];
            len[0] = head[2];
            len[1] = head[3];
            len[3] = head[4];
            len[4] = head[5];
            //计算单包长度
            siglePackageLen = Int2ByteUtil.byteArrayToInt(len);
            //读取完单包长度，结束位置为包尾
            tempEnd = tempBegin + siglePackageLen - 6;
            
            if( tempEnd > sequenceLen ) {
                //结束位置>缓冲区内数据长度
                System.out.println("结束位置>缓冲区内数据长度");
                byte[] bodyByte = Arrays.copyOfRange(tempBytes, tempBegin, sequenceLen);
                tempBytes = new byte[256 * 1024];
                System.arraycopy(head, 0, tempBytes, 0, head.length);
                System.arraycopy(bodyByte, 0, tempBytes, head.length, bodyByte.length);
                siglePackageLen=0;
                sequenceLen = head.length + bodyByte.length;//新的缓存区数据长度
                tempBegin = 0;
                return;
            } else if(tempEnd == sequenceLen) {
                //结束位置=缓冲区内数据长度
                System.out.println("结束位置=缓冲区内数据长度");
                byte[] bodyByte = Arrays.copyOfRange(tempBytes, tempBegin, tempEnd);
                bodyHandler(siglePackageLen, bodyByte);
                tempBytes = new byte[256 * 1024];
                sequenceLen = 0;
                siglePackageLen = 0;
                tempBegin = 0;
                return;
            } else {
                //结束位置<缓冲区内数据长度
                byte[] bodyByte = Arrays.copyOfRange(tempBytes, tempBegin, tempEnd);
                //处理逻辑，放到队列
                bodyHandler(siglePackageLen, bodyByte);
                //读取完body,移动至下一包，记录tempBegin = tempEnd;
                tempBegin = tempEnd;
                siglePackageLen = 0;
            }
        }
        
        endTime = System.currentTimeMillis();
        System.out.println("解析一个包耗时:"+(endTime-startTime));
    }
    
    /**
     *	客户端读取数据类 用来调用北向api
     * 
     * @param socket
     */
    public ClientReadThread(Socket socket, LinkedBlockingQueue<byte[]> queue, LinkedBlockingQueue<JSONObject> sendQue) {
        super();
        this.socket = socket;
        this.queue = queue;
        this.sendQue = sendQue;
        try {
            this.reader = new BufferedInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("client read thread constructor:"+e.getMessage());
        }
    }
    
    public static void main(String[] args) {
		System.out.println(new byte[] {0x0F}[0]-'0');
	}
    
    @Override
    public void run() {
        
        while (true) {
            try {
                long startTime = System.currentTimeMillis();
                
            	byte[] receiveData = new byte[readDataLen];
            	int recvLen = reader.read(receiveData);
            	long endTime = System.currentTimeMillis();
            	
            	System.out.println("解析耗时："+(endTime-startTime)+"\n");
            	
            	receivedPackage(recvLen, receiveData);
            	
                // 解析数据包
//                long startTime = System.currentTimeMillis();
//                
//                byte[] head = new byte[] { 0, 0};
//                reader.read(head, 0, 2);
//                
//                byte[] len = new byte[] { 0, 0, 0, 0 };
//                reader.read(len, 0, len.length);
//                int length = Int2ByteUtil.byteArrayToInt(len);
//                
//                int surplusLen = length-2-4;//-head-length
//                //剩余字节长度
//                byte[] surplusByte = new byte[surplusLen];
//                reader.read(surplusByte, 0, surplusLen);
//                
//                long endTime = System.currentTimeMillis();
//                System.out.println("解析一个包耗时："+(endTime-startTime));
//                
//                new Thread(new Runnable() {
//					@Override
//					public void run() {
//						try {
//							bodyHandler(length, surplusByte);
//						} catch (UnsupportedEncodingException | InterruptedException e) {
//							System.out.println(e.getMessage());
//						}
//					}
//				}).start();
                
            } catch (Exception e) {
                System.out.println("client read thread:"+e.getMessage());
                try {
                    reader.close();reader=null;
                    socket.close();socket=null;
                } catch (IOException e1) {
                    System.out.println(e1.getMessage());
                }
                break;
            }
        }
    }

    /**
     * body逻辑处理
     * @param tail 读取的长度
     * @param length 总长度
     * @param surplusByte body字节数组
     * @throws InterruptedException
     * @throws UnsupportedEncodingException
     */
	private void bodyHandler(int length, byte[] surplusByte)
			throws InterruptedException, UnsupportedEncodingException {
	    long startTime = System.currentTimeMillis();
		/**
		 * tag 0-3
		 * cmd 4,5
		 * ut 6
		 * s1 7
		 * s2 8
		 * s3 9
		 */
		byte[] tag = new byte[] { 0, 0, 0, 0 };
		tag[0]=surplusByte[0];
		tag[1]=surplusByte[1];
		tag[2]=surplusByte[2];
		tag[3]=surplusByte[3];
		/**
		 *	指令 服务端心跳：0x03 客户端心跳回复：0x02 客户端发送消息：0x06
		 */
		byte[] cmd = new byte[] { 0, 0, 0, 0 };
		cmd[0]=surplusByte[4];
		cmd[1]=surplusByte[5];
		int command = Int2ByteUtil.byteArrayToInt(cmd);
		
		byte[] ut = new byte[] { 0, 0, 0, 0 };
		ut[0] = surplusByte[6];
		// 预留字段
//		byte[] s1 = new byte[] { 0, 0, 0, 0 };
//		byte[] s2 = new byte[] { 0, 0, 0, 0 };
//		byte[] s3 = new byte[] { 0, 0, 0, 0 };
		
		if (command == 0x66) {
			//数据回复
		    queue.put(ClientSocketUtil.sendDataReply(cmd, tag, ut, new byte[]{0x01}));
		    int datalen = length - 0x10;
		    byte[] data = null;
		    if (datalen > 0) {
		        data = new byte[datalen];
		        for(int datai=0;datai<datalen;datai++) {
		            data[datai]=surplusByte[10+datai];
		        }
		        String lineString = new String(data);
		        // 服务端发送消息包处理
		        JSONObject json = JSONObject.parseObject(lineString.trim());
		        sendQue.put(json);
		        long endTime = System.currentTimeMillis();
                System.out.println("body处理耗时："+(endTime-startTime));
		    }
		    
		}
	}
    
	
	private class DataMessage{
	    private int sequenceLen;
	    private byte[] b;
	    
        public int getSequenceLen() {
            
            return sequenceLen;
        }
        public void setSequenceLen(int sequenceLen) {
            
            this.sequenceLen = sequenceLen;
        }
        
        public byte[] getB() {
            
            return b;
        }
        
        public void setB(byte[] b) {
            
            this.b = b;
        }
        
        public DataMessage(int sequenceLen, byte[] b) {
            
            super();
            this.sequenceLen = sequenceLen;
            this.b = b;
        }
	}
}
