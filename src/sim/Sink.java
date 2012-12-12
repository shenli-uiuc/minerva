package secon.sim;

import secon.minerva.*;

import java.util.ArrayList;

public class Sink{

    private int sinkNum = 0;
    private double [][] locs = null;
    private double range = -1;
    private ArrayList<KAry> data = null;

    public Sink(double [][] locs, double range, int sinkNum){
        this.sinkNum = sinkNum;
        this.locs = locs;

        for(int i = 0 ; i < sinkNum; ++i){
            this.locs[i][0] = (this.locs[i][0] - Configure.MIN_LAT) / (Configure.MAX_LAT - Configure.MIN_LAT);
            this.locs[i][1] = (this.locs[i][1] - Configure.MIN_LON) / (Configure.MAX_LON - Configure.MIN_LON);
        }

        this.range = range;
        this.data = new ArrayList<KAry>();
    }

    public boolean inRange(KAry kAry){
        double lat = kAry.get(0);
        double lon = kAry.get(1);
        double latDiff = 0;
        double lonDiff = 0;
        double dist = 0;

        for(int i = 0 ; i < this.sinkNum; ++i){
            //System.out.println(lat + ", " + lon + ", " + this.locs[i][0] + ", " + this.locs[i][1]);
            latDiff = (lat - this.locs[i][0]) * (Configure.MAX_LAT - Configure.MIN_LAT) * Configure.LAT_SCALE;
            lonDiff = (lon - this.locs[i][1]) * (Configure.MAX_LON - Configure.MIN_LON) * Configure.LON_SCALE;
            dist = Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
            if(dist < this.range)
                return true;
        }

        return false;

    }

    public ArrayList<KAry> getData(){
        return (ArrayList<KAry>)this.data.clone();
    }

    public void appendData(ArrayList<KAry> newData){
        this.data.addAll(newData);
    }

}
