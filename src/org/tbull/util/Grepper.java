package org.tbull.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;




/** A grep condition.
 *
 *  Classes implementing this interface provide a decision if particular elements should be part of the
 *  result list of a grep process. See the {@code grep} family of functions in {@link Collections}.
 *
 *  <P>A grepper has no state. That is, the result of the {@link #grep(Object)} decision is based solely
 *  on the element passed, not on when it is tested. In other words, the outcome does not depend, for example,
 *  on it's position in the input list, and the result must be consistently the same for multiple invocations,
 *  provided no relevant information in the element is modified.</P>
 */

public interface Grepper<E> {

    /** A {@code Grepper} that rejects all elements, producing an empty result list.
     *
     *  I have no idea what this could be good for, but I'm faithful somebody will come up with a compelling use case.
     */
    public static class NULLGrepper<E> implements Grepper<E> {
        public @Override boolean grep(@SuppressWarnings("unused") E element) { return false; }
    }


    /** A {@code Grepper} that lets all elements pass.
     *
     *  If nothing else, this can serve to join multiple lists into one.
     *  (Yeah, I know... and I'm not even drunk.)
     */
    public static class ONEGrepper<E> implements Grepper<E> {
        public @Override boolean grep(@SuppressWarnings("unused") E element) { return true; }
    }


    /** A {@code Grepper} that inverts the results of another grepper.
     *
     *  If you have a {@code Grepper} and you need exactly the inverse results (i.e. you want to grep the elements
     *  your grepper rejects and want to reject the elements your grepper accepts), then wrap this one around
     *  the original grepper.
     *
     *  <PRE>
     *  Grepper&lt;X&gt; your_grepper;
     *  results = grep(new InverseGrepper&lt;X&gt;(your_grepper), input...);
     *  </PRE>
     */
    public static class InverseGrepper<E> implements Grepper<E> {
        private Grepper<E> grepper;
        public InverseGrepper(Grepper<E> grepper)   { this.grepper = grepper; }
        /** Returns {@code true} if the wrapped grepper returns {@code false}, and vice versa. */
        public @Override boolean grep(E element)    { return !grepper.grep(element); }
    }




    /** A {@code Grepper} that chains multiple client greppers logical-OR-wise.
     *
     *  Greps elements that are grepped by <EM>at least one</EM> of the client greppers. Rejects elements that are
     *  rejected by <EM>all</EM> client greppers.
     *
     *  <P>Since this is a {@link List}, you add client greppers by using the List method {@link List#add(Object)}.
     *  However, do not rely on this class to inherit from a specific {@code List} implementation
     *  (such as {@code ArrayList}), as this may change in the future.</P>
     *
     *  <P>Invokes the client greppers in the order stored in this list. From a performance point of view it is
     *  best to add greppers first that are most likely to accept most of the tested elements.</P>
     *
     *  @see Grepper.ANDGrepper
     */
    public static class ORGrepper<E> extends ArrayList<Grepper<E>> implements Grepper<E> {
        private static final long serialVersionUID = 4380083612522464562L;

        /** Returns {@code true} if <EM>any</EM> of the client greppers returns {@code true}.
         *  Returns {@code false} if <EM>all</EM> client greppers return {@code false}.
         */
        public @Override boolean grep(E element) {
            for (int i = 0; i < this.size(); i++)
                if (this.get(i).grep(element))
                    return true;
            return false;
        }
    }


    /** A {@code Grepper} that chains multiple client greppers logical-AND-wise.
     *
     *  Greps elements that are grepped by <EM>all</EM> client greppers. Rejects elements that are rejected
     *  by <EM>at least one</EM> of the client greppers.
     *
     *  <P>Since this is a {@link List}, you add client greppers by using the List method {@link List#add(Object)}.
     *  However, do not rely on this class to inherit from a specific {@code List} implementation
     *  (such as {@code ArrayList}), as this may change in the future.</P>
     *
     *  <P>Invokes the client greppers in the order stored in this list. From a performance point of view it is
     *  best to add greppers first that are most likely to reject most of the tested elements.</P>
     *
     *  @see Grepper.ORGrepper
     */
    public static class ANDGrepper<E> extends ArrayList<Grepper<E>> implements Grepper<E> {
        private static final long serialVersionUID = -8744162737567268907L;

        /** Returns {@code false} if <EM>any</EM> of the client greppers returns {@code false}.
         *  Returns {@code true} if <EM>all</EM> client greppers return {@code true}.
         */
        public @Override boolean grep(E element) {
            for (int i = 0; i < this.size(); i++)
                if (!this.get(i).grep(element))
                    return false;
            return true;
        }
    }




    /** A {@code Grepper} that matches string-like elements against a regular expression.
     *
     *  <PRE>
     *      List&lt;String&gt; integer_strings = grep(new Grepper.RegexGrepper("[0-9]+"), strings);
     *  </PRE>
     *
     *  @todo   This grepper has not yet been tested.
     */
    public static class RegexGrepper implements Grepper<CharSequence> {
        private Pattern pattern;
        private Matcher matcher;

        /** Constructs a {@code RegexGrepper} from a given regular expression. */
        public RegexGrepper(String regex) throws PatternSyntaxException {
            this.pattern = Pattern.compile(regex);
            this.matcher = null;
        }

        /** Constructs a {@code RegexGrepper} from a precompiled {@code Pattern}. */
        public RegexGrepper(Pattern pattern) {
            this.pattern = pattern;
            this.matcher = null;
        }

        /** Greps elements that match the regular expression as per {@link Matcher#matches()}. */
        public @Override boolean grep(CharSequence element) {
            /*
            if (matcher == null)
                matcher = pattern.matcher(element);
            else matcher.reset(element);

            return matcher.matches();
            */

            return (matcher == null ? (matcher = pattern.matcher(element)) : matcher.reset(element)).matches();
        }
    }




    /** Decides if the element in question should be part of the results of a grep process.
     *
     *  @param      element  The element under test.
     *  @return     {@code true} if the element matches the grep condition, thus should be added to the results,
     *              otherwise {@code false}.
     */
    boolean grep(E element);

}
