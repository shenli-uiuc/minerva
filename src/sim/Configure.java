package secon.sim;

public class Configure{
    //near beijing, 1 degree difference in latitude results in roughly 110000 meters geographics distace
    //1 degree difference in longitude results in roughly 90000 meters geographic distance
    public static final double LAT_SCALE = 110000; // to meter 
    public static final double LON_SCALE = 90000; // to meter
    public static final int DIM = 2;
    public static final int SINK_NUM = 2;
    public static final double SINK_LOC [][] = {{39.9, 116.4}, {40.08, 116.58}};
    public static final double SINK_RANGE = 100; //in meter
    //public static final int MIN_PKT = 20; //minimum possible number of data points sended during one transmission
    //public static final int MAX_PKT = 40;
    public static final double INTERVAL = 0.005;
    public static final double HI_RATIO = 0.1;
    public static final double MIN_LAT = 39.5;
    public static final double MAX_LAT = 40.5;
    public static final double MIN_LON = 116;
    public static final double MAX_LON = 117;
}
