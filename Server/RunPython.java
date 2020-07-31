import java.io.*;
import java.util.*;

public class RunPython implements Runnable{
    private String fileName;
    private String dir;
    public Process p = null;
    
    public RunPython(String fileName,String dir){
        this.fileName = fileName;
        this.dir = dir;
    }

    @Override
    public void run() {
        LinkedList<String> commands = new LinkedList<>();
        commands.add("python3");
        commands.add(fileName);
        String arg[] = new String[1];
        try{
            p = Runtime.getRuntime().exec(commands.toArray(arg),null,new File("./"+dir)); //runs python script
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
