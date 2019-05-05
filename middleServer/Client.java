package middleServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class Client {

	public static void main(String[] args) {
		
		Socket sck = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		boolean endFlag = false;
		// 클라이언트의 입력값을 받을 변수 input
		String input = null;
		// 사용자의 아이디  
		String id = null;
		// 사용자의 일련번호  
		String number = null;
		try {
			sck = new Socket("localhost", 0525);
			pw = new PrintWriter(new OutputStreamWriter(sck.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(sck.getInputStream()));
			
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("사용할 id를 입력하시오:(아이디숫자4자리/일련번호)");
			input = keyboard.readLine();
			
			// '/'토큰을 이용해 입력 값 나누기
			StringTokenizer tokens = new StringTokenizer(input);
			id = tokens.nextToken("/");
			number = tokens.nextToken("/");

			
			System.out.println("===========(일련번호: "+number+")"+id+"님의 대화창=========");
			
			// 서버에 id보내기
			pw.println(input);
			pw.flush();
			
			//서버로 부터 계속 읽어오는 스레드 실행
			InputThread it = new InputThread(sck,br);
			it.start();
			String line = null;
			while((line = keyboard.readLine())!=null)
			{
				pw.println(line);
				pw.flush();
				if(line.equals("quit"))
				{
					System.out.println("시스템을 종료합니다.");
					endFlag = true;
					break;
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			e.printStackTrace();
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