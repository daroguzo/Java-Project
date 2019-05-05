package middleServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

public class Server {
	// 서버 넘버
	static int server_number = 1;

	public static void main(String[] args) {
		try {
			ServerSocket server = new ServerSocket(0525);
			HashMap<String, Object> hash = new HashMap<String, Object>();
			while (true) {
				// 중계서버에게 서버 번호 알리기
				System.out.println(server_number + "번 서버입니다.");

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
}

class ChatThread extends Thread {
	// 일련번호를 담을 HashSet(중복 저장x)
	HashSet<String> numberList = new HashSet<String>();
	Socket sck;
	String id;
	String number;
	BufferedReader br;
	HashMap<String, Object> hash;
	boolean initFlag = false;

	// 서버 넘버
	static int server_number = 1;

	public ChatThread(Socket sck, HashMap<String, Object> hash) {
		this.sck = sck;
		this.hash = hash;

		try {
			// id 받아오기
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(sck.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(sck.getInputStream()));
			id = br.readLine();

			// '/'토큰을 이용해 입력 값 나누기
			StringTokenizer tokens = new StringTokenizer(id);
			id = tokens.nextToken("/");
			number = tokens.nextToken("/");

			// nuberList에 일련번호 저장
			numberList.add(number);

			System.out.println("====(일련번호: " + number + ")" + id + "님과 성공적으로 연결");
			broadcast(id + "님이 접속하셨습니다.");
			synchronized (hash) {// 직렬화 후 해쉬맵에 저장
				hash.put(this.id, pw);

			}
			initFlag = true;

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void run() {
		String line = null;
		try {
			while ((line = br.readLine()) != null) {// 클라이언트로부터 quit을 받으면 종료
				if (line.equals("quit")) {
					System.out.println(id + "님이 시스탬을 종료합니다...");
					// 일련번호 삭제
					numberList.remove(number);

					// 해쉬맵 하나가 사라질 경우 제어서버에게 알려줌
					System.out.println(server_number + "번 서버 현재 접속자: " + (hash.size() - 1));
					break;

				} else {// 아닐 경우 계속 읽어온 데이터를 클라이언트들에게 전송
					broadcast(id + " : " + line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			synchronized (hash) {
				hash.remove(id);
			}
			broadcast(id + "님이 접속을 종료했습니다…");
			try {
				sck.close();
			} catch (IOException e) {
				System.out.println("socket이 정상적으로 종료되지 않았습니다.");
			}
		}
	}

	// 메시지 전송
	public void broadcast(String msg) {
		synchronized (hash) {
			Collection<Object> collec = hash.values();

			java.util.Iterator<?> iter = collec.iterator();
			while (iter.hasNext()) {
				PrintWriter pw = (PrintWriter) iter.next();
				pw.println(msg);
				pw.flush();

			}
		}
	}
}