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
	//������� ���� �� ����� socket,pw
	Socket controlSck = null;
	PrintWriter pwr = null;
	public Server() {
		try {
			//������� socket����
			controlSck = new Socket("localhost",10001);
			pwr = new PrintWriter(new OutputStreamWriter(controlSck.getOutputStream()));
			ControlThread control = new ControlThread(controlSck, pwr);
			control.start();
				
			ServerSocket server = new ServerSocket(1525);
			hash = new HashMap<String, PrintWriter>();
			//hash�� Ű���ҷ��ͼ� �� �渶�� ������� ���ϱ�
			
			
			
			while(true)
			{
				System.out.println("=======================");
				System.out.println("���� ������   "+hash.size()+"��...");
				//hash�� ���� ���ϱ�
				

				/*
				 * System.out.println("100����:  "+room1+"��...");
				 * System.out.println("101����:  "+room2+"��...");
				 * System.out.println("102����:  "+room3+"��...");
				 */
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
	public static void main(String[] args) {
		new Server();
	}
	

}
//������� ����Ǵ� ������
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
			System.out.println("println() ����");
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
				System.out.println("--------�����: "+line);
			
				
			//Ŭ���̾�Ʈ�κ��� quit�� ������ ����
				if(line.equals("quit"))
					sck.close();
			}
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("������� ���� ����...");
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
			System.out.println("�ڵ庸����");
			code = br.readLine();
			split = code.split("/");
			System.out.println("===="+split[1]+"�԰� ���������� ����("+split[0]+"����)");
			
			
			//broadcast(split[1]+"is connected.");
			synchronized (hash) {//����ȭ �� �ؽ��ʿ� ����
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
		 * msg ����
		 * �Ϸù�ȣ/a|p/msg
		 */
		try {
			while((line = br.readLine()) != null) {
				String[] split = line.split("/");
				// �Ϸù�ȣ�� ���� ���
				if(split[0].equals(code)) {
					// �ȵ���̵忡�� �۽�
					if(split[1].equals("a")) {
						// ��������� ����
						split[1] = "p";
						//split[2] = "��ɾ�";
						// Ȯ��
						System.out.println(line);
					
					}// pc���� �۽�
					else if(split[1].equals("p")) {
						// ��������� ����
						split[1] = "a";
						//split[2] = "��ɾ�";
						// Ȯ��
						System.out.println(line);
					}
				}else {
					// �Ϸù�ȣ�� ���ٸ� Do Nothing
				}
			}
		} catch (IOException e) {
			System.out.println(split[0] + split[1] +"���� �ý����� ���������� �����մϴ�...");
		}finally {
			//������� "relay server/decrease" ������
			
			
			//broadcast(split[1]+"is disconnected...");
			
			synchronized (hash) {
				hash.remove(code);
			}
			System.out.println("=======================");
			System.out.println("���� ������   "+hash.size()+"��...");
			System.out.println("=======================");
			//hash�� ���� ���ϱ�
			
			
			try {
				sck.close();
			} catch (IOException e) {
				System.out.println("socket�� ���������� ������� �ʾҽ��ϴ�.");
			}
		}
	}
	/*
	//�޽��� ����
	public void broadcast(String msg)
	{
		synchronized (hash) 
		{
			Set<?> set = hash.keySet();
			
			java.util.Iterator<?> iterator = set.iterator();
			PrintWriter pw = null;
			while(iterator.hasNext())
			{
				//���� ������ȣ ������ �޼��� �ְ�ޱ�
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
