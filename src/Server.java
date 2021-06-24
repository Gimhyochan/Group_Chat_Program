import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class Server {

    private static final String EXIT = "EXIT";
    public static Path CreateLogFile() throws IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = new Date();
        String now = simpleDateFormat.format(time);
        return Files.createFile(Path.of(now + ".log"));
    }
    public static ServerSocketChannel OpenServer(Selector selector) throws IOException {
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("localhost", 5001));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        return serverSocket;
    }
    public static void NewClientConnected(ServerSocketChannel serverSocket, Selector selector, Path path) throws IOException {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("new client connected: " + client.getRemoteAddress().toString());
        Files.write(path, ("new client connected: " + client.getRemoteAddress().toString() + "\n").getBytes(), StandardOpenOption.APPEND);

    }
    public static void main(String[] args) throws IOException {
        //System.out.println("Selector");
        System.out.println("Chatting Server running...");
        Path path = CreateLogFile();
        Charset charset = Charset.forName("UTF-8");

        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = OpenServer(selector);

        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    NewClientConnected(serverSocket, selector, path);
                }

                if (key.isReadable()) {
                    try {
                        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                        SocketChannel client = (SocketChannel) key.channel();
                        System.out.println(client.getRemoteAddress());
                        int byteCount = client.read(byteBuffer);
                        byteBuffer.flip();
                        if (byteCount == -1) {
                            Files.write(path, ("Unintended EXIT : " + client.getRemoteAddress().toString()+"\n").getBytes(), StandardOpenOption.APPEND);
                            client.close();
                        }
                        if (new String(byteBuffer.array()).trim().equals(EXIT)) {
                            //client.write(charset.encode("EXIT"));
                            Files.write(path, ("Client EXIT Request : " + client.getRemoteAddress().toString()+"\n").getBytes(), StandardOpenOption.APPEND);
                            client.close();
                        }
                        String message = charset.decode(byteBuffer).toString();
                        message = CheckMessage(message);
                        message = client.getRemoteAddress().toString() + " : " + message;
                        message = message.replace("127.0.0.1", "localhost");
                        Files.write(path, (message+"\n").getBytes(), StandardOpenOption.APPEND);
                        Set<SelectionKey> selectorkeys_Selectionkey = selector.keys();
                        Iterator<SelectionKey> iterator2 = selectorkeys_Selectionkey.iterator();
                        while (iterator2.hasNext()) {
                            SelectionKey passto = iterator2.next();
                            SocketChannel passClient = null;
                            try {
                                passClient = (SocketChannel) passto.channel();

                                if (passClient != client) {
                                    if (passClient.toString() != serverSocket.toString()) {
                                        System.out.println("Message Send to" + passClient.getRemoteAddress().toString() + ": "+ message);
                                        byteBuffer = charset.encode(message);
                                        passClient.write(byteBuffer);
                                    }
                                }
                            } catch (Exception e) {}
                        }
                        byteBuffer.clear();
                    }catch(Exception e){
                        SocketChannel client = (SocketChannel) key.channel();
                        client.close();
                    }
                }
                iterator.remove();
            }
        }
    }
    public static String CheckMessage(String message){
        message = message.replace("fuck", "\uD83D\uDE0A");
        return message.replace("\uD83D\uDE0A"+"ing", "\uD83D\uDE0A");
    }
}
