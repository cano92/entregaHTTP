package entregaHttp;

import java.io.*;
import java.net.*;

public class RefSocketCliCode {

    
    public static void main(String[] args)
    {
	// TODO Auto-generated method stub
	new RefSocketCliCode();
    }
    
    public RefSocketCliCode()
    {
	try
	    {
		byte buf[] = new byte[1];
		
		Socket sock = new Socket(InetAddress.getByName("127.0.0.1"), 8000);
                // connect() ya se hizo implícito
		InputStream in =  sock.getInputStream();
		OutputStream out = sock.getOutputStream();
		out.write('A');
		in.read(buf);
		sock.close();
	    }
    catch (Exception e)
	{
	    e.printStackTrace();
	}
    }
}

