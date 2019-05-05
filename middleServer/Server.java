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
	// ���� �ѹ�
	static int server_number = 1;

	public static void main(String[] args) {
		try {
			ServerSocket server = new ServerSocket(0525);
			HashMap<String, Object> hash = new HashMap<String, Object>();
			while (true) {
				// �߰輭������ ���� ��ȣ �˸���
				System.out.println(server_number + "�� �����Դϴ�.");

				System.out.println("������ ��ٸ�����...");
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
	// �Ϸù�ȣ�� ���� HashSet(�ߺ� ����x)
	HashSet<String> numberList = new HashSet<String>();
	Socket sck;
	String id;
	String number;
	BufferedReader br;
	HashMap<String, Object> hash;
	boolean initFlag = false;

	// ���� �ѹ�
	static int server_number = 1;

	public ChatThread(Socket sck, HashMap<String, Object> hash) {
		this.sck = sck;
		this.hash = hash;

		try {
			// id �޾ƿ���
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(sck.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(sck.getInputStream()));
			id = br.readLine();

			// '/'��ū�� �̿��� �Է� �� ������
			StringTokenizer tokens = new StringTokenizer(id);
			id = tokens.nextToken("/");
			number = tokens.nextToken("/");

			// nuberList�� �Ϸù�ȣ ����
			numberList.add(number);

			System.out.println("====(�Ϸù�ȣ: " + number + ")" + id + "�԰� ���������� ����");
			broadcast(id + "���� �����ϼ̽��ϴ�.");
			synchronized (hash) {// ����ȭ �� �ؽ��ʿ� ����
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
			while ((line = br.readLine()) != null) {// Ŭ���̾�Ʈ�κ��� quit�� ������ ����
				if (line.equals("quit")) {
					System.out.println(id + "���� �ý����� �����մϴ�...");
					// �Ϸù�ȣ ����
					numberList.remove(number);

					// �ؽ��� �ϳ��� ����� ��� ��������� �˷���
					System.out.println(server_number + "�� ���� ���� ������: " + (hash.size() - 1));
					break;

				} else {// �ƴ� ��� ��� �о�� �����͸� Ŭ���̾�Ʈ�鿡�� ����
					broadcast(id + " : " + line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			synchronized (hash) {
				hash.remove(id);
			}
			broadcast(id + "���� ������ �����߽��ϴ١�");
			try {
				sck.close();
			} catch (IOException e) {
				System.out.println("socket�� ���������� ������� �ʾҽ��ϴ�.");
			}
		}
	}

	// �޽��� ����
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