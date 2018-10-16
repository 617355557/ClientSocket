package client;

import java.util.Map;



public class ClientSocketUtil {
    
    /**
     * 发送心跳包
     * @param tag
     * @return
     */
    public static byte[] sendHeart(int tag) {
        SocketMessage socketMessage=new SocketMessage(16);
        socketMessage.setHead(Int2ByteUtil.intToByteArray(0xAAAA));
        socketMessage.setLen(Int2ByteUtil.intToByteArray(0x10));
        socketMessage.setTag(Int2ByteUtil.intToByteArray(tag));
        socketMessage.setCmd(Int2ByteUtil.intToByteArray(0x01));
        socketMessage.setUt(Int2ByteUtil.intToByteArray(0x0b));
        socketMessage.setS1(Int2ByteUtil.intToByteArray(0x00));
        socketMessage.setS2(Int2ByteUtil.intToByteArray(0x00));
        socketMessage.setS3(Int2ByteUtil.intToByteArray(0x00));
        socketMessage.initMessage();
        return socketMessage.getMessage();
    }
    
    /**
     * 心跳回复包
     * @param callbackData
     * @param tag
     * @param cmd
     * @return
     */
    public static byte[] sendHeartReply(int tag) {
        SocketMessage socketMessage = new SocketMessage(16);
        socketMessage.setHead(Int2ByteUtil.intToByteArray(0xAAAA));
        socketMessage.setLen(Int2ByteUtil.intToByteArray(0x10));
        socketMessage.setTag(Int2ByteUtil.intToByteArray(tag));
        socketMessage.setCmd(Int2ByteUtil.intToByteArray(0x04));
        socketMessage.setUt(Int2ByteUtil.intToByteArray(0x0b));
        socketMessage.setS1(Int2ByteUtil.intToByteArray(0x00));
        socketMessage.setS2(Int2ByteUtil.intToByteArray(0x00));
        socketMessage.setS3(Int2ByteUtil.intToByteArray(0x00));
        socketMessage.initMessage();
        return socketMessage.getMessage();
    }
    
    /**
     * 上报数据包
     * @param data
     * @return
     */
    public static byte[] sendDataMessage(byte[] data){
        SocketMessage socketMessage = new SocketMessage(16 + data.length);
        socketMessage.setHead(Int2ByteUtil.intToByteArray(0xAAAA));
        socketMessage.setLen(Int2ByteUtil.intToByteArray(16 + data.length));
        socketMessage.setTag(Int2ByteUtil.intToByteArray(CommonUtils.getNum()));
        socketMessage.setCmd(Int2ByteUtil.intToByteArray(0x65));
        socketMessage.setUt(Int2ByteUtil.intToByteArray(0x0b));
        socketMessage.setS1(Int2ByteUtil.intToByteArray(0x00));
        socketMessage.setS2(Int2ByteUtil.intToByteArray(0x00));
        socketMessage.setS3(Int2ByteUtil.intToByteArray(0x00));
        socketMessage.setData(data);
        socketMessage.initMessage();
        return socketMessage.getMessage();
    }
    
    public static byte[] sendDataReply(byte[] cmd,byte[] tag,byte[] ut,byte[] data){
        SocketMessage socketMessage = new SocketMessage(16 + data.length);
        socketMessage.setHead(Int2ByteUtil.intToByteArray(0xAAAA));
        socketMessage.setLen(Int2ByteUtil.intToByteArray(16 + data.length));
        socketMessage.setTag(tag);
        socketMessage.setCmd(cmd);
        socketMessage.setUt(ut);
        socketMessage.setS1(Int2ByteUtil.intToByteArray(0x00));
        socketMessage.setS2(Int2ByteUtil.intToByteArray(0x00));
        socketMessage.setS3(Int2ByteUtil.intToByteArray(0x00));
        socketMessage.setData(data);
        socketMessage.initMessage();
        return socketMessage.getMessage();
    }
    
    /**
     * 发送离线电表
     * @param data
     * @return
     */
    public static byte[] sendOfflineMeters(byte[] data){
        SocketMessage socketMessage = new SocketMessage(16 + data.length);
        socketMessage.setHead(Int2ByteUtil.intToByteArray(0xAAAA));
        socketMessage.setLen(Int2ByteUtil.intToByteArray(16 + data.length));
        socketMessage.setTag(Int2ByteUtil.intToByteArray(CommonUtils.getNum()));
        socketMessage.setCmd(Int2ByteUtil.intToByteArray(0x6B));
        socketMessage.setUt(Int2ByteUtil.intToByteArray(0x0b));
        socketMessage.setS1(Int2ByteUtil.intToByteArray(0x00));
        socketMessage.setS2(Int2ByteUtil.intToByteArray(0x00));
        socketMessage.setS3(Int2ByteUtil.intToByteArray(0x00));
        socketMessage.setData(data);
        socketMessage.initMessage();
        return socketMessage.getMessage();
    }
}
