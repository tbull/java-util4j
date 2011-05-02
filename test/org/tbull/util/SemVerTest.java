/**
 *
 */
package org.tbull.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 *
 */
public class SemVerTest {

        /** Maps a parsable version string to its several parts. */
        static class VInfo {
            String string;
            int major;
            int minor;
            int patch;
            String special;

            public VInfo(String string, int major, int minor, int patch, String special) {
                this.string = string;
                this.major = major;
                this.minor = minor;
                this.patch = patch;
                this.special = special;
            }
        }


        static VInfo[] version_strings = {
            new VInfo("1.4.2", 1, 4, 2, null),
            new VInfo("1.4", 1, 4, 0, null),
            new VInfo("1", 1, 0, 0, null),
            new VInfo("1.4.2foo23", 1, 4, 2, "foo23"),
            new VInfo("1.4foo23", 1, 4, 0, "foo23"),
            new VInfo("1foo23", 1, 0, 0, "foo23")
        };

        static VInfo[] version_vstrings = {
            new VInfo("v1.4.2", 1, 4, 2, null),
            new VInfo("v1.4", 1, 4, 0, null),
            new VInfo("v1", 1, 0, 0, null),
            new VInfo("v1.4.2foo23", 1, 4, 2, "foo23"),
            new VInfo("v1.4foo23", 1, 4, 0, "foo23"),
            new VInfo("v1foo23", 1, 0, 0, "foo23")
        };

        static String[] non_version_strings = {
            "",
            "1.",
            "1.4.",
            "1.4.2.",
            "foo",
            "1.4.foo",
            "1.4foo_23",
            "1.3.a",
            "1.a.3",
            "a.1.3",
            "-1.4.2",
            "1.-4.2",
            "1.4.-2",
            "1.4.2-foo",
            "1.4.2_foo"
        };





    @BeforeClass public static void setUpBeforeClass() throws Exception {    }
    @AfterClass public static void tearDownAfterClass() throws Exception {    }


    @Before public void setUp() throws Exception { }
    @After public void tearDown() throws Exception { }


        /** Checks if the contents of the SemVer object match the supplied arguments. */
        static boolean vequals(SemVer v, int major, int minor, int patch, String special) {
            return v.major == major && v.minor == minor && v.patch == patch &&
                (v.special == null ? special == null : v.special.equals(special));
        }






    /** Test method for {@link org.tbull.util.SemVer#hashCode()}. */
    @Test
    public void testHashCode() {
        fail("Not yet implemented");
    }




    /** Test method for {@link org.tbull.util.SemVer#SemVer(int, int, int)}. */
    @Test
    public void testSemVerIntIntInt() {
        assertTrue("vequals", vequals(new SemVer(1, 4, 2), 1, 4, 2, null));
        assertTrue("vequals", vequals(new SemVer(1, 4, 0), 1, 4, 0, null));
        assertTrue("vequals", vequals(new SemVer(1, 0, 0), 1, 0, 0, null));
    }




    /** Test method for {@link org.tbull.util.SemVer#SemVer(int, int, int, java.lang.String)}. */
    @Test
    public void testSemVerIntIntIntString() {
        assertTrue("vequals", vequals(new SemVer(1, 4, 2, null), 1, 4, 2, null));
        assertTrue("vequals", vequals(new SemVer(1, 4, 0, null), 1, 4, 0, null));
        assertTrue("vequals", vequals(new SemVer(1, 0, 0, null), 1, 0, 0, null));
        // TODO: treatment of an empty special string has yet to be defined
//        assertTrue("vequals", vequals(new SemVer(1, 4, 2, ""), 1, 4, 2, null));
//        assertTrue("vequals", vequals(new SemVer(1, 4, 0, ""), 1, 4, 0, null));
//        assertTrue("vequals", vequals(new SemVer(1, 0, 0, ""), 1, 0, 0, null));
        assertTrue("vequals", vequals(new SemVer(1, 4, 2, "draft"), 1, 4, 2, "draft"));
        assertTrue("vequals", vequals(new SemVer(1, 4, 0, "draft"), 1, 4, 0, "draft"));
        assertTrue("vequals", vequals(new SemVer(1, 0, 0, "draft"), 1, 0, 0, "draft"));
    }




    /**
     * Test method for {@link org.tbull.util.SemVer#compareTo(org.tbull.util.SemVer)}.
     */
    @Test
    public void testCompareTo() {
        fail("Not yet implemented");
    }




    /**
     * Test method for {@link org.tbull.util.SemVer#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsObject() {
        fail("Not yet implemented");
    }




    /**
     * Test method for {@link org.tbull.util.SemVer#compatibleForUse(org.tbull.util.SemVer)}.
     */
    @Test
    public void testCompatibleForUse() {
        fail("Not yet implemented");
    }




    /**
     * Test method for {@link org.tbull.util.SemVer#isStable()}.
     */
    @Test
    public void testIsStable() {
        fail("Not yet implemented");
    }




    /**
     * Test method for {@link org.tbull.util.SemVer#nextMajor()}.
     */
    @Test
    public void testNextMajor() {
        fail("Not yet implemented");
    }




    /**
     * Test method for {@link org.tbull.util.SemVer#nextMinor()}.
     */
    @Test
    public void testNextMinor() {
        fail("Not yet implemented");
    }




    /**
     * Test method for {@link org.tbull.util.SemVer#nextPatch()}.
     */
    @Test
    public void testNextPatch() {
        fail("Not yet implemented");
    }




        boolean parse_throws(String vstring) {
            try {
                SemVer.parse(vstring);
            } catch (IllegalArgumentException e) {
                return true;
            }

            return false;
        }

    /** Test method for {@link org.tbull.util.SemVer#parse(java.lang.CharSequence)}. */
    @Test
    public void testParse() {
        // version designators that should be successfully parsed
        assertTrue("vequals", vequals(SemVer.parse("1.4.2"), 1, 4, 2, null));
        assertTrue("vequals", vequals(SemVer.parse("1.4"), 1, 4, 0, null));
        assertTrue("vequals", vequals(SemVer.parse("1"), 1, 0, 0, null));
        assertTrue("vequals", vequals(SemVer.parse("1.4.2foo23"), 1, 4, 2, "foo23"));
        assertTrue("vequals", vequals(SemVer.parse("1.4foo23"), 1, 4, 0, "foo23"));
        assertTrue("vequals", vequals(SemVer.parse("1foo23"), 1, 0, 0, "foo23"));
        assertTrue("vequals", vequals(SemVer.parse("v1.4.2"), 1, 4, 2, null));
        assertTrue("vequals", vequals(SemVer.parse("v1.4"), 1, 4, 0, null));
        assertTrue("vequals", vequals(SemVer.parse("v1"), 1, 0, 0, null));
        assertTrue("vequals", vequals(SemVer.parse("v1.4.2foo23"), 1, 4, 2, "foo23"));
        assertTrue("vequals", vequals(SemVer.parse("v1.4foo23"), 1, 4, 0, "foo23"));
        assertTrue("vequals", vequals(SemVer.parse("v1foo23"), 1, 0, 0, "foo23"));

        // strings that are not parsable as a version number
        for (String vs: non_version_strings)
            assertTrue("parse_throws", parse_throws(vs));
    }




    /** Test method for {@link org.tbull.util.SemVer#toString()}.
     *
     *  Depends on the correctness of {@code equals()} and {@code parse()}.
     */
    @Test
    public void testToString() {

        for (VInfo vi: version_strings) {
            SemVer v = new SemVer(vi.major, vi.minor, vi.patch, vi.special);
            // stringify and parse back the version and compare for equalness
            // do that at least two times to test the tostring_cache mechanism
            assertTrue(SemVer.parse(v.toString()).equals(v));
            assertTrue(SemVer.parse(v.toString()).equals(v));
            assertTrue(SemVer.parse(v.toString()).equals(v));
        }
    }


    /** Test method for {@link org.tbull.util.SemVer#toTagString()}.
     *
     *  Depends on the correctness of {@code equals()} and {@code parse()}.
     */
    @Test
    public void testToTagString() {
        for (VInfo vi: version_strings) {
            SemVer v = new SemVer(vi.major, vi.minor, vi.patch, vi.special);
            // stringify and parse back the version and compare for equalness
            // do that at least two times to test the totag_cache mechanism
            assertTrue(SemVer.parse(v.toTagString()).equals(v));
            assertTrue(SemVer.parse(v.toTagString()).equals(v));
            assertTrue(SemVer.parse(v.toTagString()).equals(v));
        }
    }

}
