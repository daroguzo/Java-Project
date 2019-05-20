package middleServer;

import java.net.InetAddress;

public class Middleserver {
   String ip;
   int port;
   int count;
   

   public Middleserver(String ip,int port) {
      this.ip=ip;
      this.port=port;
   }

   public String getIp() {
      return ip;
   }

   public int getPort() {
      return port;
   }

   public int getCount() {
      return count;
   }
   
   public void increase_Count() {
      this.count = this.count +1;
   }
   public void decrease_Count() {
      this.count = this.count -1;
   }
}