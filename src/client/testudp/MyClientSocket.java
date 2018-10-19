package client.testudp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;

import client.Int2ByteUtil;

public class MyClientSocket {
    
    private int port = 20000;
//    private String host = "192.168.0.110";
    private InetAddress host;
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
    
    private DatagramSocket socket = null;
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
                        System.out.println("开始连接udp");
                        socket = new DatagramSocket(); 
                        host = InetAddress.getByName("193.112.183.70");
//                        host = InetAddress.getByName("localhost");
                        DatagramPacket outputPacket=new DatagramPacket("CCCC0000".getBytes(),
                                "CCCC0000".getBytes().length,host,port);
                        socket.send(outputPacket);
                        System.out.println("数据发送完毕");
                        //先准备一个空数据报文
                        String msg="";
                        DatagramPacket inputPacket=new DatagramPacket(new byte[4],4);
                          try {
                              //阻塞语句，有数据就装包，以装完或装满为此.
                              socket.receive(inputPacket);
                              //从报文中取出字节数据并装饰成字符。
                             System.out.println(Int2ByteUtil.byte2StringHex(inputPacket.getData()));
                          } catch (IOException ex) {
                              msg=null;
                          }
                        System.out.println("udp执行完一次循环");
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
