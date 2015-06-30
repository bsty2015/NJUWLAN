package com.jjlink.jieyun.njuwlan.util;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zlu on 15-4-21.
 */
public class GestureListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
    //上下滑动的最短距离
    private int distance = 200;
    //上下滑动的最快速度
    private int velocity = 200;

    private GestureDetector gestureDetector;

    public GestureListener(Context context) {
        super();
        gestureDetector = new GestureDetector(context, this);
    }

    public boolean up() {
        return false;
    }

    public boolean left(){
        return false;
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        System.out.println("y:"+(e1.getY() - e2.getY())+"\nvelocityY:"+velocityY);
        if (e1.getY() - e2.getY() > distance && Math.abs(velocityY) > velocity) {
            up();
        }
        if(e1.getX()-e2.getX()>distance && Math.abs(velocityX)>velocity){
            left();
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return false;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public GestureDetector getGestureDetector() {
        return gestureDetector;
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }
}
