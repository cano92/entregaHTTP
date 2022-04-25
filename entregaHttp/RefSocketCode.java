package entregaHttp;
import java.io.*;
import java.net.*;

public class RefSocketCode {

	final int port=8000;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	
    public RefSocketCode(){
		try{	
			
			serverSocket = new ServerSocket (port);
			System.err.println("se crea el socket port:"+port);
			
			while (true){
				//Accepta la conexión  
				clientSocket = serverSocket.accept();
				System.err.println("cliente conectado");
				
				BufferedReader in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()) );
				this.conectionRequest(in);
				
				this.conectionResponse( clientSocket.getOutputStream() );
				
				clientSocket.close();
				System.err.println("conexion con cliente finalizada");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
    }
    
    /**
     * Lee todo el contenido que recibe el socket*/
    private void conectionRequest(BufferedReader in) throws IOException {
    	this.printConectionrquest(in);
    }
    
    /**
     * imprime todo el contenido del buffer
     * */
    private void printConectionrquest(BufferedReader in) throws IOException {
		String s;
		while ( (s = in.readLine()) != null) {
			System.out.println(s);
			if (s.isEmpty()) break;
		}
    }
    
    
    /**
     * Escribe la respuesta del Socket en buffer de salida del socket
     * */
    private void conectionResponse(OutputStream clientOutPut) throws IOException {
		clientOutPut.write("HTTP/1.1 200 OK \r\n".getBytes() );
		clientOutPut.write("\r\n".getBytes() );
		clientOutPut.write("<b>hola mundo</b>".getBytes() );
		clientOutPut.write("\r\n\r\n".getBytes() );
		clientOutPut.flush();
    }
    
}
