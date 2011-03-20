/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.developers.blog.osgi.webservice.rest.test;

/**
 *
 * @author rafsob
 */
public class Permutation {

    static char[] a;
    static int p;

    static void perm(int n) {
        char hilf;

        if (n == 1) {
            p = p + 1;
        } else {
            for (int k = n; k >= 1; k = k - 1) {
                hilf = a[k];
                a[k] = a[n];
                a[n] = hilf;

                perm(n - 1);

                hilf = a[k];
                a[k] = a[n];
                a[n] = hilf;
            }
        }
    }

} 

