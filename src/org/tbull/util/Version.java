package org.tbull.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** Implements version numbers according to T-Bull versioning, which is a variation of the
 *  <A HREF="http://semver.org/">Semantic Versioning</A> (SemVer) recommendation.
 *
 *  The points where this versioning scheme deviates from SemVer, are:
 *  * allow 4 levels of counting instead of 3
 *  ** the same rules apply to the 3rd and 4th number, that is both denote implementation changes, not API changes
 *  * if the major version is 0, the remaining numbers shift their meaning
 *  ** minor version bumping denotes backward incompatible API changes
 *  ** revision bumping denotes backward compatible API changes
 *  ** patch level bumping denotes API non-changes
 *  * the special part is separated by a dash from the rest of the version number
 *  ** the dash is, however, optional
 *
 *  H:\projects\ctml\CTML.(unsorted).version-numbers.text
 *  https://github.com/mojombo/semver.org/issues/5
 *  http://apr.apache.org/versioning.html
 *  http://www.freebsd.org/doc/en/books/developers-handbook/policies-shlib.html
 *
 *  issues:
 *  https://github.com/mojombo/semver.org/issues/26
 *
 *
 *
 */
public class Version implements Comparable<Version> {
    protected static final Pattern special_pattern = Pattern.compile("[A-Za-z][0-9A-Za-z-]*");
    protected static final Pattern version_pattern =
        Pattern.compile("v?(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:\\.(\\d+))?-?([A-Za-z][0-9A-Za-z-]*)?");

    /** The version of this version number implementation. */
    public static final Version VERSION = new Version(0, 1, 0, 0, "draft");

    public final int major;
    public final int minor;
    // TODO: rename (revision, patch) to (patch, tiny)?
    public final int revision;
    public final int patch;
    public final String special;

    private String tostring_cache = null;
    private String totag_cache = null;




    public Version(int major, int minor, int revision) {
        this(major, minor, revision, 0, null);
    }

    public Version(int major, int minor, int revision, int patch) {
        this(major, minor, revision, patch, null);
    }

    public Version(int major, int minor, int revision, int patch, String special) throws IllegalArgumentException {
        if (major < 0) throw new IllegalArgumentException("major version < 0");
        this.major = major;

        if (minor < 0) throw new IllegalArgumentException("minor version < 0");
        this.minor = minor;

        if (revision < 0) throw new IllegalArgumentException("revision < 0");
        this.revision = revision;

        if (patch < 0) throw new IllegalArgumentException("patch level < 0");
        this.patch = patch;

        // TODO does not match the empty string "" - what to do then?
        if (special != null && !special_pattern.matcher(special).matches())
            throw new IllegalArgumentException();
        this.special = special;
    }




        private static int cmp(int a, int b) {
            return a < b ? -1 : (a == b ? 0 : 1);
        }

    /* Compares this version with the specified version for order.
     *
     *  @see Comparable#compareTo(Object)
     */
    public @Override int compareTo(Version o) {
        int i;

        i = cmp(major, o.major);
        if (i != 0) return i;

        i = cmp(minor, o.minor);
        if (i != 0) return i;

        i = cmp(revision, o.revision);
        if (i != 0) return i;

        i = cmp(patch, o.patch);
        if (i != 0) return i;

        if (special == null) return (o.special == null ? 0 : 1);
        if (o.special == null) return -1;
        return special.compareTo(o.special);    // should case differences be ignored?
    }


    public @Override int hashCode() {
        /* generated by Eclipse */
        final int prime = 31;
        int result = 1;
        result = prime * result + this.major;
        result = prime * result + this.minor;
        result = prime * result + this.patch;
        result = prime * result + this.revision;
        result = prime * result + ((this.special == null) ? 0 : this.special.hashCode());
        return result;
    }

    public @Override boolean equals(Object obj) {
        /* generated by Eclipse */
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Version)) return false;
        Version other = (Version) obj;
        if (this.major != other.major) return false;
        if (this.minor != other.minor) return false;
        if (this.patch != other.patch) return false;
        if (this.revision != other.revision) return false;
        if (this.special == null) {
            if (other.special != null) return false;
        } else if (!this.special.equals(other.special)) return false;
        return true;
    }




    public boolean compatibleForUse(SemVer expected) {
        throw new UnsupportedOperationException("not yet implemented");
    }


    /** Checks if the API with this version is stable.
     *
     *  An API is considered stable as soon as it reaches the first major version (1).
     *
     */
    public boolean isStable() {
        return major > 0;
    }


    public SemVer nextMajor() {
        // TODO
        throw new UnsupportedOperationException("not yet implemented");
    }

    public SemVer nextMinor() {
        // TODO
        throw new UnsupportedOperationException("not yet implemented");
    }

    public SemVer nextPatch() {
        // TODO
        throw new UnsupportedOperationException("not yet implemented");
    }




    public static Version parse(CharSequence version) throws IllegalArgumentException {
        int a, b, c, d;
        String as, bs, cs, ds;
        String special;

        Matcher m = version_pattern.matcher(version);
        if (!m.matches()) throw new IllegalArgumentException();

//        System.out.printf("semver.parse: group count: %d\n", Integer.valueOf(m.groupCount()));
//        for (int i = 1; i <= m.groupCount(); i++)
//            System.out.printf("%d: (%s)\n", Integer.valueOf(i), m.group(i));

        System.out.printf("matched: (%s) (%s) (%s) (%s) (%s)\n",
                m.group(1), m.group(2), m.group(3), m.group(4), m.group(5));

        as = m.group(1); a = Integer.parseInt(as);
        bs = m.group(2); b = bs == null ? 0 : Integer.parseInt(bs);
        cs = m.group(3); c = cs == null ? 0 : Integer.parseInt(cs);
        ds = m.group(4); d = ds == null ? 0 : Integer.parseInt(ds);
        special = m.group(5);

        return new Version(a, b, c, d, special);
    }


        private StringBuilder format(StringBuilder sb) {
            sb.append(major).append('.').append(minor).append('.').append(revision).append('.').append(patch);
            if (special != null) sb.append(special);
            return sb;
        }


    /** Returns the version number as a String, as in {@code "A.B.C.D-special"} */
    public @Override String toString() {
        if (tostring_cache == null)
            tostring_cache = format(new StringBuilder()).toString();
        return tostring_cache;
    }

    /** Returns the version number as a String suitable for use as a version control tag,
     *  as in {@code "vA.B.C.D-special"} */
    public String toTagString() {
        if (totag_cache == null)
            totag_cache = format((new StringBuilder()).append('v')).toString();
        return totag_cache;
    }


}
