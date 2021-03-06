import java.io.*;
import java.net.*;
import java.util.*;

//Linux based Server application
public class BotWebServer{
    public static final int PORT = 55588;
    public ServerSocket serverSocket;   //This application's endpoint
    public Socket client;   //Socket of the application sending files for the Bot
    public File mainScript;
    public File config;
    private String scriptName;
    private final Hashtable<Thread,RunPython> threads;
    public DataOutputStream dos = null;
    public DataInputStream dis;

    public BotWebServer() throws IOException {
        
        threads = new Hashtable<>();
        while(true){
            String dir = ".";
            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("Awaiting Connection...");
                client = serverSocket.accept(); //returns the client socket that is trying to connect to this server
                System.out.println("Connected");
                dos = new DataOutputStream(client.getOutputStream());    //output stream sends info to client
                dis = new DataInputStream(client.getInputStream());    //output stream reads info to client
                
                dir = dis.readUTF(); //directory pathName for this bots files
                
                new File(dir).delete();
                new File(dir).mkdir();
                
                getSubmission(dir);    //get Python script
                getConfig(dir);        //get configuration file for this bot
                
                dos.close();
                dis.close();

            } catch(Exception e) {
                e.printStackTrace();
            }

            runSubmission(dir);
            client.close();
            serverSocket.close();
        }
    }

    private void getConfig(String dir) {
        try {
            generateFileName(dis.readUTF()); //removes special characters
            config = readFile(".txt",dir,"config"); //reads file from client
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private void getSubmission(String dir){
        try {
            scriptName = generateFileName(dis.readUTF()); //removes special characters
            mainScript = readFile(".py",dir,dir); //reads file from client
            System.out.println(dir);
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    private File readFile(String ext,String dir,String name) throws IOException {
        DataOutputStream submissionWriter = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(dir+'/'+name+ext))));
        try{
            int size = dis.readInt();
            int tSize;
            System.out.println("File Size Read: "+size);
            byte[] file = new byte[size];
            if (( tSize = dis.read(file) )== size) { //reads file sent from client into byte array
                System.out.println("file read");
                submissionWriter.write(file, 0, size);  //saves file received from client on this machine
            }else{
                System.err.println("FILE READ ERROR " + tSize + " bytes read instead of "+size);
            }
        }catch(EOFException e){
            System.err.println("EOF was reached");
        }
        
        submissionWriter.close();
        System.out.println("FILE RECEIVED");
        dos.writeUTF("Server Recieved File");
        return new File(scriptName);
    }

    private void runSubmission(String dir){
        RunPython rp = new RunPython(scriptName,dir);
        Thread thread = new Thread(rp);
        Set<Thread> s = threads.keySet();
        for(Thread t: s){
            System.out.println(t.getName());
            if(t.getName().equals(scriptName)){
                threads.get(t).p.destroy();
                System.out.println("Thread Destroyed");
            }
        }
        thread.setName(scriptName);
        thread.setDaemon(true);
        thread.start();
        System.out.println("Bot running");
        threads.put(thread,rp);
    }

    private String generateFileName(String nameCpy) {
        return nameCpy.replaceAll(" ","_").replaceAll("\n","");
    }

    public static void main(String[] args) throws IOException {
        new BotWebServer();
    }

}
