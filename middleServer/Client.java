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
			//code목록 읽어오기
			System.out.println("코드읽어오기");
			String str = br.readLine();
			System.out.println(str);
			//방번호 입력받기
			code = keyboard.readLine();
			System.out.println("id를 입력하시오:");
			//id 입력받기
			id = keyboard.readLine();
			pw.println(code+"/"+id);//일련번호랑 id 한 스트링에 담아보내기
			pw.flush();
			System.out.println("==========="+id+"님의 대화창=========");
			//서버에 id보내기
			pw.println(id);
			pw.flush();
			//서버로 부터 계속 읽어오는 스레드 실행
			InputThread it = new InputThread(sck,br);
			it.start();
			String line = null;
			while((line = keyboard.readLine())!=null)
			{
				pw.println(code.split("/")[0]+"/"+line);
				pw.flush();
				if(line.equals("quit"))
				{
					System.out.println("시스템을 종료합니다.");
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
	public void run()//스레드로 서버로부터 계속 읽어오기
	{
		try {
			String line = null;
			//null값이 아니면 계속 읽어다 출력해주기
			while((line = br.readLine()) !=null)
			{
				System.out.println(line);
			}
		} catch (IOException e) {
			System.out.println("시스템을 종료합니다.");
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