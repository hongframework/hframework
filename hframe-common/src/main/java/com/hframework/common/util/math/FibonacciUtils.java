package com.hframework.common.util.math;

/**
 * Created by zhangquanhong on 2016/12/15.
 */
public class FibonacciUtils {

    public static boolean isMatch(Long number){
        if(number <= 0 ) return false;

        long prevNumber =0, curNumber = 1;
        while(prevNumber + curNumber < number) {
            long tmpNumber = prevNumber + curNumber;
            prevNumber = curNumber;
            curNumber = tmpNumber;
        };
        return prevNumber + curNumber == number ? true : false;
    }

    public static void main(String[] args) {
        System.out.println(isMatch(5L));
    }
}
