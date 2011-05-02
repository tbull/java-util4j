package org.tbull.util;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.tbull.util.CTU.NUMBERS_PER_LIST;
import static org.tbull.util.CTU.nonprime_grepper;
import static org.tbull.util.CTU.prime_grepper;

import java.util.Iterator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tbull.util.CTU.NoiseIterator;




/** Tests the example greppers beneath {@link Grepper}.
 *
 *
 */

public class GrepperTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        CTU.setUpBeforeClass();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        CTU.tearDownAfterClass();
    }




        /** A Grepper that greps Integers that have a certain bit set. */
        static class BitGrepper implements Grepper<Integer> {
            final int mask;

            public BitGrepper(int bitpos) {
                this.mask = 1 << bitpos;
            }

            public @Override boolean grep(Integer element) {
                return (element.intValue() & mask) != 0;
            }
        }




    @Before public void setUp() throws Exception { }
    @After public void tearDown() throws Exception { }




    /** Test method for {@link Grepper.NULLGrepper}. */
    @Test
    public void testNULLGrepper() {
        Grepper<Integer> null_grepper = new Grepper.NULLGrepper<Integer>();
        Iterator<Integer> it = new NoiseIterator(NUMBERS_PER_LIST);

        while (it.hasNext())
            assertTrue("NULLGrepper greps nothing", null_grepper.grep(it.next()) == false);
    }




    /** Test method for {@link Grepper.ONEGrepper}. */
    @Test
    public void testONEGrepper() {
        Grepper<Integer> one_grepper = new Grepper.ONEGrepper<Integer>();
        Iterator<Integer> it = new NoiseIterator(NUMBERS_PER_LIST);

        while (it.hasNext())
            assertTrue("ONEGrepper greps everything", one_grepper.grep(it.next()) == true);
    }




    /** Test method for {@link Grepper.InverseGrepper}. */
    @Test
    public void testInverseGrepper() {
        Grepper<Number> inv_grepper;

        inv_grepper = new Grepper.InverseGrepper<Number>(prime_grepper);
        for (Iterator<Integer> it = new CTU.NoiseIterator(NUMBERS_PER_LIST); it.hasNext(); ) {
            Integer element = it.next();
            assertTrue("inv(prime) == nonprime", inv_grepper.grep(element) == nonprime_grepper.grep(element));
        }

        inv_grepper = new Grepper.InverseGrepper<Number>(nonprime_grepper);
        for (Iterator<Integer> it = new NoiseIterator(NUMBERS_PER_LIST); it.hasNext(); ) {
            Integer element = it.next();
            assertTrue("inv(nonprime) == prime", inv_grepper.grep(element) == prime_grepper.grep(element));
        }
    }




    /** Test method for {@link Grepper.ORGrepper}. */
    @Test
    public void testORGrepper() {
        int mask = 0;
        Grepper.ORGrepper<Integer> or_grepper = new Grepper.ORGrepper<Integer>();
        or_grepper.add(new BitGrepper(3)); mask |= 1 << 3;          // when crafting the bitmask, remember that
        or_grepper.add(new BitGrepper(8)); mask |= 1 << 8;          // the noise generator returns only numbers
        or_grepper.add(new BitGrepper(17)); mask |= 1 << 17;        // up to MAX_RANDOM

        for (Iterator<Integer> it = new NoiseIterator(NUMBERS_PER_LIST); it.hasNext(); ) {
            Integer element = it.next();
            assertTrue("ORed greps == ORed mask", or_grepper.grep(element) == ((element.intValue() & mask) != 0));
        }
    }




    /** Test method for {@link Grepper.ANDGrepper}. */
    @Test
    public void testANDGrepper() {
        int mask = 0;
        Grepper.ANDGrepper<Integer> and_grepper = new Grepper.ANDGrepper<Integer>();
        and_grepper.add(new BitGrepper(3)); mask |= 1 << 3;         // when crafting the bitmask, remember that
        and_grepper.add(new BitGrepper(8)); mask |= 1 << 8;         // the noise generator returns only numbers
        and_grepper.add(new BitGrepper(17)); mask |= 1 << 17;       // up to MAX_RANDOM

        for (Iterator<Integer> it = new NoiseIterator(10*NUMBERS_PER_LIST); it.hasNext(); ) {
            Integer element = it.next();
            assertTrue("ANDed greps == ANDed mask", and_grepper.grep(element) == ((element.intValue() & mask) == mask));
        }
    }




    /** Test method for {@link Grepper.RegexGrepper}. */
    @Test // @SuppressWarnings("unchecked")
    public void testRegexGrepper() {


        fail("not yet implemented");
    }

}
