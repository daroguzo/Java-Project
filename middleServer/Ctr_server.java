package middleServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Ctr_server {
   int port = 10001;
   ServerSocket server = null;
   Socket sock = null;
   HashMap<String, PrintWriter> hm;
   ArrayList<Middleserver> middleservers;
   int totaluser;
   int miniserver;
   boolean allserverdown;
   Thread thread;
   
   public Ctr_server() {
      try {
         server = new ServerSocket(port);
         hm = new HashMap<String, PrintWriter>();
         middleservers = new ArrayList<Middleserver>();
         
         System.out.println("첫 중계서버를 기다리고 있습니다.");
         Socket sock = server.accept();
         Ctr_thread ctr_thread = new Ctr_thread(sock, hm, middleservers);
         thread = new Thread(ctr_thread);
         thread.start();
         while (true) {
            System.out.println("접속을 기다립니다.");
            sock = server.accept();
            for (int i = 0; i < middleservers.size(); i++)
               totaluser = +middleservers.get(i).getCount();
            System.out.println("현재 총 유저 수 : " + totaluser);

            if (middleservers.size() == 0) {
               PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
               pw.println("all server down");
               pw.flush();
               System.out.println("중계서버가 없습니다. 클라이언트와의 연결을 해제합니다.");
            }
            
            System.out.println("중계 서버일 경우 판단 후에 제어서버에 연결됩니다.");
               thread = new Thread(new Ctr_thread(sock, hm, middleservers));
               thread.start();
         }
      } catch (Exception e) {
         // TODO: handle exception
      }

   }

   public static void main(String[] args) {
      new Ctr_server();
   }
}