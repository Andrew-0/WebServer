import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class HttpServer {
    public static void main(String[] args) throws Throwable{
        ServerSocket ss = new ServerSocket(8081);
        while (true){
            Socket s = ss.accept();
            System.err.println("Client accepted");
            new Thread(new SocketProcessor(s)).start();
        }
    }

    private static class SocketProcessor implements Runnable {
        private Socket s;
        private InputStream is;
        private OutputStream os;

        private SocketProcessor(Socket s) throws Throwable {
            this.s = s;
            this.is = s.getInputStream();
            this.os = s.getOutputStream();
        }

        public void run(){
            try{
                readInputHeaders();
                writeResponse("<html><body><h1>Hello from Habr!</h1></body></html>");
            } catch (Throwable t){
                // do nothing
            } finally {
                try {
                    s.close();
                } catch(Throwable t){
                    // do nothing
                }
            }

            System.err.println("Client processing finihed");
        }

        public void writeResponse(String s) throws Throwable {
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Server: YarServer/2017-03-29\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Connection: close\r\n\r\n";
            String result = response + s;
            os.write(result.getBytes());
            os.flush();
        }

        private void readInputHeaders() throws Throwable{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            while(true){
                String string = bufferedReader.readLine();
                if (string == null || string.trim().length() == 0){
                    break;
                }
            }
        }
    }
}
