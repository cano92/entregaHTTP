package entregaHttp;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RefSocketCode {

	final int port=8000;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	
	private List<String>listErrors;
	private String pathResources;
	
    public RefSocketCode(){
		try{	
			
			serverSocket = new ServerSocket (port);
			System.err.println("se crea el socket port:"+port);
			
			while (true){
				//Accepta la conexión  
				clientSocket = serverSocket.accept();
				System.err.println("... cliente conectado ...");
				this.initVars();
				
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
     * Lee todo el contenido que recibe el socket
     * @throws InterruptedException */
    private void conectionRequest(BufferedReader in) throws IOException, InterruptedException {
    	//controlar que el stream ya esta listo para ser leido en el buffer
    	if (in.ready()) {
    		in.mark(1); //marca para poder usar el reset para imprimir
    		this.processHeaderHtpp( in.readLine().split(" ") );
    		in.reset();
    		
    		this.printConectionrquest(in); 	
    	}
    }
    
    private void processHeaderHtpp(String[] primerLineaHeader) {
    	if (primerLineaHeader != null) { //el header es incorrecto
    		if( !"GET".equals(primerLineaHeader[0]) )
    			//el method http no es GET
    			this.listErrors.add("405");
    		
    		int IndexHttpVersion=1;
    		if(primerLineaHeader.length > 2) {
    			// GET /resources HTTP/1.1     --ejemplo con /path/resources, el path es opcional 
    			this.pathResources = primerLineaHeader[1];
    			IndexHttpVersion++;
    		}
   
    		if( !this.isValidHttpVersion(primerLineaHeader[IndexHttpVersion]) )
    			//la version de http no es 1.0 o 1.1
    			this.listErrors.add("400");
    	}
    }
    
    /**
     * versiones validas de protocolo
     *  -- HTTP/1.0 
     *  -- HTTP/1.1*/
    private boolean isValidHttpVersion(String httpVersion) {
	    if("HTTP/1.0".equals(httpVersion) || "HTTP/1.1".equals(httpVersion))
	    	return true;
	    
	    return false;
    }
    
    /**
     * imprime todo el contenido del buffer
     * */
    private void printConectionrquest(BufferedReader in) throws IOException {
		String s;
		System.err.println("header request cliente");
		while ( (s = in.readLine()) != null) {
			System.out.println(s);
			if (s.isEmpty()) break;
		}

    }
    
    
    /**
     * Escribe la respuesta del Socket en buffer de salida del socket
     * */
    private void conectionResponse(OutputStream clientOutPut) throws IOException {
    	System.err.println("header response server");
    	if(this.listErrors.isEmpty()) {
    		
    		this.printResponsePositive(clientOutPut);
    	}else
    		this.printResponseNegative(clientOutPut);
    }
    
    private void printResponseNegative(OutputStream clientOutPut) throws IOException{
    	this.printBadHeaderResponseHttp(clientOutPut);
    	this.printBadBodyResponseHttp(clientOutPut);
    	clientOutPut.flush();
    }
    
    private void printBadHeaderResponseHttp(OutputStream clientOutPut) throws IOException {
    	if(this.listErrors.get(0)=="405")
    		clientOutPut.write("HTTP/1.0 405 Method Not Allowed \r\n".getBytes() );
    	else
    		clientOutPut.write("HTTP/1.0 400 Bad Request \r\n".getBytes() );
    	// Date: Wed, 16 Mar 2022 15:39 GMT
    	clientOutPut.write(("Date: "+ new Date().toString()+"\r\n").getBytes() );
    	clientOutPut.write("Server: Silly/0.1 \r\n".getBytes() );
    	clientOutPut.write(("Content-Length: "+this.getSizeLengthContentHeader()+"\r\n").getBytes() );
    	clientOutPut.write("Content-Type: text/html \r\n".getBytes() );
    	clientOutPut.write("Connection: Closed \r\n".getBytes() );
    	
    	//separa el header Http del contenido Http --el doc html
		clientOutPut.write("\r\n".getBytes() ); 
		this.logBadHeaderResponseHttp();
    }
    
    private void logBadHeaderResponseHttp() {
    	if(this.listErrors.get(0)=="405")
    		System.out.println("HTTP/1.0 405 Method Not Allowed");
    	else
    		System.out.println("HTTP/1.0 400 Bad Request");
    	System.out.println("Date: "+ new Date().toString());
    	System.out.println("Server: Silly/0.1");
    	System.out.println("Content-Length: "+this.getSizeLengthContentHeader());
    	System.out.println("Content-Type: text/html");
    	System.out.println("Connection: Closed"); 
    }
    
    private void printBadBodyResponseHttp(OutputStream clientOutPut) throws IOException {
    	if(this.listErrors.get(0)=="405")
    		clientOutPut.write("405 Method Not Allowed \r\n".getBytes() );
    	else
    		clientOutPut.write("400 Bad Request \r\n".getBytes() );
    }
    
    private void printResponsePositive(OutputStream clientOutPut) throws IOException{
    	this.printHeaderResponseHttp(clientOutPut);
    	this.printBodyResponseHttp(clientOutPut);
   
		clientOutPut.flush();
    }
    
    private void printHeaderResponseHttp(OutputStream clientOutPut) throws IOException {
    	clientOutPut.write("HTTP/1.1 200 OK \r\n".getBytes() );
    	// Date: Wed, 16 Mar 2022 15:39 GMT
    	clientOutPut.write(("Date: "+ new Date().toString()+"\r\n").getBytes() );
    	clientOutPut.write("Server: Silly/0.1 \r\n".getBytes() );
    	clientOutPut.write(("Content-Length: "+this.getSizeLengthContentHeader()+"\r\n").getBytes() );
    	clientOutPut.write("Content-Type: text/html \r\n".getBytes() );
    	clientOutPut.write("Connection: Closed \r\n".getBytes() );
    	
    	//separa el header Http del contenido Http --el doc html
		clientOutPut.write("\r\n".getBytes() ); 
		this.printLogHeaderResponseHttp();
    }
    
    private void printLogHeaderResponseHttp() {
    	System.out.println("HTTP/1.1 200 OK");
    	System.out.println("Date: "+ new Date().toString());
    	System.out.println("Server: Silly/0.1");
    	System.out.println("Content-Length: "+this.getSizeLengthContentHeader());
    	System.out.println("Content-Type: text/html");
    	System.out.println("Connection: Closed"); 
    }
    
    private int getSizeLengthContentHeader() {
    	//se suma 22, por los tags <html> y <h1> de apertura y cierre
    	if (pathResources != null)return this.pathResources.length()+22; 
    	return 0;
    }
    
    private void printBodyResponseHttp(OutputStream clientOutPut) throws IOException {
    	clientOutPut.write("<HTML><H1>".getBytes() );
		clientOutPut.write(this.getBodytext().getBytes() );
		clientOutPut.write("</H1></HTML>\r\n".getBytes() );
		clientOutPut.write("\r\n".getBytes() );
    }
    
    private String getBodytext() {
    	if (pathResources == null) return "Hola Mundo!";
    	return pathResources;
    }
    
    private void initVars() {
    	this.listErrors = new ArrayList<String>();
    	this.pathResources = null;
    }
    
    public static void main(String[] args){
    	new RefSocketCode();
    }
}
