import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

import static java.lang.System.exit;

public class Client {
    public static SocketChannel socketChannel = null;

    public void Client_module() throws IOException, InterruptedException {
        this.socketChannel = SocketChannel.open();
        this.socketChannel.configureBlocking(true);
        //System.out.println("연결요청");
        this.socketChannel.connect(new InetSocketAddress("localhost", 5001));
        //System.out.println("연결 성공");
        System.out.println("Enter Chatting Room");

        Runnable receiveMSG = new Runnable() {
            @Override
            public void run() {
                SocketChannel socketChannel1 = Client.socketChannel;
                while (true) {
                    if (!socketChannel1.isConnected()){
                        exit(0);
                    }
                    ByteBuffer byteBuffer = null;
                    Charset charset = Charset.forName("UTF-8");
                    byteBuffer = ByteBuffer.allocate(256);
                    try {
                        int byteCount = socketChannel1.read(byteBuffer);
                        if (byteCount == -1){
                            exit(0);
                        }
                        if (byteCount == 0) {
                            continue;
                        }
                        byteBuffer.flip();
                        String message = charset.decode(byteBuffer).toString();
                        System.out.println(message);
                        if (message == "EXIT"){
                            exit(0);
                        }


                    } catch (IOException e) {
                    }
                }
            }
        };
        Runnable sendMSG = new Runnable() {
            @Override
            public void run() {
                SocketChannel socketChannel2 = Client.socketChannel;
                while (true) {
                    if (!socketChannel2.isConnected()){
                        return;
                    }
                    ByteBuffer byteBuffer = null;
                    Charset charset = Charset.forName("UTF-8");
                    Scanner scanner = new Scanner(System.in);
                    String input = scanner.nextLine().toString();
                    byteBuffer = charset.encode(input);
                    if (input == "EXIT"){
                        return;
                    }
                    try {
                        socketChannel2.write(byteBuffer);
                    } catch (IOException e) {
                    }
                }
            }
        };

        Thread receive = new Thread(receiveMSG);
        Thread send = new Thread(sendMSG);
        receive.start();
        send.start();

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("NewClient");
        Client client = new Client();
        client.Client_module();
    }
}
