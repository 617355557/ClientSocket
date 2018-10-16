package client;

import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;

public class MyClientSocket {
    
    private int port = 17921;
//    private String host = "192.168.0.110";
    private String host = "localhost";
    private LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue<byte[]>();
    private LinkedBlockingQueue<JSONObject> sendQueue = new LinkedBlockingQueue<JSONObject>();
    //下发消息的线程池
//  private ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(50, 300, 3, 
            TimeUnit.SECONDS, 
            new LinkedBlockingQueue<Runnable>(), 
            new MyRejectedExecutionHandler());
    
    private class MyRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            new Thread(r,"新线程"+new Random().nextInt(10)).start();
        }
    }
    
    private Socket socket = null;
    private OutputStream writer;

    public LinkedBlockingQueue<byte[]> getQueue() {
        return queue;
    }

    public void setQueue(LinkedBlockingQueue<byte[]> queue) {
        this.queue = queue;
    }

    public void start() {
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                new Thread(new ClientSocket()).start();
            }
        }).start();
    }
    
    public void sendCommand() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//              threadPoolExecutor.allowCoreThreadTimeOut(true);
                while(true) {
                    try {
                        JSONObject json = sendQueue.take();
                        //下发命令
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println(json);
                                Thread.yield();
                            }
                        }).start();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    
    private class ClientSocket implements Runnable{
        @Override
        public void run() {
            while (true) {
                try {
                    if (socket == null || socket.isClosed()) {
                        socket = new Socket(host, port);//连接socket
                        socket.setTcpNoDelay(true);
                        writer=socket.getOutputStream();
                        if(socket!=null) {
                            new Thread(new ClientReadThread(socket,queue,sendQueue)).start();
//                            new Thread(new ClientWriteThread(socket,writer,queue)).start();
                            sendCommand();
                        }
                    }
                } catch (Exception e) {
                    if(e instanceof ConnectException) {
                        continue;
                    }else {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public static void main(String[] args) {
        
        new MyClientSocket().start();
    }
}
