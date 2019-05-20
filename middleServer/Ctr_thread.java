package middleServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Ctr_thread implements Runnable {

   Socket sock;
   BufferedReader br;
   PrintWriter pw;
   HashMap<String, PrintWriter> hm;
   String[] firstmsg;
   String id;
   String ip;
   int port;

   ArrayList<Middleserver> middleservers;

   public Ctr_thread(Socket sock, HashMap<String, PrintWriter> hm, ArrayList<Middleserver> server_count) {
      this.sock = sock;
      this.hm = hm;
      this.middleservers = server_count;

      try {
         br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
         pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
         ip = sock.getInetAddress().toString();
         port = sock.getPort();
         String msg = br.readLine();
         firstmsg = msg.split("/");
         // firstmsg ���� connection_p/id
         // connection_a/id
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      if (firstmsg[0].equals("connection_p")) {
         id = firstmsg[1];
         System.out.println("PC �Ϸù�ȣ : " + id + " �� ������ ��û���Դϴ�.");
         pw.println("hi pc");
         pw.flush();
         synchronized (hm) {
            hm.put(id, pw);
         }
      } else if (firstmsg[0].equals("connection_a")) {
         id = firstmsg[1];
         Middleserver miniserver;
         System.out.println("Android �Ϸù�ȣ : " + id + " �� ������ ��û�Ͽ����ϴ�.");
         pw.println("hi android");
         pw.flush();
         synchronized (hm) {
            if (hm.get(id) != null) {
               System.out.println("����");
               miniserver = what_miniserver(middleservers);
               pw.println("connect/" + miniserver.getIp()+"/"+port);
               pw.flush();
               PrintWriter pcpw = hm.get(id);
               pcpw.println("connect/" + miniserver.getIp()+"/"+port);
               pcpw.flush();
               hm.remove(id);
            } else {
               pw.println("error/id do not match");
            }
         }
      } else if (firstmsg[0].equals("relay server")) {
         ip=ip.substring(1);
         System.out.println("�߰輭�� : " + ip + " ����Ǿ����ϴ�.");
         port = Integer.parseInt(firstmsg[1]);
         pw.println(ip+"/"+port);
         pw.flush();
         System.out.println("�߰輭������ ���� �޽���: "+ip+"/"+port);
      //   synchronized (middleservers) {
            middleservers.add(new Middleserver(ip, port));
      //   }
      }
      
      else {
         System.out.println(firstmsg);
      }
   }

   @Override
   public void run() {
      String msg;
      /* msg ����
         
         relay�����ϰ��
         relay server/connection termination   : relay ������ ��������
         relay server/increase/ip              : �ش� ip�� relay ������ �ο��� ����
         relay server/decrease/ip              : �ش� ip�� relay ������ �ο��� ����
          
         client�ϰ��
         client/success                        : �ش� Ŭ���̾�Ʈ�� �߰輭�� ���ῡ ����
         client/fail                           : �ش� Ŭ���̾�Ʈ�� �߰輭�� ���ῡ ����
         
      */
      try {
         while ((msg = br.readLine()) != null) {
            String[] split = msg.split("/");
            if (split[0].equals("relay server")) {
               if (split[1].equals("connection termination")) {
                  br.close();
                  sock.close();
                  break;
               } else if (split[1].equals("decrease")) {
                  for (Middleserver m : middleservers) {
                     if (m.getIp().equals(split[2]))
                        m.decrease_Count();
                  }
                  System.out.println("server : "+split[2]+ " decrease");
               }
               else if(split[1].equals("increase")) {
                  for(Middleserver m : middleservers) {
                     if (m.getIp().equals(split[2]))
                        m.increase_Count();
                  }
                  System.out.println("server : "+split[2]+ " increase");
               }
            }else if(split[1].equals("client")){
               if(split[1].equals("success")) {
                  System.out.println("Ŭ���̾�Ʈ�� �߰輭���� ���ῡ �����Ͽ����ϴ�");
                  System.out.println("�ش� Ŭ���̾�Ʈ�� ������ �����մϴ�.");
                  br.close();
                  sock.close();
                  break;
               }
               else if(split[1].equals("fail")){
                  System.out.println("Ŭ���̾�v�� �߰輭���� ���ῡ �����߽��ϴ�");
                  System.out.println("�ش� Ŭ����Ʈ���� �翬���� �ؾ��մϴ�.");
                  br.close();
                  sock.close();
                  break;
               }
            }
         }
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public Middleserver what_miniserver(ArrayList<Middleserver> middleservers) {
   //   Middleserver minimum = null;
      Middleserver minimum = middleservers.get(0);
      int mini = middleservers.get(0).getCount();
//      for (Middleserver m : middleservers) {
//         if (mini > m.getCount())
//            minimum = m;
//      }
      System.out.println("�̴ϼ��� ����"+minimum.toString());
      return minimum;
   }

   public void sendtomsg(String id, String msg) {

   }

}