package secon.sim;

import secon.minerva.*;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

public class Scheduler{
    
    private UserNodes userNodes = null;
    private Sink sink = null;
    private String algo = null;

    //the file is currently under "/home/shenli/Dropbox/SECON2013/data/coor.txt"
    private String finPath = null;
    private int simCnt = 0;
    private int dim = 0;

    private Random rand = null;

    public Scheduler(String finPath, int simCnt, String algo){
        this.sink = new Sink(Configure.SINK_LOC, Configure.SINK_RANGE, Configure.SINK_NUM);        

        this.userNodes = new UserNodes();
        this.finPath = finPath;
        this.simCnt = simCnt;
        this.dim = Configure.DIM;
        this.rand = new Random();
        this.algo = algo;
    }

    private int getRandPktCnt(){
        return rand.nextInt(Configure.MAX_PKT - Configure.MIN_PKT + 1) + Configure.MIN_PKT;
    }


    private void syncData(UserNode user, Sink sink){
        if(this.algo.equals("minerva")){
            doMinerva(user, sink);
        }
        else if(this.algo.equals("fifo")){
            doFIFO(user, sink);
        }
        else if(this.algo.equals("random")){
            doRandom(user, sink);
        }
        else if(this.algo.equals("distance")){
            doDistance(user, sink);
        }
        else{
            System.out.println("Unrecognized algorithm name");
            assert(false);
        }
        
    }

    private void doDistance(UserNode user, Sink sink){
        ArrayList<KAry> userData = user.getTrace();
        ArrayList<KAry> sinkData = sink.getData();

        ArrayList<KAry> order = Distance.orderComp(sinkData, userData);
        int pktCnt = getRandPktCnt();

        if(pktCnt < order.size()){
            sink.appendData(new ArrayList(order.subList(0, pktCnt)));
            user.updateTrace(new ArrayList(order.subList(pktCnt, order.size())));
        }
        else{
            sink.appendData(order);
            user.updateTrace(new ArrayList<KAry>());
        }
         
    }

    private void doRandom(UserNode user, Sink sink){
        ArrayList<KAry> userData = user.getTrace();

        Collections.shuffle(userData, new Random(System.nanoTime()));
        int pktCnt = getRandPktCnt();
        if(pktCnt < userData.size()){
            sink.appendData(new ArrayList(userData.subList(0, pktCnt)));
            user.updateTrace(new ArrayList(userData.subList(pktCnt, userData.size())));
        }
        else{
            sink.appendData(userData);
            user.updateTrace(new ArrayList());
        }
    }

    private void doFIFO(UserNode user, Sink sink){
        ArrayList<KAry> userData = user.getTrace();
        int pktCnt = getRandPktCnt();
        if(pktCnt < userData.size()){
            sink.appendData(new ArrayList(userData.subList(0, pktCnt)));
            user.updateTrace(new ArrayList(userData.subList(pktCnt, userData.size())));
        }
        else{
            sink.appendData(userData);
            user.updateTrace(new ArrayList());
        }
    }

    private double calcScore(UserNode user, Sink sink){
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
        return m.coverageComp(sink.getData(), Configure.INTERVAL);
    }

    private void doMinerva(UserNode user, Sink sink){
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
            sink.appendData(order);
            user.updateTrace(new ArrayList<KAry>());
        }

        //return m;
        //return m.coverageComp(sink.getData(), Configure.INTERVAL);
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
            int m = 0; //calculate the score every 10 minutes
            double TEN_M = 600; //in s 
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
                    syncData(user, sink);
                    if(t > TEN_M * (m + 1)){
                        m = (int)(Math.floor(t / TEN_M ));
                        System.out.println(t + ", " + cnt + ", " + lat + ", " + lon + ", " + calcScore(user, sink));                
                    }
                    //System.out.println(t + ", " + cnt + ", " + lat + ", " + lon + ", " + syncData(user, sink));
                }

            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void main(String args[]){
        if(args.length < 3){
            System.out.println("Need 3 args: trace file location, simulation round, algorithm name (minerva/fifo/random/distance).");
            return;
        }
        //System.out.println(args[0] + ", " + args[1]);
        String path = args[0];
        int simCnt = Integer.parseInt(args[1]);
        String algo = args[2];

        Scheduler s = new Scheduler(path, simCnt, algo);
        s.simulate();        
    }
}
