import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Init {
    public static void main(String[] args) throws Exception{
        Scanner sc = new Scanner(System.in);
        //String username = sc.nextLine();
        while(true){
            init();
            int option = sc.nextInt();
            String[] strings = new String[3];
            switch(option){
                case 1:
                    Client.main(strings);
                    return;
                case 2:
                    Server.main(strings);
                    return;
                case 3:
                    NewFeatures();
                    break;
                case 4:
                    Credit();
                    break;
                default:
                    System.out.println("Out of Range (1~4)");
            }
        }


    }
    public static void init(){
        System.out.println("Chatting Service Dev 1.0");
        System.out.print("1. Connect to Server\n" +
                "2. Create new chatting server\n" +
                "3. New Features\n" +
                "4. Credit\n>> ");
    }
    public static void NewFeatures() throws IOException {
        Path relativePath = Paths.get("");
        String path = relativePath.toAbsolutePath().toString();
        BufferedReader introduceNewFeatures = new BufferedReader(new FileReader(path + "/NewFeatures.txt"));
        String line;
        while ((line = introduceNewFeatures.readLine()) != null){
            System.out.println(line);
        }
    }
    public static void Credit(){
        System.out.println("Developer: KUS 2019270124 김효찬\n");
    }
}
