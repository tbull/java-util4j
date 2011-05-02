package org.tbull.util;




/** A translation in the mapping process.
 *
 *  Classes implementing this interface provide the translation for one element from its original (input) type
 *  to the target (output) type. See the {@code map} family of functions in {@link Collections}.
 *
 *  <P>A mapper has no state. That is, the result of the {@link #map(Object)} translation is based solely
 *  on the element passed, not on when it is translated. In other words, the outcome does not depend, for example,
 *  on it's position in the input list, and the result must be consistently the same for multiple invocations,
 *  provided no relevant information in the element is modified.</P>
 *
 *  @param <I>   The type of the input elements.
 *  @param <O>   The type of the output elements.
 */
public interface Mapper<I, O> {

    /** A Mapper that maps according to an identity function. */
    public static class IdentityMapper<E> implements Mapper<E, E> {
        public @Override E map(E element) {
            return element;
        }
    }




    /** Maps the element under translation from its original (input) type to the target (output) type of the
     *  mapping process.
     *
     *  @param      element  The element to be translated.
     *  @return     The translated element.
     */
    O map(I element);

    // TODO: allow any number of O values per I element
}
