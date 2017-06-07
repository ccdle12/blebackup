package com.ccdle.christophercoverdale.onemillionsteps;

/**
 * Created by USER on 5/8/2017.
 */

public interface SingletonObserver {
    void notifyObservable();

    interface singletonObservable {
        void update();
    }
}
