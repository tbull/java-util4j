package org.tbull.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.tbull.util.CTU.numbers1;
import static org.tbull.util.CTU.numbers2;
import static org.tbull.util.CTU.numbers3;
import static org.tbull.util.CTU.numbers4;
import static org.tbull.util.CTU.numbers5;
import static org.tbull.util.Collections.map;
import static org.tbull.util.Collections.mapLazy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;




/** Tests the {@code map} family of functions in {@link Collections}.
 *
 *
 */

public class CollectionsMapTest {


    static List<Integer> allnumbers;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        CTU.setUpBeforeClass();

        allnumbers = new ArrayList<Integer>(5 * CTU.NUMBERS_PER_LIST);
        allnumbers.addAll(numbers1);
        allnumbers.addAll(numbers2);
        allnumbers.addAll(numbers3);
        allnumbers.addAll(numbers4);
        allnumbers.addAll(numbers5);
        allnumbers = java.util.Collections.unmodifiableList(allnumbers);    // don't accidentally mess around
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        CTU.tearDownAfterClass();
    }




        static class IsPrimeWrapper {
            public Number number;
            public boolean is_prime;

            public IsPrimeWrapper(Number number, boolean is_prime) {
                this.number = number;
                this.is_prime = is_prime;
            }
        }


        static class IsPrimeWrapMapper implements Mapper<Number, IsPrimeWrapper> {
            public @Override IsPrimeWrapper map(Number element) {
                return new IsPrimeWrapper(element, CTU.isPrime(element.intValue()));
            }
        }




    List<IsPrimeWrapper> wrapped;


    @Before public void setUp() throws Exception { }
    @After public void tearDown() throws Exception { }




        void check_result() {
            assertEquals("total number of elements", allnumbers.size(), wrapped.size());

            for (int i = 0; i < allnumbers.size(); i++) {
                IsPrimeWrapper ipw;
                Integer ii;

                ii = allnumbers.get(i); ipw = wrapped.get(i);
                if (ii != ipw.number)
                    fail("map did not copy the number reference");
                if (CTU.isPrime(ii.intValue()) != ipw.is_prime)
                    fail("map has not set up the is_prime property correctly");
            }
        }




    /** Test method for {@link Collections#map(Mapper, Iterable...)}. */
    @Test @SuppressWarnings("unchecked")
    public void testMapMapperOfQsuperIQextendsOIterableOfIArray() {
        wrapped = map(new IsPrimeWrapMapper(), numbers1, numbers2, numbers3, numbers4, numbers5);
        check_result();
    }


    /** Test method for {@link Collections#map(Mapper, Iterator...)}. */
    @Test @SuppressWarnings("unchecked")
    public void testMapMapperOfQsuperIQextendsOIteratorOfIArray() {
        wrapped = map(new IsPrimeWrapMapper(), numbers1.iterator(), numbers2.iterator(),
                numbers3.iterator(), numbers4.iterator(), numbers5.iterator());
        check_result();
    }




    /** Test method for {@link Collections#map(List, Mapper, Iterable...)}. */
    @Test @SuppressWarnings("unchecked")
    public void testMapListOfQsuperOMapperOfQsuperIQextendsOIterableOfIArray() {
        wrapped = new ArrayList<IsPrimeWrapper>(allnumbers.size());
        if (wrapped != map(wrapped, new IsPrimeWrapMapper(), numbers1, numbers2, numbers3, numbers4, numbers5))
            fail("map didn't return destination list");
        check_result();
    }


    /** Test method for {@link Collections#map(List, Mapper, Iterator...)}. */
    @Test @SuppressWarnings("unchecked")
    public void testMapListOfQsuperOMapperOfQsuperIQextendsOIteratorOfIArray() {
        wrapped = new ArrayList<IsPrimeWrapper>(allnumbers.size());
        if (wrapped != map(wrapped, new IsPrimeWrapMapper(), numbers1.iterator(), numbers2.iterator(),
                numbers3.iterator(), numbers4.iterator(), numbers5.iterator()))
            fail("map didn't return destination list");
        check_result();
    }




    /** Test method for {@link Collections#mapLazy(Mapper, Iterable...)}. */
    @Test @SuppressWarnings("unchecked")
    public void testMapLazyMapperOfQsuperIQextendsOIterableOfIArray() {
        wrapped = new ArrayList<IsPrimeWrapper>(allnumbers.size());
        Iterator<IsPrimeWrapper> it;

        it = mapLazy(new IsPrimeWrapMapper(), numbers1, numbers2, numbers3, numbers4, numbers5);
        while (it.hasNext()) wrapped.add(it.next());

        check_result();
    }



    /** Test method for {@link Collections#mapLazy(Mapper, Iterable...)}
     *  using only the next() method of the iterator, not hasNext().
     */
    @Test @SuppressWarnings("unchecked")
    public void testMapLazyMapperOfQsuperIQextendsOIterableOfIArray2() {
        wrapped = new ArrayList<IsPrimeWrapper>(allnumbers.size());
        Iterator<IsPrimeWrapper> it;

        it = mapLazy(new IsPrimeWrapMapper(), numbers1, numbers2, numbers3, numbers4, numbers5);
        try {
            while (true) wrapped.add(it.next());
        } catch (NoSuchElementException e) {
            // done
        }

        check_result();
    }


    /** Test method for {@link Collections#mapLazy(Mapper, Iterator...)}. */
    @Test @SuppressWarnings("unchecked")
    public void testMapLazyMapperOfQsuperIQextendsOIteratorOfIArray() {
        wrapped = new ArrayList<IsPrimeWrapper>(allnumbers.size());
        Iterator<IsPrimeWrapper> it;

        it = mapLazy(new IsPrimeWrapMapper(), numbers1.iterator(), numbers2.iterator(),
                numbers3.iterator(), numbers4.iterator(), numbers5.iterator());
        while (it.hasNext()) wrapped.add(it.next());

        check_result();
    }

    /** Test method for {@link Collections#mapLazy(Mapper, Iterator...)}
     *  using only the next() method of the iterator, not hasNext().
     */
    @Test @SuppressWarnings("unchecked")
    public void testMapLazyMapperOfQsuperIQextendsOIteratorOfIArray2() {
        wrapped = new ArrayList<IsPrimeWrapper>(allnumbers.size());
        Iterator<IsPrimeWrapper> it;

        it = mapLazy(new IsPrimeWrapMapper(), numbers1.iterator(), numbers2.iterator(),
                numbers3.iterator(), numbers4.iterator(), numbers5.iterator());
        try {
            while (true) wrapped.add(it.next());
        } catch (NoSuchElementException e) {
            // done
        }

        check_result();
    }
}
