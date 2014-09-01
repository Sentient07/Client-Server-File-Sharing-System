/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ftpjava;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author ramana
 */
public class FTPClient implements Runnable{

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        // TODO code application logic here
        
    }

    @Override
    public void run() {
        try {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            Socket cSock = new Socket("localhost", 8880);
            System.out.println("Connecting to the server..");
            final OutputStream ostream = cSock.getOutputStream();
            
            new Thread(new Runnable(){

                @Override
                public void run() {
                    Scanner inputScanner = new Scanner(System.in);
                    String message;
                    while((message = inputScanner.nextLine()) != null){
                        try {
                            File fileObject;
                            if((fileObject = returnsFileobject(message))!= null){
                                BufferedInputStream bistream = new BufferedInputStream(
                                        new FileInputStream(fileObject));
                                byte[] bytearray = new byte[(int) fileObject.length()];
                                bistream.read(bytearray, 0, (int) fileObject.length());
                                ostream.write(bytearray);
                                ostream.flush();
                                ostream.close();
                            }
                            else {
                                System.out.println(
                                        "The command is !file:<Path To the file>");
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

            }).start();
            InputStream istream ;
            while((istream = cSock.getInputStream())!= null){
                StoresFile(istream);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(FTPClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private synchronized File returnsFileobject(String message) 
            throws FileNotFoundException{
        
        final String pattern = "^!file:";
        //final String pattern2 = "^!search:";
        Pattern ptr = Pattern.compile(pattern);
        //Pattern ptr2 = Pattern.compile(pattern2);
        Matcher match = ptr.matcher(message);
        String[] patharray;
        File file_object ;
        if(!(match.find())){
            patharray = message.split(":", 2);
            file_object = new File(patharray[1]);
        }
        else{
            file_object = null;
        } 
        return file_object;
        
    }
    private void StoresFile(InputStream is) throws IOException{
        int max_size = 99999;
        byte[] byarr = new byte[max_size];
        BufferedOutputStream bostream =  new BufferedOutputStream
        (new FileOutputStream("received"));
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
   
}
