package org.tbull.util;

import java.io.StringWriter;
import java.io.Writer;




/** Like {@link StringWriter}, but completely unsynchronized.
 *
 *  <P>While {@code StringWriter} writes to a {@link StringBuffer}, which is fully synchronized, this class writes
 *  to a {@link StringBuilder}, which is the unsynchronized version of {@code StringBuffer}.</P>
 *
 *  <P>Use this if you don't need the sync or want to take care of that yourself.</P>
 *
 *  <P>You can use the {@code StringBuilderWriter} and the underlying {@code StringBuilder} interchangeably as long as
 *  you don't do that concurrently from several threads (or you provide for appropriate locking). The writer will
 *  always append to the current end of the buffer, regardless of what you do with the buffer. You get hold of the
 *  underlying {@code StringBuilder} either by constructing the writer with a self-supplied buffer, or by a call to
 *  {@link #getBuilder()}.</P>
 *
 *  <P>For your convenience, this class also implements the {@link CharSequence} interface, so that you can easily
 *  examine the accumulated string data without constructing an intermediate {@code String} object. Again, you can
 *  combine this with interleaving write access as long as you take care of concurrency issues.</P>
 *
 *  <P>Ceterum censeo HTML in Javadoc is the dumbest idea ever.</P>
 *
 *  @see    CharSequenceReader
 */

public class StringBuilderWriter extends Writer implements CharSequence {

    private StringBuilder sb;




    /*
     *  Construction.
     *
     */


    /** Creates a new stringbuilder writer.
     *
     */
    public StringBuilderWriter() {
        sb = new StringBuilder();
    }


    /** Creates a new stringbuilder writer using the specified initial capacity.
     *
     *  @param  initialCapacity             The number of {@code char} values that will fit into the buffer
     *                                      before it is automatically expanded.
     *  @throws IllegalArgumentException    If {@code initialCapacity <= 0}.
     */
    public StringBuilderWriter(int initialCapacity) throws IllegalArgumentException {
        if (initialCapacity <= 0) throw new IllegalArgumentException("Invalid initial buffer size");
        sb = new StringBuilder(initialCapacity);
    }


    /** Creates a new stringbuilder writer using the supplied {@link StringBuilder} instead of creating a new one.
     *
     *  The buffer does not need to be empty. The writer always appends to the end of the buffer.
     */
    public StringBuilderWriter(StringBuilder sb) {
        this.sb = sb;
    }




    /** Returns the underlying {@code StringBuilder}.
     *
     *  This is a life object and still in use!
     *  It is safe to continue using the writer afterwards, you can even intermix calls to the writer and to the
     *  stringbuilder, as long as you don't do that concurrently from several threads. The writer will always
     *  append to the current end of the stringbuilder.
     */
    public StringBuilder getBuilder() {
        return sb;
    }




    /*
     *  Now, the CharSequence interface.
     *  This is quite easy, because the underlying StringBuilder is a CharSequence, too.
     *  So we just have to pass through.
     *
     */

    public @Override char charAt(int index) throws IndexOutOfBoundsException {
        return sb.charAt(index);
    }


    /** Returns the number of characters in the accumulated string data. */
    public @Override int length() {
        return sb.length();
    }

    public @Override CharSequence subSequence(int start, int end) throws IndexOutOfBoundsException {
        return sb.subSequence(start, end);
    }


    /** Returns the results of the operation so far. */
    public @Override String toString() {
        return sb.toString();
    }




    /*
     *  And here we have the Writer related stuff.
     *
     *  We could inherit the implementations of some of these methods from Writer,
     *  but a specific implementation should perform better.
     *
     */

    public @Override StringBuilderWriter append(char c) {
        sb.append(c);
        return this;
    }

    public @Override StringBuilderWriter append(CharSequence csq, int start, int end) throws IndexOutOfBoundsException {
        if (csq == null) write("null", start, end);
        else sb.append(csq, start, end);
        return this;
    }

    public @Override StringBuilderWriter append(CharSequence csq) {
        sb.append(csq == null ? "null" : csq);
        return this;
    }


    public @Override void write(int c) {
        sb.append((char) c);
    }

    public @Override void write(char[] cbuf, int off, int len) throws IndexOutOfBoundsException {
        sb.append(cbuf, off, len);
    }

    public @Override void write(String str) {
        sb.append(str);
    }

    public @Override void write(String str, int off, int len) throws IndexOutOfBoundsException {
        sb.append(str.substring(off, off + len));
    }




    /** Does nothing. */
    public @Override void close() {
        // nothing to do
    }

    /** Does nothing. */
    public @Override void flush() {
        // nothing to do
    }

}
