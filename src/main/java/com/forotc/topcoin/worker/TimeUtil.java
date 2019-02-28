package com.forotc.topcoin.worker;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TimeUtil {
    public static void printElapsedTime(String where, long elapsedTime) {

        long millis = TimeUnit.NANOSECONDS.toMillis(elapsedTime);
        long micros = TimeUnit.MICROSECONDS.toMicros(elapsedTime);
        log.debug("Time-elapsed-@{} :{} millis, {} micros.", where, millis, micros);
    }
}