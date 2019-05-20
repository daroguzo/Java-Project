package middleServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;
import java.util.Iterator.*;
public class Server {
	int room1 = 0;
	int room2 = 0;
	int room3 = 0;
	HashMap<String, PrintWriter> hash;
	//static PrintWriter pwr;
	//제어서버에 보낼 때 사용할 socket,pw
	Socket controlSck = null;
	PrintWriter pwr = null;
	public Server() {
		try {
			//제어서버에 socket연결
			controlSck = new Socket("localhost",10001);
			pwr = new PrintWriter(new OutputStreamWriter(controlSck.getOutputStream()));
			ControlThread control = new ControlThread(controlSck, pwr);
			control.start();
				
			ServerSocket server = new ServerSocket(1525);
			hash = new HashMap<String, PrintWriter>();
			//hash맵 키값불러와서 각 방마다 몇명인지 구하기
			
			
			
			while(true)
			{
				System.out.println("=======================");
				System.out.println("현재 서버에   "+hash.size()+"명...");
				//hash맵 개수 구하기
				

				/*
				 * System.out.println("100번방:  "+room1+"명...");
				 * System.out.println("101번방:  "+room2+"명...");
				 * System.out.println("102번방:  "+room3+"명...");
				 */
				System.out.println("접속을 기다리는중...");
				Socket sck = server.accept();
				ChatThread chatThr = new ChatThread(sck, hash);
				chatThr.start();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new Server();
	}
	

}
//제어서버와 연결되는 스레드
class ControlThread extends Thread
{
	Socket sck;
	BufferedReader br;
	PrintWriter pwr;
	

	public ControlThread(Socket sck, PrintWriter pwr) 
	{
		super();
		this.sck = sck;
		this.pwr = pwr;
		try {
			br = new BufferedReader(new InputStreamReader(sck.getInputStream()));
			System.out.println(sck);
			pwr.println("relay server/1525");
			System.out.println("println() 실행");
			pwr.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void run() 
	{
		String line = null;
		try {
			while((line = br.readLine()) != null)
			{
				System.out.println("--------제어서버: "+line);
			
				
			//클라이언트로부터 quit을 받으면 종료
				if(line.equals("quit"))
					sck.close();
			}
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("제어서버와 연결 끊김...");
		}
	}
}
class ChatThread extends Thread{
	Socket sck;
	String [] split;
	String code;
	BufferedReader br;
	HashMap<String, PrintWriter> hash;
	boolean initFlag = false;
	public ChatThread(Socket sck,HashMap<String, PrintWriter> hash) {
		
		this.sck = sck;
		this.hash = hash;
		try {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(sck.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(sck.getInputStream()));
			pw.println("Connection is success");
			pw.flush();
			pw.println("Please press your code number(100, 101, 102)");
			pw.flush();
			System.out.println("코드보내기");
			code = br.readLine();
			split = code.split("/");
			System.out.println("===="+split[1]+"님과 성공적으로 연결("+split[0]+"번방)");
			
			
			//broadcast(split[1]+"is connected.");
			synchronized (hash) {//직렬화 후 해쉬맵에 저장
				hash.put(code, pw);
			}
			initFlag = true;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	public void run() {
		String line;
		/*
		 * msg 형식
		 * 일련번호/a|p/msg
		 */
		try {
			while((line = br.readLine()) != null) {
				String[] split = line.split("/");
				// 일련번호가 같을 경우
				if(split[0].equals(code)) {
					// 안드로이드에서 송신
					if(split[1].equals("a")) {
						// 상대편으로 저장
						split[1] = "p";
						//split[2] = "명령어";
						// 확인
						System.out.println(line);
					
					}// pc에서 송신
					else if(split[1].equals("p")) {
						// 상대편으로 저장
						split[1] = "a";
						//split[2] = "명령어";
						// 확인
						System.out.println(line);
					}
				}else {
					// 일련번호가 없다면 Do Nothing
				}
			}
		} catch (IOException e) {
			System.out.println(split[0] + split[1] +"님이 시스탬을 강제적으로 종료합니다...");
		}finally {
			//제어서버에 "relay server/decrease" 보내기
			
			
			//broadcast(split[1]+"is disconnected...");
			
			synchronized (hash) {
				hash.remove(code);
			}
			System.out.println("=======================");
			System.out.println("현재 서버에   "+hash.size()+"명...");
			System.out.println("=======================");
			//hash맵 개수 구하기
			
			
			try {
				sck.close();
			} catch (IOException e) {
				System.out.println("socket이 정상적으로 종료되지 않았습니다.");
			}
		}
	}
	/*
	//메시지 전송
	public void broadcast(String msg)
	{
		synchronized (hash) 
		{
			Set<?> set = hash.keySet();
			
			java.util.Iterator<?> iterator = set.iterator();
			PrintWriter pw = null;
			while(iterator.hasNext())
			{
				//같은 고유번호 끼리만 메세지 주고받기
				  String key = (String)iterator.next();
				  if(key.split("/")[0].equals(msg.split("/")[0]))
				  {
					  pw = (PrintWriter)hash.get(key);
					  pw.println(split[1]+":  "+msg.split("/")[1]);
					  pw.flush();
				  }
			}
		}
	}
	*/
}
