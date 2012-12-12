package secon.sim;

import secon.minerva.*;

import java.util.Hashtable;

public class UserNodes{
    private int userNum = 0;
    private Hashtable<Integer, UserNode> userNodes= null;

    public UserNodes(){
        this.userNodes = new Hashtable<Integer, UserNode>();
    }

    public void  addUser(int uid, UserNode user){
        this.userNodes.put(uid, user);    
    }

    public UserNode getUser(int uid){
        return this.userNodes.get(uid);
    }
}
