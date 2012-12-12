package secon.sim;

import secon.minerva.*;

import java.util.ArrayList;

public class UserNode{
    private int uid = -1;
    private int dim = -1;
    private ArrayList<KAry> trace = null;

    public UserNode(int uid, int dim){
        this.trace = new ArrayList<KAry>();
        this.uid = uid;
        this.dim = dim;
    }

    public void addOneTrace(KAry kAry){
        this.trace.add(kAry);
    }

    public void updateTrace(ArrayList<KAry> trace){
        this.trace = trace;
    }

    public ArrayList<KAry> getTrace(){
        return this.trace;
    }
}
