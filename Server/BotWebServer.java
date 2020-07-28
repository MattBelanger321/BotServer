import java.io.*;
import java.net.*;
import java.util.LinkedList;

//Linux based Server application
public class BotWebServer{
    public static final int PORT = 55588;
    public ServerSocket serverSocket;   //This application's endpoint
    public Socket client;   //Socket of the application sending files for the Bot
    public File mainScript;
    public File config;
    private String scriptName;
    private String configName;
    private LinkedList<Thread> threads;
    public DataOutputStream dos = null;
    public DataInputStream dis;

    public BotWebServer() throws IOException {
        
        threads = new LinkedList<>();
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
            byte[] file = new byte[size];
            dis.read(file);
            submissionWriter.write(file,0,size);  //receives file from client and saves it on this machine
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
        thread.setName(scriptName);
        thread.setDaemon(true);
        thread.run();
        threads.add(thread);
    }

    private String generateFileName(String nameCpy) {
        return nameCpy.replaceAll(" ","_").replaceAll("\n","");
    }

    public static void main(String[] args) throws IOException {
        BotWebServer server = new BotWebServer();
    }
}