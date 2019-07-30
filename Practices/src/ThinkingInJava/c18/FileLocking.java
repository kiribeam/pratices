import java.nio.channels.*;
import java.util.concurrent.*;
import java.io.*;

public class FileLocking{
  public static void main(String[] args) throws Exception{
    FileOutputStream fos = new FileOutputStream("file.txt");
    FileLock fl = fos.getChannel().tryLock();
    if(fl != null){
      System.out.println("Locked File");
      TimeUnit.MILLISECONDS.sleep(100);
      fl.release();
      System.out.println("Release lock");
    }
    fos.close();
  }
}
