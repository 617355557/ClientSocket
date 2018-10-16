
package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;


public class ClientWriteThread implements Runnable {
    
    
    private OutputStream                writer;
    
    private LinkedBlockingQueue<byte[]> queue;
    
    private Socket socket = null;
    
    /**
     * 客户端发送数据类 用来上报数据
     * 
     * @param socket
     */
    public ClientWriteThread(Socket socket,OutputStream writer, LinkedBlockingQueue<byte[]> queue) {
        super();
        this.queue = queue;
        this.writer = writer;
        this.socket = socket;
    }
    
    @Override
    public void run() {
        
        while (true) {
            byte[] lastTimeElement = null;
            try {
                //防止下次写数据的时候write关闭，将queue元素take出来
                if(!socket.isClosed()) {
                    lastTimeElement=queue.take();
                    writer.write(lastTimeElement);
                    writer.flush();
                }else {
                    throw new Exception("socket关闭");
                }
            } catch (Exception e) {
                //将queue.take对象放回去
                System.out.println("client write thread:"+e.getMessage());
                try {
                    queue.put(lastTimeElement);
                    writer.close();
                } catch (IOException | InterruptedException e1) {
                    writer=null;
                    System.out.println("关闭writer异常："+e1.getMessage());
                }
                break;
            }
        }
    }
    
}
