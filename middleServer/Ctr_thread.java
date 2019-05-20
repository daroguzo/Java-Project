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
         // firstmsg 형식 connection_p/id
         // connection_a/id
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      if (firstmsg[0].equals("connection_p")) {
         id = firstmsg[1];
         System.out.println("PC 일련번호 : " + id + " 가 연결을 요청중입니다.");
         pw.println("hi pc");
         pw.flush();
         synchronized (hm) {
            hm.put(id, pw);
         }
      } else if (firstmsg[0].equals("connection_a")) {
         id = firstmsg[1];
         Middleserver miniserver;
         System.out.println("Android 일련번호 : " + id + " 가 연결을 요청하였습니다.");
         pw.println("hi android");
         pw.flush();
         synchronized (hm) {
            if (hm.get(id) != null) {
               System.out.println("들어옴");
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
         System.out.println("중계서버 : " + ip + " 연결되었습니다.");
         port = Integer.parseInt(firstmsg[1]);
         pw.println(ip+"/"+port);
         pw.flush();
         System.out.println("중계서버에게 보낸 메시지: "+ip+"/"+port);
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
      /* msg 형식
         
         relay서버일경우
         relay server/connection termination   : relay 서버와 연결종료
         relay server/increase/ip              : 해당 ip의 relay 서버의 인원이 증가
         relay server/decrease/ip              : 해당 ip의 relay 서버의 인원이 감소
          
         client일경우
         client/success                        : 해당 클라이언트가 중계서버 연결에 성공
         client/fail                           : 해당 클라이언트가 중계서버 연결에 실패
         
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
                  System.out.println("클라이언트가 중계서버와 연결에 성공하였습니다");
                  System.out.println("해당 클라이언트와 연결을 해제합니다.");
                  br.close();
                  sock.close();
                  break;
               }
               else if(split[1].equals("fail")){
                  System.out.println("클라이언틍와 중계서버가 연결에 실패했습니다");
                  System.out.println("해당 클라인트와의 재연결을 해야합니다.");
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
      System.out.println("미니서버 리턴"+minimum.toString());
      return minimum;
   }

   public void sendtomsg(String id, String msg) {

   }

}