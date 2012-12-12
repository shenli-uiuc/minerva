package secon.sim;

import secon.minerva.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

public class Scheduler{
    
    private UserNodes userNodes = null;
    private Sink sink = null;

    //the file is currently under "/home/shenli/Dropbox/SECON2013/data/coor.txt"
    private String finPath = null;
    private int simCnt = 0;
    private int dim = 0;

    private Random rand = null;

    public Scheduler(String finPath, int simCnt){
        this.sink = new Sink(Configure.SINK_LOC, Configure.SINK_RANGE, Configure.SINK_NUM);        

        this.userNodes = new UserNodes();
        this.finPath = finPath;
        this.simCnt = simCnt;
        this.dim = Configure.DIM;
        this.rand = new Random();
    }

    private int getRandPktCnt(){
        return rand.nextInt(Configure.MAX_PKT - Configure.MIN_PKT + 1) + Configure.MIN_PKT;
    }

    private double syncData(UserNode user, Sink sink){
        //System.out.println("before ceate arraylist " + user + ", " + sink);
        ArrayList<KAry> userData = user.getTrace();
        ArrayList<KAry> sinkData = sink.getData();

        //System.out.println("before gen user");
        MetaGen userGen = new MetaGen(userData, Configure.INTERVAL, Configure.HI_RATIO);
        MetaData userMeta = userGen.generate();

        //System.out.println("before gen sink");
        MetaGen sinkGen = new MetaGen(sinkData, Configure.INTERVAL, Configure.HI_RATIO);
        MetaData sinkMeta = sinkGen.generate();

        //System.out.println("before minerva");
        Minerva m = new Minerva(sinkMeta, userMeta, sinkData, Configure.INTERVAL, Configure.HI_RATIO);
        ArrayList<KAry> order = m.orderComp();

        int pktCnt = getRandPktCnt();

        //System.out.println("send data");
        if(pktCnt < order.size()){
            sink.appendData(new ArrayList(order.subList(0, pktCnt)));
            user.updateTrace(new ArrayList(order.subList(pktCnt, order.size())));
        }
        else{
            sink.appendData(new ArrayList(order.subList(0, order.size())));
            user.updateTrace(new ArrayList<KAry>());
        }

        return m.coverageComp(sink.getData(), Configure.INTERVAL);
    }

    public void simulate(){
        FileReader fr = null;
        BufferedReader br = null;
        String line = null;
        String [] items = null;
        UserNode user = null;
        KAry kAry = null;
        int cnt = 0;

        try{
            fr = new FileReader(this.finPath);
            br = new BufferedReader(fr);
            
            int t = -1;
            int uid = -1;
            double lon = 0;
            double lat = 0;
            while(cnt < this.simCnt && (line = br.readLine()) != null){
                //System.out.println("new round");
                cnt += 1;
                items = line.split(",");
                t = Integer.parseInt(items[0]);
                uid = Integer.parseInt(items[1]);
                lon = (Double.parseDouble(items[2]) - Configure.MIN_LON) / (Configure.MAX_LON - Configure.MIN_LON);
                lat = (Double.parseDouble(items[3]) - Configure.MIN_LAT) / (Configure.MAX_LAT - Configure.MIN_LAT);

                //System.out.println(t + ", " + uid + ", " + lon + ", " + lat);
                kAry = new KAry(this.dim);
                kAry.set(0, lat);
                kAry.set(1, lon);

                user = this.userNodes.getUser(uid); 
                if(null == user){
                    user = new UserNode(uid, this.dim);
                    this.userNodes.addUser(uid, user);
                }               
                user.addOneTrace(kAry);
                //System.out.println("before sync");                
                if(this.sink.inRange(kAry)){
                    System.out.println(t + ", " + cnt + ", " + lat + ", " + lon + ", " + syncData(user, sink));
                }

            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void main(String args[]){
        if(args.length < 2){
            System.out.println("Need 2 args: trace file location, and simulation round.");
            return;
        }
        //System.out.println(args[0] + ", " + args[1]);
        String path = args[0];
        int simCnt = Integer.parseInt(args[1]);

        Scheduler s = new Scheduler(path, simCnt);
        s.simulate();        
    }
}
