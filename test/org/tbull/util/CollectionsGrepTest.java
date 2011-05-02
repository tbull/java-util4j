package org.tbull.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.tbull.util.CTU.NUMBERS_PER_LIST;
import static org.tbull.util.CTU.isPrime;
import static org.tbull.util.CTU.nonprime_grepper;
import static org.tbull.util.CTU.numbers1;
import static org.tbull.util.CTU.numbers2;
import static org.tbull.util.CTU.numbers3;
import static org.tbull.util.CTU.numbers4;
import static org.tbull.util.CTU.numbers5;
import static org.tbull.util.CTU.prime_grepper;
import static org.tbull.util.Collections.grep;
import static org.tbull.util.Collections.grepCount;
import static org.tbull.util.Collections.grepLazy;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;




/** Tests the {@code grep} family of functions in {@link Collections}.
 *
 *
 */

public class CollectionsGrepTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        CTU.setUpBeforeClass();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        CTU.tearDownAfterClass();
    }




    List<Integer> primes, nonprimes;
    List<Integer> empty = null;


    @Before public void setUp() throws Exception { }
    @After public void tearDown() throws Exception { }




        void check_result() {
            // the sum of the element counts must equal the sum of the original lists' sizes
            assertEquals("total number of elements", 5*NUMBERS_PER_LIST, primes.size() + nonprimes.size());

            // the contents of primes and nonprimes lists must be completely distinct
            // this can be easily checked by testing primality for each number again

            for (Integer n: primes)
                if (!isPrime(n.intValue()))
                    fail("member of primes is not prime");

            for (Integer n: nonprimes)
                if (isPrime(n.intValue()))
                    fail("member of nonprimes is prime");

            if (empty != null)
                assertEquals("empty list size", 0, empty.size());
        }




    /** Test method for {@link Collections#grep(Grepper, Iterable...)}. */
// TODO: the warnings should go away with Java 7; remove all @SuppressWarnings("unchecked") then
    @Test @SuppressWarnings("unchecked")
    public void testGrepGrepperOfQsuperEIterableOfEArray() {
        primes = grep(prime_grepper, numbers1, numbers2, numbers3, numbers4, numbers5);
        nonprimes = grep(nonprime_grepper, numbers1, numbers2, numbers3, numbers4, numbers5);
        empty = grep(nonprime_grepper,
                grep(prime_grepper, numbers1, numbers2, numbers3, numbers4, numbers5));
        check_result();
    }


    /** Test method for {@link Collections#grep(Grepper, Iterator...)}. */
    @Test @SuppressWarnings("unchecked")
    public void testGrepGrepperOfQsuperEIteratorOfEArray() {
        primes = grep(prime_grepper, numbers1.iterator(), numbers2.iterator(),
                numbers3.iterator(), numbers4.iterator(), numbers5.iterator());
        nonprimes = grep(nonprime_grepper, numbers1.iterator(), numbers2.iterator(),
                numbers3.iterator(), numbers4.iterator(), numbers5.iterator());
        empty = grep(nonprime_grepper,
                grep(prime_grepper, numbers1.iterator(), numbers2.iterator(), numbers3.iterator(),
                        numbers4.iterator(), numbers5.iterator()));
        check_result();
    }




    /** Test method for {@link Collections#grep(List, Grepper, Iterable...)}. */
    @Test @SuppressWarnings("unchecked")
    public void testGrepListOfQsuperEGrepperOfQsuperEIterableOfEArray() {
        primes = new LinkedList<Integer>();
        nonprimes = new LinkedList<Integer>();
        if (primes != grep(primes, prime_grepper, numbers1, numbers2, numbers3, numbers4, numbers5))
            fail("grep didn't return destination list");
        if (nonprimes != grep(nonprimes, nonprime_grepper, numbers1, numbers2, numbers3, numbers4, numbers5))
            fail("grep didn't return destination list");
        check_result();
    }


    /** Test method for {@link Collections#grep(List, Grepper, Iterator...)}. */
    @Test @SuppressWarnings("unchecked")
    public void testGrepListOfQsuperEGrepperOfQsuperEIteratorOfEArray() {
        primes = new LinkedList<Integer>();
        nonprimes = new LinkedList<Integer>();
        if (primes != grep(primes, prime_grepper, numbers1.iterator(), numbers2.iterator(),
                numbers3.iterator(), numbers4.iterator(), numbers5.iterator()))
            fail("grep didn't return destination list");
        if (nonprimes != grep(nonprimes, nonprime_grepper, numbers1.iterator(), numbers2.iterator(),
                numbers3.iterator(), numbers4.iterator(), numbers5.iterator()))
            fail("grep didn't return destination list");
        check_result();
    }




    /** Test method for {@link Collections#grepLazy(Grepper, Iterable...)}. */
    @Test @SuppressWarnings("unchecked")
    public void testGrepLazyGrepperOfQsuperEIterableOfEArray() {
        primes = new LinkedList<Integer>();
        nonprimes = new LinkedList<Integer>();
        empty = new LinkedList<Integer>();
        Iterator<Integer> it;

        it = grepLazy(prime_grepper, numbers1, numbers2, numbers3, numbers4, numbers5);
        while (it.hasNext()) primes.add(it.next());

        it = grepLazy(nonprime_grepper, numbers1, numbers2, numbers3, numbers4, numbers5);
        while (it.hasNext()) nonprimes.add(it.next());

        it = grepLazy(prime_grepper,
                (Iterator) grepLazy(nonprime_grepper, numbers1, numbers2, numbers3, numbers4, numbers5));
        while (it.hasNext()) empty.add(it.next());

        check_result();
    }


    /** Test method for {@link Collections#grepLazy(Grepper, Iterable...)}
     *  using only the next() method of the iterator, not hasNext().
     */
    @Test @SuppressWarnings("unchecked")
    public void testGrepLazyGrepperOfQsuperEIterableOfEArray2() {
        primes = new LinkedList<Integer>();
        nonprimes = new LinkedList<Integer>();
        empty = new LinkedList<Integer>();
        IterableIterator<Integer> it;


        it = grepLazy(prime_grepper, numbers1, numbers2, numbers3, numbers4, numbers5);
        try {
            while (true) primes.add(it.next());
        } catch (NoSuchElementException e) { /* done */ }

        it = grepLazy(nonprime_grepper, numbers1, numbers2, numbers3, numbers4, numbers5);
        try {
            while (true) nonprimes.add(it.next());
        } catch (NoSuchElementException e) { /* done */ }

        it = grepLazy(prime_grepper,
                (Iterator) grepLazy(nonprime_grepper, numbers1, numbers2, numbers3, numbers4, numbers5));
        try {
            while (true) empty.add(it.next());
        } catch (NoSuchElementException e) { /* done */ }

        check_result();
    }




    /** Test method for {@link Collections#grepLazy(Grepper, Iterator...)}. */
    @Test @SuppressWarnings("unchecked")
    public void testGrepLazyGrepperOfQsuperEIteratorOfEArray() {
        primes = new LinkedList<Integer>();
        nonprimes = new LinkedList<Integer>();
        empty = new LinkedList<Integer>();
        Iterator<Integer> it;

        it = grepLazy(prime_grepper, numbers1.iterator(), numbers2.iterator(),
                numbers3.iterator(), numbers4.iterator(), numbers5.iterator());
        while (it.hasNext()) primes.add(it.next());

        it = grepLazy(nonprime_grepper, numbers1.iterator(), numbers2.iterator(),
                numbers3.iterator(), numbers4.iterator(), numbers5.iterator());
        while (it.hasNext()) nonprimes.add(it.next());

        it = grepLazy(prime_grepper,
                grepLazy(nonprime_grepper, numbers1.iterator(), numbers2.iterator(),
                        numbers3.iterator(), numbers4.iterator(), numbers5.iterator()).iterator());
        while (it.hasNext()) empty.add(it.next());

        check_result();
    }


    /** Test method for {@link Collections#grepLazy(Grepper, Iterator...)}
     *  using only the next() method of the iterator, not hasNext().
     */
    @Test @SuppressWarnings("unchecked")
    public void testGrepLazyGrepperOfQsuperEIteratorOfEArray2() {
        primes = new LinkedList<Integer>();
        nonprimes = new LinkedList<Integer>();
        empty = new LinkedList<Integer>();
        IterableIterator<Integer> it;


        it = grepLazy(prime_grepper, numbers1.iterator(), numbers2.iterator(),
                numbers3.iterator(), numbers4.iterator(), numbers5.iterator());
        try {
            while (true) primes.add(it.next());
        } catch (NoSuchElementException e) { /* done */ }

        it = grepLazy(nonprime_grepper, numbers1.iterator(), numbers2.iterator(),
                numbers3.iterator(), numbers4.iterator(), numbers5.iterator());
        try {
            while (true) nonprimes.add(it.next());
        } catch (NoSuchElementException e) { /* done */ }

        it = grepLazy(prime_grepper,
                grepLazy(nonprime_grepper, numbers1.iterator(), numbers2.iterator(),
                        numbers3.iterator(), numbers4.iterator(), numbers5.iterator()).iterator());
        try {
            while (true) empty.add(it.next());
        } catch (NoSuchElementException e) { /* done */ }

        check_result();
    }



// TODO: test lazy iterator's remove()



    /** Test method for {@link Collections#grepCount(Grepper, Iterable...)}. */
    @Test @SuppressWarnings("unchecked")
    public void testGrepCountGrepperOfQsuperEIterableOfEArray() {
        int p, np;
        p = grepCount(prime_grepper, numbers1, numbers2, numbers3, numbers4, numbers5);
        np = grepCount(nonprime_grepper, numbers1, numbers2, numbers3, numbers4, numbers5);

        assertEquals("total grep count", 5*NUMBERS_PER_LIST, p + np);
    }




    /** Test method for {@link Collections#grepCount(Grepper, Iterator...)}. */
    @Test @SuppressWarnings("unchecked")
    public void testGrepCountGrepperOfQsuperEIteratorOfEArray() {
        int p, np;
        p = grepCount(prime_grepper, numbers1.iterator(), numbers2.iterator(),
                numbers3.iterator(), numbers4.iterator(), numbers5.iterator());
        np = grepCount(nonprime_grepper, numbers1.iterator(), numbers2.iterator(),
                numbers3.iterator(), numbers4.iterator(), numbers5.iterator());

        assertEquals("total grep count", 5*NUMBERS_PER_LIST, p + np);
    }
}
