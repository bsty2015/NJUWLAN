package com.jjlink.jieyun.njuwlan.activity;

import android.app.Activity;
import android.content.Intent;

import com.jjlink.jieyun.R;

/**
 * Created by zlu on 15-6-9.
 */
public class JieyunActivity extends Activity {
    /**
     * activity动画切换效果
     * @param clas
     */
    public void openActivity(Class<?> clas){
        Intent intent=new Intent(this,clas);
        startActivity(intent);
        this.overridePendingTransition(R.anim.enteralpha,R.anim.exitalpha);
    }
}
