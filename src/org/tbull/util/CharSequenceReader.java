package org.tbull.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;




/** Like {@link StringReader}, but reads arbitrary {@link CharSequence}s and is unsynchronized.
 *
 *  <P>{@code StringReader} has some weird behaviour. Not only are all methods synchronized (which may or may not
 *  be desirable), also it throws {@code IOException}s around without reason. And it is very sloppily documented.
 *  You have to read the source code to find out the aforementioned facts.</P>
 *
 *  <P>This class is not synchronized, you can decide yourself if you need sync. It does not throw checked
 *  exceptions except for {@link #mark(int) mark}/{@link #reset() reset}. (Only unchecked exceptions, which indicate
 *  programming errors at your side, are otherwise thrown.) And it generalizes the source material to any
 *  {@link CharSequence}, which {@code String} is only one example of, so you can read from a
 *  {@code StringBuilder} or something without constructing an intermediate {@code String}.</P>
 *
 *  <P>The last point, however, has some implications. In contrast to a {@code String}, a {@code CharSequence}
 *  is not necessarily immutable. For the mark/reset (rewind) mechanism to work reliably, the reader would have to
 *  buffer character data separately from the backing sequence, thus degrading performance, sucking memory and making
 *  everything more complicated. Therefore we decided to support rewinding only for character sequences known to be
 *  immutable. If you need rewinding with a mutable stream, you can easily wrap a {@link BufferedReader} around, which
 *  performs the necessary buffering.</P>
 *
 *  <P>The reader knows that {@code String}s are immutable. Any other classes implementing {@code CharSequence} are
 *  assumed to be mutable unless you tell otherwise. Use {@link #setImmutable(boolean) setImmutable} to tell the
 *  reader that the backing sequence is immutable, enabling direct mark/reset support. Make sure you tell the truth,
 *  as misuse of this feature may cause unpredictable behaviour!</P>
 *
 *  <P><STRONG>Note well:</STRONG> Obviously, if the source sequence is sync'd on its own, like a {@link StringBuffer},
 *  that sync will not cease to operate only because the sequence is wrapped with this reader. Yet you must not rely
 *  on that sync, because it only syncs the internals of the sequence, not the internals of the reader. Thus, in a
 *  concurrent environment, you must provide external sync, regardless of the implementation of the underlying
 *  sequence.</P>
 *
 *  <P>Ceterum censeo HTML in Javadoc is the dumbest idea ever.</P>
 *
 *  @see    StringBuilderWriter
 */

 /* Rationale: The mark/reset mechanism would also work with mutable sequences, provided that the sequence only ever
  *     grows in size, but never shrinks and never mutates characters at positions that were already read. So we could
  *     have a setOnlyEverGrows() instead of setImmutable(). However, that appears rather unusual, so we wait until
  *     somebody comes up with a compelling use case for it.
  */

public class CharSequenceReader extends Reader {

    protected CharSequence cs;
    protected boolean immutable;
    protected int position;
    protected int mark;

    protected String s;
    protected StringBuilder sb;
    protected StringBuffer sbuf;




    /** Constructs a reader that reads from {@code cs}.
     *
     *  If {@code cs} happens to be a {@code String}, the sequence is automatically
     *  {@link #setImmutable(boolean) flagged immutable}.
     */
    public CharSequenceReader(CharSequence cs) {
        this.cs = cs;
        immutable = false;
        position = mark = 0;
        s = null; sb = null; sbuf = null;

        if (cs instanceof String) {
            s = (String) cs;
            immutable = true;
        } else if (cs instanceof StringBuilder) {
            sb = (StringBuilder) cs;
        } else if (cs instanceof StringBuffer) {
            sbuf = (StringBuffer) cs;
        } /* else if (cs instanceof whatever) {
            // (add more classes known to be immutable or that receive special treatment)
        } */
    }


    /** Sets the immutable flag for the underlying sequence.
     *
     *  <P>The {@link #mark(int) mark}/{@link #reset() reset} mechanism is only supported for immutable sequences,
     *  such as {@code String}. Unless flagged immutable, rewinding is denied by the reader.</P>
     *
     *  <P>Use this, if you know the backing {@code CharSequence} is immutable or a mutable sequence
     *  will not change over the lifetime of this reader as per your application logic.</P>
     *
     *  <P>The flag is set to {@code false} upon the reader's construction, except if the sequence happens to be a
     *  {@code String}, in which case the flag is set to {@code true}, because strings are always immutable.</P>
     *
     *  <P><STRONG>Use with care!</STRONG> Misuse of this feature may cause unpredictable behaviour!</P>
     *
     *  @param immutable    Whether the reader should treat the backing sequence as immutable.
     */
    public void setImmutable(boolean immutable) {
        this.immutable = immutable;
    }




    /** Returns the current length of the underlying char sequence. */
    public int length() {
        return cs.length();
    }


    /** Returns the current number of characters remaining to be read from the underlying char sequence.
     *
     *  In the event that the underlying sequence shrinked in size below the current read position, the
     *  returned value is negative.
     *
     *  @return     Remaining number of characters.
     */
    public int remaining() {
        return cs.length() - position;
    }




    /** Tells whether this stream supports the {@link #mark(int)} operation.
     *
     *  This reader supports rewinding the stream if and only if the underlying {@code CharSequence} is immutable.
     *
     *  @see #setImmutable(boolean)
     */
    public @Override boolean markSupported() {
        return immutable;
    }


    /** Marks the current position in the stream for later repositioning.
     *
     *  Invalidates any previously set marks. The {@code readAheadLimit} parameter is ignored.
     *
     *  <P>Due to the specification of this method by {@link Reader} we are forced to use {@code IOException}
     *  to signal that the mark/reset mechanism is not available for this reader
     *  (where {@link UnsupportedOperationException} would be more appropriate). But we promise that we
     *  throw it only if this reader is not backed by an immutable sequence, i.e. {@link #markSupported()} would
     *  return {@code false}.</P>
     *
     *  @throws IOException     If mark/reset is not supported for the underlying sequence.
     *
     *  @see #reset()
     *  @see #setImmutable(boolean)
     */
    /*  Rationale: Throwing UnsupportedOperationException instead of IOException is not an option because some code
     *      could rely on the exception to be thrown instead of asking markSupported().
     */
    public @Override void mark(@SuppressWarnings("unused") int readAheadLimit) throws IOException {
        if (!immutable) throw new IOException("mark() not supported");
        mark = position;
    }


    /** Repositions the stream to the previously marked position.
     *
     *  The mark is not invalidated by this operation, so you can reset to the same position over and over again
     *  without calling {@code mark()} each time.
     *
     *  <P>Due to the specification of this method by {@link Reader} we are forced to use {@code IOException}
     *  to signal that the mark/reset mechanism is not available for this reader
     *  (where {@link UnsupportedOperationException} would be more appropriate). But we promise that we
     *  throw it only if this reader is not backed by an immutable sequence, i.e. {@link #markSupported()} would
     *  return {@code false}.</P>
     *
     *  @throws IOException     If mark/reset is not supported for the underlying sequence.
     *
     *  @see #mark(int)
     *  @see #setImmutable(boolean)
     */
    /*  Rationale: Throwing UnsupportedOperationException instead of IOException is not an option because some code
     *      could rely on the exception to be thrown instead of asking markSupported().
     */
    public @Override void reset() throws IOException {
        if (!immutable) throw new IOException("reset() not supported");
        position = mark;
    }




    public @Override int read() {
        return position < cs.length() ? cs.charAt(position++) : -1;
    }


    public @Override int read(char[] cbuf, int off, int len) throws IndexOutOfBoundsException {
        // check destination boundaries
        if (off < 0 || len < 0 || off + len < 0 || off + len > cbuf.length)
            throw new IndexOutOfBoundsException();
        if (len == 0) return 0;

        int cslen = cs.length();

        if (position >= cslen) return -1;
        if (position + len > cslen) len = cslen - position;

        /* optimized copying for a String backend */
        if (s != null) {
            s.getChars(position, position + len, cbuf, off);
            position += len;
            return len;
        }
        /* optimized copying for a StringBuilder backend */
        if (sb != null) {
            sb.getChars(position, position + len, cbuf, off);
            position += len;
            return len;
        }
        /* optimized copying for a StringBuffer backend */
        if (sbuf != null) {
            sbuf.getChars(position, position + len, cbuf, off);
            position += len;
            return len;
        }

        // add more optimized copying here

        /* if nothing else, we copy characters one by one */
        int n = len;
        while (len-- > 0) cbuf[off++] = cs.charAt(position++);
        return n;
    }


    public @Override int read(char[] cbuf) {
        return read(cbuf, 0, cbuf.length);
    }


    public @Override int read(CharBuffer target) {
        int len;
        char[] cbuf;

        len = target.remaining();
        if (len == 0) return 0;

        cbuf = new char[len];
        len = read(cbuf, 0, len);
        if (len > 0) target.put(cbuf, 0, len);

        return len;
    }




    /** Tells whether this stream is ready to be read without blocking, which is always true.
     *
     *  @return     {@code true}
     */
    public @Override boolean ready() {
        return true;
    }




    public @Override long skip(long n) throws IllegalArgumentException {
        /*  Rationale: Why are negeative values for n not accepted? StringReader does it.
         *      That is strictly an interface violation by StringReader. Some code could rely on
         *      IllegalArgumentException to be thrown in the case of a negative value n.
         */
        if (n < 0) throw new IllegalArgumentException();
        if (n == 0) return 0;

        // EOF is not signalled by this method, we just skip no more chars than available, which might be 0
        int max = cs.length() - position;
        if (n > max) n = max;

        position += n;
        return n;
    }




    /** Does nothing. */
    public @Override void close() {
        // nothing to do
    }

}

