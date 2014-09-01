/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ftpjava;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author ramana
 */
public class Server implements Runnable {

    public static void main(String [] args) throws IOException{
        new Thread(new Server()).start();
    }
    @Override
    public void run() {

        try {
            while (true){
                ServerSocket servSocket = new ServerSocket(8880);
                Socket fSock = servSocket.accept();
                long clientid = 0;
                new Thread(new ClientHandler(fSock, clientid)).start();
                
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    

}

class ClientHandler implements Runnable {
    private Socket fsock;
    private final long clientid;
    
    public ClientHandler(Socket sock, long cid){
        this.fsock = sock;
        this.clientid = cid;
    }
    @Override
    public void run() {
        InputStream istream;
        try {
            long fileId = 0;
            while((istream = fsock.getInputStream())!=null){
                StoresFile(istream, fileId);
                fileId += 1;
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //StoresFile on the server 
    void StoresFile(InputStream is, long fileId) throws IOException{
        int max_size = 99999;
        byte[] byarr = new byte[max_size];
        String filename = String.valueOf(clientid) + "Saved the file" + String.valueOf(fileId);
        BufferedOutputStream bostream =  new BufferedOutputStream
        (new FileOutputStream(filename));
        int bysize, currentSize = 0;
        bysize = is.read(byarr, 0, byarr.length);
        currentSize = bysize;
        
        do {
            bysize = is.read(byarr, currentSize, (bysize-currentSize));
            if(bysize > 0){
                currentSize += bysize;
            }
            
        }while(bysize>-1);
        /* use this for storing */
        bostream.write(byarr, 0, bysize);
        bostream.flush();
        bostream.close();

    }
   
    private void sendFile(OutputStream os, String PathToFile) throws IOException{
        File file_object = new File(PathToFile);
        BufferedInputStream bistream = new BufferedInputStream
        (new FileInputStream(file_object));
        byte[] bytearray = new byte[(int) file_object.length()];
        bistream.read(bytearray, 0, (int) file_object.length());
        os.write(bytearray);
        os.flush();
    }


}