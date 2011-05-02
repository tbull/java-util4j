package org.tbull.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;




/** CTU = CollectionsTestUtil.
 *
 *  Test input material commonly used by the Collections related tests.
 *  No, Jack Bauer is not here, he left earlier today.
 */

class CTU {

    static final int NUMBERS_PER_LIST = 0x10000;    // 64K
    static final int MAX_RANDOM = 0x80000;          // 512K


    static Grepper<Number> prime_grepper;
    static Grepper<Number> nonprime_grepper;
    static Random rnd;
    static List<Integer> numbers1, numbers2, numbers3, numbers4, numbers5;


    /* taken from http://stackoverflow.com/questions/2385909/most-elegant-way-to-write-isprime-in-java */
    static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i <= Math.sqrt(n) + 1; i = i + 2) {
            if (n % i == 0) return false;
        }
        return true;
    }


    static class PrimeGrepper implements Grepper<Number> {
        public @Override boolean grep(Number i) {
            return isPrime(i.intValue());
        }
    }

    static class NonPrimeGrepper implements Grepper<Number> {
        public @Override boolean grep(Number i) {
            return !isPrime(i.intValue());
        }
    }


    static class NoiseIterator implements Iterator<Integer> {
        final int size;
        int count;

        public NoiseIterator(int size) { this.count = this.size = size; }

        public @Override boolean hasNext() { return count > 0; }

        public @Override Integer next() {
            if (count-- > 0) return Integer.valueOf(rnd.nextInt(MAX_RANDOM));
            throw new NoSuchElementException();
        }

        public @Override void remove() { throw new UnsupportedOperationException(); }
    }




    static List<Integer> make_list() {
        List<Integer> numbers = new ArrayList<Integer>(NUMBERS_PER_LIST);
        for (int i = 0; i < NUMBERS_PER_LIST; i++)
            numbers.add(Integer.valueOf(rnd.nextInt(MAX_RANDOM)));
        return java.util.Collections.unmodifiableList(numbers); // make sure we don't accidentally mess around
    }


    static void setUpBeforeClass() throws Exception {
        prime_grepper = new PrimeGrepper();
        nonprime_grepper = new NonPrimeGrepper();

        rnd = new Random();
        numbers1 = make_list();
        numbers2 = make_list();
        numbers3 = make_list();
        numbers4 = make_list();
        numbers5 = make_list();
    }

    static void tearDownAfterClass() throws Exception { }

}
