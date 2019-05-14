package middleServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	public static void main(String[] args) {
		Socket sck = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		boolean endFlag = false;
		String id = null;
		String code = null;
		try {
			//10.10.101.153
			sck = new Socket("192.168.0.7", 1525);
			pw = new PrintWriter(new OutputStreamWriter(sck.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(sck.getInputStream()));
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
			//code��� �о����
			System.out.println("�ڵ��о����");
			String str = br.readLine();
			System.out.println(str);
			//���ȣ �Է¹ޱ�
			code = keyboard.readLine();
			System.out.println("id�� �Է��Ͻÿ�:");
			//id �Է¹ޱ�
			id = keyboard.readLine();
			pw.println(code+"/"+id);//�Ϸù�ȣ�� id �� ��Ʈ���� ��ƺ�����
			pw.flush();
			System.out.println("==========="+id+"���� ��ȭâ=========");
			//������ id������
			pw.println(id);
			pw.flush();
			//������ ���� ��� �о���� ������ ����
			InputThread it = new InputThread(sck,br);
			it.start();
			String line = null;
			while((line = keyboard.readLine())!=null)
			{
				pw.println(code.split("/")[0]+"/"+line);
				pw.flush();
				if(line.equals("quit"))
				{
					System.out.println("�ý����� �����մϴ�.");
					endFlag = true;
					break;
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(br != null)
					br.close();
				if(pw != null)
					pw.close();
				if(sck != null)
					sck.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
	
}
class InputThread extends Thread
{
	Socket sck = null;
	BufferedReader br = null;
	public InputThread(Socket sck, BufferedReader br) {
		super();
		this.sck = sck;
		this.br = br;
	}
	public void run()//������� �����κ��� ��� �о����
	{
		try {
			String line = null;
			//null���� �ƴϸ� ��� �о�� ������ֱ�
			while((line = br.readLine()) !=null)
			{
				System.out.println(line);
			}
		} catch (IOException e) {
			System.out.println("�ý����� �����մϴ�.");
		}finally {
			try {
				if(sck != null)
					sck.close();
				if(br !=null)
					br.close();
					
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
}