package com.jjlink.jieyun.njuwlan.util;

/**
 * value的长度
 * Created by zlu on 15-3-10.
 */
public class LPosition {
    private int _vL;
    private int _position;

    public LPosition(int _vL, int _position) {
        this._vL = _vL;
        this._position = _position;
    }

    public int get_vL() {
        return _vL;
    }

    public void set_vL(int _vL) {
        this._vL = _vL;
    }

    public int get_position() {
        return _position;
    }

    public void set_position(int _position) {
        this._position = _position;
    }
}
