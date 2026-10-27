package org.firstinspires.ftc.teamcode.autonomous.StateMachine;

import com.pedropathing.follower.Follower;

import java.util.ArrayList;

class StateMachine {
    Follower follower;
    int orderIndex;
    ArrayList<MyState> order;
    public StateMachine(ArrayList<MyState> _order) {
        order = _order;
        cancel();
    }
    public StateMachine(ArrayList<MyState> _order, Follower _follower) {
        order = _order;
        follower = _follower;
        cancel();
    }
    public MyState getState() {
        if(isInBounds(orderIndex)){
            return order.get(orderIndex);
        }
        return null;
    }
    public void cancel(){
        orderIndex = -1;
    }
    public void start(){
        orderIndex = 0;
        order.get(orderIndex).enter();
    }
    public boolean isInBounds(int num){
        return num>-1 && num<order.size();
    }
    public void update() {
        if(orderIndex < 0){
            return;
        }
        MyState nextState = null;
        if(order.get(orderIndex).isEnded()){
            order.get(orderIndex).leaving();
            if(isInBounds(orderIndex+1)){
                orderIndex++;
                nextState = order.get(orderIndex);
            }else{ // This is so that if not entering END state; state will be null
                orderIndex = -1;
            }
        }
        if (nextState != null) {
            order.get(orderIndex).enter();
        }
        if(isInBounds(orderIndex)){
            order.get(orderIndex).update();
        }

    }
}
