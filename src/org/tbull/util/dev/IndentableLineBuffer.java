package org.tbull.util.dev;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;


/** @TODO   words to look up:

    indent
    indention
    indentation
    indent(.*) level
    indentable

    file:///D:/doc/java/CodeConventions/CodeConventions.doc3.html#262


    @TODO   upgrade append() methods to take varargs, e.g. append(String... s)
*/


    /** A single indentable line. {@link IndentableLineBuffer} consists of zero or more of these.
     *
     */

    class IndentableLine implements Cloneable {
        /** The line's individual indent level. */
        int indent_level;   // "indentation"?
        /** The line's text. */
        StringBuilder text;


        IndentableLine() {
            indent_level = 0;
            text = new StringBuilder();
        }

        IndentableLine(String s) {
            indent_level = 0;
            text = new StringBuilder(s);
        }

        IndentableLine(StringBuilder sb) {
            indent_level = 0;
            text = new StringBuilder(sb);
        }

        /** Returns a deep copy of this object. */
        @Override
        public IndentableLine clone() {
            IndentableLine that = new IndentableLine(this.text);
            that.indent_level = this.indent_level;
            return that;
        }
    }




/** Provides a line buffer of individually indentable lines.
 *
 *  Maintains the notion of a current line. Once the current line is dismissed (by beginning a new line),
 *  the line is no longer individually accessible, and its contents (text and, for the most part, the individual indent level [REALLY?])
 *  are considered immutable for user operations.
 *
 *  Detail on indent levels:
 *      base [indent] level
 *      individual [indent] level
 *      resulting [indent] level
 *      default [individual] [line] [indent] level
 *      If the resulting indent level is less than 0, it is assumed to be 0.
 *
 *  add/appendLines(): The notion is, that the sub-buffer is *integrated* into the main buffer,
 *  thus the sub-buffer is expected to be dismissed thereafter. Therefore it does not hurt to screw
 *  it up (by messing with the indent levels). Use clone(), if you don't like it.
 *
 *  Unless otherwise documented, all methods return this same object to facilitate [BANDWURM invocations (hibernate: method chaining)].
 *
 *  implements java.lang.Appendable, so the buffer can be specified as the destination of a java.util.Formatter.
 *  However, the buffer will not properly deal with newlines in the appended char sequences, so don't use them.
 *  Use newLine() then, e.g. like lb.format(...).newLine().format(...)
 */

//public class IndentableLineBuffer implements Cloneable {
public class IndentableLineBuffer implements Cloneable, Appendable, DataDumpable {


        public IndentableLineBuffer dumpData() {
            IndentableLineBuffer lb = new IndentableLineBuffer();


            lb.addLine(this.getClass().getName() + " = {");
            lb.setDefaultLineIndentLevel(1);
            lb.addLine("base_indent: " + base_indent);
            lb.addLine("default_line_indent: " + default_line_indent);
            lb.addLine("indentation: \"" + indentation + "\"");
            // lb.addLine("line_separator: \"" + line_separator + "\"");   // @TODO hexdump?

            lb.addLine("lines: [");
            lb.setDefaultLineIndentLevel(2);

            for (IndentableLine l: lines)
                lb.addLine(String.format("%2d, %s", Integer.valueOf(l.indent_level), l.text.toString()));

            lb.setDefaultLineIndentLevel(1);
            lb.addLine("]");

            lb.setDefaultLineIndentLevel(0);
            lb.addLine("}");

            lb.setBaseIndentLevel(1);
            return lb;
        }




    /** The list of indentable lines. */
    protected ArrayList<IndentableLine> lines;
    /** Points to the current (== last) line of the line buffer. */
    protected IndentableLine current_line;
    /** The base indent level, which is added to the individual indent levels of each line. */
    protected int base_indent;
    /** The default individual line indent level, which is assigned to a line if not otherwise provided for. */
    protected int default_line_indent;
    /** The indentation string, inserted by toString() once per indent level. */
    protected String indentation;
    /** The line separator (end of line marker) used by toString() to join lines. */
    protected String line_separator;




    /** Constructs a new LineBuffer, initially consisting of no lines (empty). */
    public IndentableLineBuffer() {
        this.lines = new ArrayList<IndentableLine>();
        /*  these are standard default values, so we don't need to init:
        this.current_line = null;
        this.base_indent = 0;
        this.default_line_indent = 0;
        */
        this.indentation = "    ";
        this.line_separator = java.lang.System.getProperty("line.separator");
    }


    /** Constructs a new LineBuffer, initially consisting of a line given by s. */
    public IndentableLineBuffer(String s) {
        this();
        addLine(s);
    }


    /** Constructs a new LineBuffer, initially empty, with the given base indent level. */
    public IndentableLineBuffer(int base_indent_level) {
        this();
        this.base_indent = base_indent_level;
    }




    /** Sets the indentation string to use on stringification.
     *
     *  The string represents one level of indentation. Popular values are a single
     *  tab character ("\t") or 4 consecutive spaces.
     */

    public IndentableLineBuffer setIndentation/*String*/(String indentation) {
        this.indentation = indentation;
        return this;
    }


    /** Sets the line separator used by {@link #toString()}. */
    public IndentableLineBuffer setLineSeparator(String sep) {
        this.line_separator = sep;
        return this;
    }


    /** Sets the base indent level. */
    public IndentableLineBuffer setBaseIndentLevel(int indent_level) {
        this.base_indent = indent_level;
        return this;
    }


    /** Sets the default individual line indent level. */
    public IndentableLineBuffer setDefaultLineIndentLevel(int indent_level) {
        this.default_line_indent = indent_level;
        return this;
    }




    /** Indents existing and future lines by modifing the base indent level.
     *
     *  This affects both, already existing as well as subsequently added lines.
     */

    public IndentableLineBuffer indent(int indent) {
        this.base_indent += indent;
        return this;
    }


    /** Indents existing lines by modifying each line's individual indent level. */
    public IndentableLineBuffer indentExisting(int indent) {
        for (IndentableLine l: lines) l.indent_level += indent;
        return this;
    }


    /** Indents the current line. */
    public IndentableLineBuffer indentCurrentLine(int indent) {
        this.current_line.indent_level += indent;
        return this;
    }




    /** Begins a new (empty) line. */
    public IndentableLineBuffer newLine() {
        addLine("");
        return this;
    }


    /** Begins a new (empty) line with the given indent level. */
    public IndentableLineBuffer newLine(int indent) {
        addLine(indent, "");
        return this;
    }


    /** Appends the contents of s to the current last line. */
    public IndentableLineBuffer append(String s) {
        if (lines.isEmpty()) addLine(s); else current_line.text.append(s);
        return this;
    }


    /** Appends the contents of sb to the current last line. */
    public IndentableLineBuffer append(StringBuilder sb) {
        if (lines.isEmpty()) addLine(sb); else current_line.text.append(sb);
        return this;
    }


    /** Appends the contents of l to the current last line, ignoring its indent level. */
/** @TODO    manage the indent level, depending on what we want and the addLine() behaviour.
             note that addLine(IndentableLine) treats the level different than append(String) does
*/
    protected IndentableLineBuffer append(IndentableLine l) {
        if (lines.isEmpty()) addLine(l.text); else current_line.text.append(l.text);
        return this;
    }




    /** Appends the specified character to the current last line.
     *
     *  This is part of the java.lang.Appendable interface, which enables the IndentableLineBuffer to be specified as the
     *  destination of a {@link java.util.Formatter}.
     */
    public IndentableLineBuffer append(char c) {
        if (lines.isEmpty()) newLine();
        // luckily, StringBuilder implements Appendable as well, so we merely hand over the request
        current_line.text.append(c);
        return this;
    }


    /** Appends the specified character sequence to the current last line.
     *
     *  This is part of the java.lang.Appendable interface, which enables the IndentableLineBuffer to be specified as the
     *  destination of a {@link java.util.Formatter}.
     */
    public IndentableLineBuffer append(CharSequence csq) {
        if (lines.isEmpty()) newLine();
        // luckily, StringBuilder implements Appendable as well, so we merely hand over the request
        current_line.text.append(csq);
        return this;
    }


    /** Appends a subsequence of the specified character sequence to the current last line.
     *
     *  This is part of the java.lang.Appendable interface, which enables the IndentableLineBuffer to be specified as the
     *  destination of a {@link java.util.Formatter}.
     */
    public IndentableLineBuffer append(CharSequence csq, int start, int end) {
        if (lines.isEmpty()) newLine();
        // luckily, StringBuilder implements Appendable as well, so we merely hand over the request
        current_line.text.append(csq, start, end);
        return this;
    }




    /** Append a formatted string to the current last line using the specified format string and arguments.
     *
     *  Constructs a {@link java.util.Formatter}, whose output is connected to this {@code IndentableLineBuffer},
     *  and passes all arguments to its {@link java.util.Formatter#format(String, Object...)} method. See there for
     *  a description of the parameters and the format string.
     */
    public IndentableLineBuffer format(String format, Object... args)
        throws java.util.IllegalFormatException, java.util.FormatterClosedException
    {
        (new java.util.Formatter(this)).format(format, args);
        return this;
    }

    /** Append a formatted string to the current last line using the specified locale, format string, and arguments.
     *
     *  Constructs a {@link java.util.Formatter}, whose output is connected to this {@code IndentableLineBuffer},
     *  and passes all arguments to its {@link java.util.Formatter#format(Locale, String, Object...)} method. See there for
     *  a description of the parameters and the format string.
     */
    public IndentableLineBuffer format(java.util.Locale l, String format, Object... args)
        throws java.util.IllegalFormatException, java.util.FormatterClosedException
    {
        (new java.util.Formatter(this)).format(l, format, args);
        return this;
    }




    /** Begins a new line and copies the contents of s into it.
     *
     *  The individual indent level of the new line is take from the default indent level.
     */

    public IndentableLineBuffer addLine(String s) {
        lines.add(current_line = new IndentableLine(s));
        current_line.indent_level = default_line_indent;
        return this;
    }


    /** Begins a new line with the given indent level and copies the contents of s into it. */
    public IndentableLineBuffer addLine(int indent, String s) {
        lines.add(current_line = new IndentableLine(s));
        current_line.indent_level = indent;
        return this;
    }




    /** Begins a new line and copies the contents of sb into it.
     *
     *  The individual indent level of the new line is take from the default indent level.
     */

    public IndentableLineBuffer addLine(StringBuilder sb) {
        lines.add(current_line = new IndentableLine(sb));
        current_line.indent_level = default_line_indent;
        return this;
    }


    /** Begins a new line with the given indent level and copies the contents of sb into it. */
    public IndentableLineBuffer addLine(int indent, StringBuilder sb) {
        lines.add(current_line = new IndentableLine(sb));
        current_line.indent_level = indent;
        return this;
    }




    /** Adds the given IndentableLine as a new line, /not/ copying its contents.
     *
     *  The line's individual indent level remains untouched (yet subject to our base level).
     */

    protected IndentableLineBuffer addLine(IndentableLine l) {
        lines.add(current_line = l);
        return this;
    }




    /** Adds the lines of the other line buffer to this one.
     *
     *  The individual lines are /not/ copied, they are taken over. Each line is assigned a new individual indent level,
     *  made up of its former resulting (base + individual) indent level. The new resulting indent level then is the
     *  sum of the new individual level plus this buffer's base level. This buffer's default level is not
     *  taken into account.
     *
<PRE>
new resulting = this base + new individual
              = this base + former resulting
              = this base + other base + former individual
</PRE>
     *
     *  After this operation the other buffer is pretty messed up. The idea is, the other buffer is merged into
     *  this one and then forgotten. If you don't like this, {@link #clone()} the buffer in advance.
     */

    public IndentableLineBuffer addLines(IndentableLineBuffer other) {

        /* When taking over the lines to this buffer, their base indent gets lost, therefore
         * each line has to merge its buffer's base indent level into its individual level.
         */
        for (IndentableLine l: other.lines) {
            l.indent_level += other.base_indent;
            this.lines.add(l);
            this.current_line = l;      // updating current_line within the loop, though less efficient, deals properly with the lines.size()==0 case
        }

        /* Undocumented feature: After we have modified the individual line indents, reset other buffer's base indent
         *  so that resulting indents stay unmodified for now.
         */
        other.base_indent = 0;

        return this;
    }




    /** Adds the lines of the other line buffer to this one, concatenating the [letzte und erste (adjoining?)] lines.
     *
     *  Works much like {@link #addLines(IndentableLineBuffer)}, except that the first line of the other line buffer is
     *  appended to the current last line of this line buffer, ignoring the indent level of the other line.
     *
     *  The individual lines are /not/ copied, they are taken over. The other buffer has to be considered destroyed
     *  after this operation. If you don't like this, {@link #clone()} the buffer in advance.
     */

    public IndentableLineBuffer appendLines(IndentableLineBuffer other) {
        if (!other.lines.isEmpty()) {
            this.append(other.lines.remove(0));     // play it hard
            this.addLines(other);                   // let addLines() do the rest
        }

        return this;
    }




    /** Returns all lines in the LineBuffer as String, separated by newlines.
     *
     *  The last line has a newline appended, too.
     *
     */

    public @Override String toString() {
//        Iterator<IndentableLine> i;
        StringBuilder result;
        int char_count, indent_sum;


        // count the number of characters and sum up all indentation
        char_count = indent_sum = 0;
        for (IndentableLine l: lines) {
            char_count += l.text.length();
            indent_sum += (base_indent+l.indent_level) < 0 ? 0 : (base_indent+l.indent_level);
        }

        // alloc StringBuilder with projected required capacity so that the StringBuilder will not need to resize
        result = new StringBuilder(
            char_count +
            indent_sum * indentation.length() +
            lines.size() * line_separator.length()
        );

        // join the lines properly indented and separated by newline
        for (IndentableLine l: lines) {
            for (int j = 0; j < base_indent + l.indent_level; j++)    // this deals properly with resulting indent levels < 0
                result.append(indentation);
            result.append(l.text).append(line_separator);
        }

        return result.toString();
    }




    /** Creates a deep copy of this object.
     *
     *  The clone is completely independent of this line buffer object.
     *
     *  @return     The newly created clone.
     */

    public @Override IndentableLineBuffer clone() {
        IndentableLineBuffer that;
        Iterator<IndentableLine> i;


        // copy all primitive fields to a new object (the clone)
        //  the clone will point to the exact same lines list
        try {
            that = (IndentableLineBuffer) super.clone();
        } catch (CloneNotSupportedException e) {
            /* This shouldn't happen, since we are Cloneable. Anyway, CloneNotSupportedException is a checked
             *  exception thrown by Object.clone(), so we have to deal with it. We turn it into an approriate
             *  unchecked exception, which indicates an internal JVM error. */
/*  AssertionError would be an alternative (used by EnumSet) */
            throw (InternalError) new InternalError().initCause(e);
        }

        // replace the clone's lines list by a new (empty) one
        that.lines = new ArrayList<IndentableLine>();

        // fill the clone's lines list with clones of our lines
        i = this.lines.iterator();
        while (i.hasNext())
            that.addLine(i.next().clone());         // sets current_line appropriately

        return that;
    }


}

