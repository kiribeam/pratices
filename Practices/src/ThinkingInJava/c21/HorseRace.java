import java.util.concurrent.*;
import java.util.*;

class Horse implements Runnable{
  private static int counter = 0;
  private final int id = counter ++;
  private int strides = 0;
  private static CyclicBarrier barrier;
  private static Random rand = new Random(47);
  public Horse(CyclicBarrier cy){
    barrier = cy;
  }


  public synchronized int getStrides(){
    return strides;
  }
  @Override
  public void run(){
    try{
      while(!Thread.interrupted()){
        synchronized(this){
          strides += rand.nextInt(3);
        }
        barrier.await();
      }
    }catch(InterruptedException e){
      ;
    }catch(BrokenBarrierException e){
      throw new RuntimeException(e);
    }
  }
  @Override
  public String toString(){
    return "Horse " + id + "  ";
  }
  public String tracks(){
    StringBuilder s = new StringBuilder();
    for(int i=0; i<getStrides(); i++)
      s.append("*");
    s.append(id);
    return s.toString();
  }
}

public class HorseRace{
  static final int FinishLine = 75;
  private List<Horse> horses = new ArrayList<Horse>();
  private ExecutorService ex = Executors.newCachedThreadPool();
  private CyclicBarrier barrier;
  public HorseRace(int nHorses, final int pause){
    barrier = new CyclicBarrier(nHorses, new Runnable(){
      @Override
      public void run(){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<FinishLine; i++)
          sb.append("=");
        System.out.println(sb);
        for(Horse horse : horses)
          System.out.println(horse.tracks());
        for(Horse horse: horses)
          if(horse.getStrides() >= FinishLine){
            System.out.println(horse + "won!");
            ex.shutdownNow();
            return;
          }
        try{
          TimeUnit.MILLISECONDS.sleep(pause);
        }catch(InterruptedException e){
          System.out.println("barrier-action sleep interrupted");
        }
      }
    });
    for(int i=0; i<nHorses; i++){
      Horse horse = new Horse(barrier);
      horses.add(horse);
      ex.execute(horse);
    }
  }

  public static void main(String[] args) {
    int nHorses = 7;
    int pause = 200;
    if(args.length > 0){
      int n = new Integer(args[0]);
      nHorses = n>0 ? n : nHorses;
    }
    if(args.length > 1){
      int p = new Integer(args[1]);
      pause = p>-1 ? p : pause;
    }
    new HorseRace(nHorses, pause);
  }
}
