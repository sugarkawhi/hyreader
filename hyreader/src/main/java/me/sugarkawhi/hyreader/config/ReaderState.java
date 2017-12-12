package me.sugarkawhi.hyreader.config;

/**
 * 阅读器滑动还是静止状态
 * <p>
 * 1.静止状态下只需要画当前页
 * 2.滑动状态下需要画两页
 *
 * @author zhzy
 * @date 2017/12/12
 */

public interface ReaderState {
    //滑动
    int SCROLL = 1;
    //静止
    int STATIC = 2;
}
