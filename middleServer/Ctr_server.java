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
         
         System.out.println("ù �߰輭���� ��ٸ��� �ֽ��ϴ�.");
         Socket sock = server.accept();
         Ctr_thread ctr_thread = new Ctr_thread(sock, hm, middleservers);
         thread = new Thread(ctr_thread);
         thread.start();
         while (true) {
            System.out.println("������ ��ٸ��ϴ�.");
            sock = server.accept();
            for (int i = 0; i < middleservers.size(); i++)
               totaluser = +middleservers.get(i).getCount();
            System.out.println("���� �� ���� �� : " + totaluser);

            if (middleservers.size() == 0) {
               PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
               pw.println("all server down");
               pw.flush();
               System.out.println("�߰輭���� �����ϴ�. Ŭ���̾�Ʈ���� ������ �����մϴ�.");
            }
            
            System.out.println("�߰� ������ ��� �Ǵ� �Ŀ� ������� ����˴ϴ�.");
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