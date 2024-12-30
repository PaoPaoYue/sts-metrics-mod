package com.github.paopaoyue.metrics_local.utility;

public class Inject {

    public static int[] insertAfter(int[] lineNum, int num) {
        for (int i = 0; i < lineNum.length; ++i) {
            lineNum[i] += num;
        }

        return lineNum;
    }

    public static int[] insertBefore(int[] lineNum, int num) {
        for (int i = 0; i < lineNum.length; ++i) {
            lineNum[i] -= num;
        }

        return lineNum;
    }
}
