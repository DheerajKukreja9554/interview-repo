package com.codesbydk.interviews.leetcode;

public class LeetcodeStringQuestions {

    public boolean isInterleave(String s1, String s2, String s3) {
        if (s1.length() + s2.length() != s3.length())
            return false;
        int fLeft = 0;//, fRight = 0;
        int sLeft = 0;//, sRight = 0;
        int it = 0;
        while (it < s3.length()) {
                        System.out.println("fleft: %s; sleft: %s; it: %s".formatted(fLeft,sLeft,it));

            if (fLeft < s1.length() && s1.charAt(fLeft) == s3.charAt(it)) {
                while (fLeft < s1.length() && s1.charAt(fLeft) == s3.charAt(it)) {
                    fLeft++;
                    it++;
                }
            } else if (sLeft < s2.length() && s2.charAt(sLeft) == s3.charAt(it)) {
                while (sLeft < s2.length() && s2.charAt(sLeft) == s3.charAt(it)) {
                    sLeft++;
                    it++;
                }
            } else
                return false;
            // System.out.println("fleft: %s; sleft: %s; it: %s".formatted(fLeft,sLeft,it));
        }
        return true;
    }

    public static void main(String[] args) {
        LeetcodeStringQuestions sq = new LeetcodeStringQuestions();
        // System.out.println(sq.isInterleave("aabcc", "dbbca", "aadbbcbcac"));
        // System.out.println(sq.isInterleave("", "", ""));
        // System.out.println(sq.isInterleave("aabcc", "dbbca", "aadbbbaccc"));
        // System.out.println(sq.isInterleave("a", "b", "a"));
        System.out.println(sq.isInterleave("aa", "ab", "aaba"));
    }
}
