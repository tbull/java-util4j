package org.tbull.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;




/** Utilities for dealing with {@link java.util.Collection}s and alike.
 *
 *
 *  <P>Instead of insisting on {@code Collection}s or even more specific structures like {@code List}s, the tools here
 *  deal with {@link Iterable}s (each Collection is just another Iterable) and {@link java.util.Iterator}s. This helps you in
 *  using the tools on your custom collections-like structures that don't actually implement {@code Collection}, but
 *  do implement {@code Iterable}. If your source material is not even iterable (like {@link Map}s), you may still
 *  get hold of an {@code Iterator} somehow (like {@code Map.keySet().iterator()}), and you can still use the
 *  alternative incarnation of each of the functions.</P>
 *
 *  <P>Ah yes, the declarations look kind of scary with all the generic type parameters, but don't be afraid, it's
 *  actually quite easy. Some examples:</P>
 *
 *  <H4>Grepping</H4>
 *
 *  <PRE>
 *      import org.tbull.util.Collections;
 *      import org.tbull.util.Grepper;
 *
 *      /* a Grepper defines what properties elements need to have to get grepped
 *       *  this grepper choses each Node which is collapsed (not expanded)
 *       *  (the Node class is kindly provided by your imagination)
 *       *&#47;
 *      private static class CollapsedGrepper implements {@link Grepper}<Node> {
 *          public @Override boolean grep(Node element) {
 *              return !element.expanded();
 *          }
 *      }
 *
 *      /* grep elements satisfying the collapsed condition from the nodes list &#42;/
 *      List<Node> nodes, collapsed_nodes;
 *      nodes = ... (get it from somewhere)
 *      collapsed_nodes = Collections.grep(new CollapsedGrepper(), nodes);
 *
 *      /* the same with a self-supplied destination list *&#47;
 *      collapsed_nodes = new LinkedList<E>();
 *      Collections.grep(collapsed_nodes, new CollapsedGrepper(), nodes);
 *          // returns collapsed_nodes for your convenience
 *
 *      /* with multiple input lists *&#47;
 *      collapsed_nodes = Collections.grep(new CollapsedGrepper(), nodes, nodes2, nodes3);
 *
 *      /* for frequent use of grep in a compilation unit, consider static import *&#47;
 *      import static org.tbull.util.Collections.grep;
 *      collapsed_nodes = grep(new CollapsedGrepper(), nodes);
 *
 *      /* iterators and chaining *&#47;
 *      // TODO
 *
 *
 *
 *      /* count only *&#47;
 *      // TODO
 *
 *  </PRE>
 *
 *  <P>If you want to grep {@code String}s that match a regular expression, give {@link Grepper.RegexGrepper} a shot.</P>
 *
 *  <H4>Mapping</H4>
 *
 *  <H4>Unchecked cast warnings</H4>
 *
 *  <P>When using these functions, upto and including Java 6 the compiler will issue warnings over type safety, like
 *  "A generic array of Iterable&lt;Integer&gt; is created for a varargs parameter". Don't blame yourself, this
 *  warning is not your fault. It's due to a conceptual incompatibility of Java arrays and generics (and variable
 *  length argument lists are passed as an array to the function). The only thing you can do is to add the annotation</P>
 *  <PRE>   @SuppressWarnings("unchecked")</PRE>
 *  <P>to the method from which you invoke these functions.</P>
 *
 *  <P>Beginning with Java 7, these warnings are no longer generated.</P>
 *
 *  @todo   finish class documentation
 *  @todo   provide docs for map family of functions
 *  @todo   tests for map family of functions
 *
 *  <P STYLE="font-variant:small-caps">Ceterum censeo HTML in Javadoc is the dumbest idea ever.</P>
 */

public class Collections {


        private static abstract class AbstractLazyIterator<I, O> implements IterableIterator<O> {
            protected Iterable<I>[] lists;
            protected Iterator<I>[] iterators;
            protected Iterator<I> it;           // null if there is no or no more input material
            protected int i;


            /** Constructs an AbstractLazyIterator that iterates over the given iterables (lists). */
            public AbstractLazyIterator(Iterable<I>[] lists) {
                this.lists = lists;
                this.iterators = null;

                if (lists.length == 0) {
                    it = null;
                    i = -1;     // this value has no documented meaning, don't rely on it!
                } else
                    it = lists[i = 0].iterator();
            }

            /** Constructs an AbstractLazyIterator that fetches from the given iterators. */
            public AbstractLazyIterator(Iterator<I>[] iterators) {
                this.lists = null;
                this.iterators = iterators;

                if (iterators.length == 0) {
                    it = null;
                    i = -1;     // this value has no documented meaning, don't rely on it!
                } else
                    it = iterators[i = 0];
            }


            /* Returns true if it loaded the next iterator, false if there is no more input material. */
            protected boolean load_next_it() {
                i++;
                it = null;
                if (lists != null) {
                    if (i >= lists.length) return false;
                    it = lists[i].iterator();
                } else {
                    if (i >= iterators.length) return false;
                    it = iterators[i];
                }
                return true;
            }


            public abstract @Override boolean hasNext();
            public abstract @Override O next();
            public abstract @Override void remove() throws UnsupportedOperationException;

            public @Override Iterator<O> iterator() {
                return this;
            }

        }




        /** An iterator that provides the {@code grepLazy} functionality.
         *
         *  <P>The lazy iterator guarantees to fetch only so many elements from the input lists/iterators as are
         *  necessary to compute the result of a call to {@code hasNext()} or {@code next()}. Also, an input
         *  list/iterator is not queried before the input before it is exhausted.</P>
         *
         *  <P>You can get the lazy iterator by direct instantiation or using one of the {@code grepLazy} functions.</P>
         *
         *  TODO: remove
         *
         *  <P>{@code RuntimeException}s from the input iterators are passed along. Notably, the iterators from the
         *  {@linkplain java.util Collections framework} throw {@link java.util.ConcurrentModificationException}
         *  when they detect you didn't synchronize properly.</P>
         *
         *  <P>This iterator implements {@link Iterator} and {@link Iterable} at the same time. In case you chain
         *  multiple {@code grep/map} invocations as in<BR>
         *  {@code grepLazy(grepper2, grepLazy(grepper1, lists))}<BR>
         *  there arises an ambiguity, because the functions come in an overloaded version for each of them
         *  to take as input, so the compiler will not know which version to choose. You can easily resolve the
         *  ambiguity by either invoking the {@code iterator()} method on the intermediate {@link IterableIterator}
         *  like<BR>
         *  {@code grepLazy(grepper2, grepLazy(grepper1, lists).iterator())}<BR>
         *  or by casting it to a pure {@code Iterator}<BR>
         *  {@code grepLazy(grepper2, (Iterator) grepLazy(grepper1, lists))}.</P>
         */
        public static class LazyGrepIterator<E> extends AbstractLazyIterator<E, E> implements IterableIterator<E> {
            private Grepper<? super E> grepper;
            private E e;        // non-null only if hasNext() has chosen an element for next() delivery
            private boolean removable;      // not yet implemented


            /** Constructs a LazyGrepIterator that iterates over the given iterables (lists). */
            public LazyGrepIterator(Grepper<? super E> grepper, Iterable<E>[] lists) {
                super(lists);
                this.grepper = grepper;
                e = null;
                removable = false;
            }

            /** Constructs a LazyGrepIterator that fetches from the given iterators. */
            public LazyGrepIterator(Grepper<? super E> grepper, Iterator<E>[] iterators) {
                super(iterators);
                this.grepper = grepper;
                e = null;
                removable = false;
            }


            /* If this returns true, {@code e} is guaranteed to have an element for delivery. */
            public @Override boolean hasNext() {
                if (e != null) return true;     // you had already asked this!
                if (it == null) return false;   // no (more) iterators -- no elements

                removable = false;
                while (true) {
                    while (it.hasNext()) {
                        e = it.next();
                        if (grepper.grep(e)) return true;
                    }
                    e = null;

                    if (!load_next_it()) return false;
                }
            }


            public @Override E next() throws NoSuchElementException {
                if (e == null)      // either caller hasn't called hasNext() before or there are no elements left
                    hasNext();

                if (e != null) {
                    E temp = e;
                    e = null;
                    removable = true;
                    return temp;
                }

                /* no elements left. */
                throw new NoSuchElementException();
            }


            /** Always throws {@code UnsupportedOperationException}. */
            /*  Actually it would be possible to provide remove() under the condition that hasNext() hasn't been
             *  called since the last next(), but what would be the point of deleting through a grep result?
             *  Removing exactly the grepped results? Ok, that might make sense. Later, perhaps. */
            public @Override void remove() throws IllegalStateException, UnsupportedOperationException {
                if (!removable) throw new IllegalStateException();
                it.remove();
            }


            public @Override Iterator<E> iterator() {
                return this;
            }
        }




        /** An iterator that provides the {@code mapLazy} functionality.
         *
         *  <P>The lazy iterator guarantees to fetch only one element from the input lists/iterators at each call to
         *  {@code next()}. Also, an input list/iterator is not queried before the input before it is exhausted.</P>
         *
         *  <P>You can get the lazy iterator by direct instantiation or using one of the {@code mapLazy} functions.</P>
         *
         *  TODO: remove
         *
         *  <P>{@code RuntimeException}s from the input iterators are passed along. Notably, the iterators from the
         *  {@linkplain java.util Collections framework} throw {@link java.util.ConcurrentModificationException}
         *  when they detect you didn't synchronize properly.</P>
         *
         *  <P>This iterator implements {@link Iterator} and {@link Iterable} at the same time. In case you chain
         *  multiple {@code grep/map} invocations as in<BR>
         *  {@code mapLazy(mapper2, mapLazy(mapper1, lists))}<BR>
         *  there arises an ambiguity, because the functions come in an overloaded version for each of them
         *  to take as input, so the compiler will not know which version to choose. You can easily resolve the
         *  ambiguity by either invoking the {@code iterator()} method on the intermediate {@link IterableIterator}
         *  like<BR>
         *  {@code mapLazy(mapper2, mapLazy(mapper1, lists).iterator())}<BR>
         *  or by casting it to a pure {@code Iterator}<BR>
         *  {@code mapLazy(mapper2, (Iterator) mapLazy(mapper1, lists))}.</P>
         */
        public static class LazyMapIterator<I, O> extends AbstractLazyIterator<I, O> implements IterableIterator<O> {
            private Mapper<? super I, ? extends O> mapper;


            /** Constructs a LazyMapIterator that iterates over the given iterables (lists). */
            public LazyMapIterator(Mapper<? super I, ? extends O> mapper, Iterable<I>[] lists) {
                super(lists);
                this.mapper = mapper;
            }

            /** Constructs a LazyMapIterator that fetches from the given iterators. */
            public LazyMapIterator(Mapper<? super I, ? extends O> mapper, Iterator<I>[] iterators) {
                super(iterators);
                this.mapper = mapper;
            }


            /* If this returns true, {@code e} is guaranteed to have an element for delivery. */
            public @Override boolean hasNext() {
                if (it == null) return false;   // no (more) iterators -- no elements

                if (it.hasNext()) return true;
                if (!load_next_it()) return false;
                return it.hasNext();
            }


            public @Override O next() throws NoSuchElementException {
                if (!it.hasNext()) {
                    if (!load_next_it() || !it.hasNext()) throw new NoSuchElementException();
                }

                return mapper.map(it.next());
            }


            /** Always throws {@code UnsupportedOperationException}. */
            public @Override void remove() throws UnsupportedOperationException {
                throw new UnsupportedOperationException();
            }


            public @Override Iterator<O> iterator() {
                return this;
            }
        }






    /** Greps through one or more {@code Iterable}s, returning the results in a list.
     *
     *  <P>This is potentially less efficient than {@link #grep(List, Grepper, Iterable...)} because the result list
     *  has to be allocated and grown.</P>
     *
     *  <P>In case you supply a handcrafted array in place of the {@code lists} vararg parameter, be advised that
     *  {@code null} pointers are forbidden.</P>
     *
     *  @param <E>          type of the list elements
     *  @param grepper      The grepper that is asked for each element if the grep condition is met.
     *  @param lists        input list(s) to grep from
     *  @return             A {@code List} containing the elements that made it through the {@code Grepper}.
     */
//    @SuppressWarnings("varargs")
    public static <E> List<E> grep(Grepper<? super E> grepper, Iterable<E>... lists) {
        List<E> dest = new ArrayList<E>();
        grep(dest, grepper, lists);
        return dest;
    }


    /** Greps from one or more {@code Iterator}s, returning the results in a list.
     *
     *  <P>Use this if you got hold of an {@code Iterator} without having access to a backing {@code Collection}
     *  (or whatever {@code Iterable}).</P>
     *
     *  <P>This is potentially less efficient than {@link #grep(List, Grepper, Iterator...)} because the result list
     *  has to be allocated and grown.</P>
     *
     *  <P>In case you supply a handcrafted array in place of the {@code iterators} vararg parameter, be advised that
     *  {@code null} pointers are forbidden.</P>
     *
     *  @param <E>          type of the elements the iterator returns
     *  @param grepper      The grepper that is asked for each element if the grep condition is met.
     *  @param iterators    input iterator(s) to grep from
     *  @return             A {@code List} containing the elements that made it through the {@code Grepper}.
     */
//    @SuppressWarnings("varargs")
    public static <E> List<E> grep(Grepper<? super E> grepper, Iterator<E>... iterators) {
        List<E> dest = new ArrayList<E>();
        grep(dest, grepper, iterators);
        return dest;
    }


    /** Greps through one or more {@code Iterable}s, writing the results to a destination list.
     *
     *  <P>This is potentially more efficient than {@link #grep(Grepper, Iterable...)} because the caller can supply
     *  a pre-allocated {@code List} with the expected necessary capacity. Use this, if you can estimate how many
     *  elements will make it through the {@code Grepper}, or if you need the result in a specific implementation
     *  of {@code List}.</P>
     *
     *  <P>In case you supply a handcrafted array in place of the {@code lists} vararg parameter, be advised that
     *  {@code null} pointers are forbidden.</P>
     *
     *  @param <E>          type of the list elements
     *  @param dest         destination list to which to add the results to
     *  @param grepper      The grepper that is asked for each element if the grep condition is met.
     *  @param lists        input list(s) to grep from
     *  @return             The destination list {@code dest}, for chaining.
     */
//    @SuppressWarnings("varargs")
    public static <E> List<? super E> grep(List<? super E> dest, Grepper<? super E> grepper, Iterable<E>... lists) {
        for (Iterable<E> l: lists)
            for (E e: l)
                if (grepper.grep(e)) dest.add(e);

        return dest;
    }


    /** Greps from one or more {@code Iterator}s, writing the results to a destination list.
     *
     *  Use this if you got hold of an {@code Iterator} without having access to a backing {@code Collection}
     *  (or whatever {@code Iterable}).
     *
     *  <P>This is potentially more efficient than {@link #grep(Grepper, Iterator...)} because the caller can supply
     *  a pre-allocated {@code List} with the expected necessary capacity. Use this, if you can estimate how many
     *  elements will make it through the {@code Grepper}, or if you need the result in a specific implementation
     *  of {@code List}.</P>
     *
     *  <P>In case you supply a handcrafted array in place of the {@code iterators} vararg parameter, be advised that
     *  {@code null} pointers are forbidden.</P>
     *
     *  @param <E>          type of the elements the iterator returns
     *  @param dest         destination list to which to add the results to
     *  @param grepper      The grepper that is asked for each element if the grep condition is met.
     *  @param iterators    input iterator(s) to grep from
     *  @return             The destination list {@code dest}, for chaining.
     */
//    @SuppressWarnings("varargs")
    public static <E> List<? super E> grep(List<? super E> dest, Grepper<? super E> grepper, Iterator<E>... iterators) {
        for (Iterator<E> i: iterators)
            while (i.hasNext()) {
                E e = i.next();
                if (grepper.grep(e)) dest.add(e);
            }

        return dest;
    }




    /** Greps through one or more {@code Iterable}s, returning one result element at a time.
     *
     *  Hands you a {@link LazyGrepIterator LazyGrepIterator} which returns the individual result elements.
     *  See there for details of behaviour.
     *
     *  <P>Elements from the input lists are only fetched when necessary to compute the result of a call to the
     *  returned iterator's {@code hasNext()} or {@code next()}. Also, an input list is not queried before the input
     *  before it is exhausted.</P>
     *
     *  <P>This is particularly useful if you have large amounts of material to grep through, or if it's in the
     *  input's nature to make up one element at a time and dumping them all at once prior to further processing
     *  would impose a peak level of stress on the generating unit (like the results of an SQL query).</P>
     *
     *  <P>In case you supply a handcrafted array in place of the {@code lists} vararg parameter, be advised that
     *  {@code null} pointers are forbidden.</P>
     *
     *  @param <E>          type of the list elements
     *  @param grepper      The grepper that is asked for each element if the grep condition is met.
     *  @param lists        input list(s) to grep from
     *  @return             A properly set up instance of {@link LazyGrepIterator}.
     */
//    @SuppressWarnings("varargs")
    public static <E> IterableIterator<E> grepLazy(Grepper<? super E> grepper, Iterable<E>... lists) {
        return new LazyGrepIterator<E>(grepper, lists);
    }


    /** Greps through one or more {@code Iterator}s, returning one result element at a time.
     *
     *  Hands you a {@link LazyGrepIterator LazyGrepIterator} which returns the individual result elements.
     *  See there for details of behaviour.
     *
     *  <P>Elements from the input iterators are only fetched when necessary to compute the result of a call to the
     *  returned iterator's {@code hasNext()} or {@code next()}. Also, an input list is not queried before the input
     *  before it is exhausted.</P>
     *
     *  <P>This is particularly useful if you have large amounts of material to grep through, or if it's in the
     *  input's nature to make up one element at a time and dumping them all at once prior to further processing
     *  would impose a peak level of stress on the generating unit (like the results of an SQL query).</P>
     *
     *  <P>In case you supply a handcrafted array in place of the {@code iterators} vararg parameter, be advised that
     *  {@code null} pointers are forbidden.</P>
     *
     *  @param <E>          type of the elements the iterator returns
     *  @param grepper      The grepper that is asked for each element if the grep condition is met.
     *  @param iterators    input iterator(s) to grep from
     *  @return             A properly set up instance of {@link LazyGrepIterator}.
     */
//    @SuppressWarnings("varargs")
    public static <E> IterableIterator<E> grepLazy(Grepper<? super E> grepper, Iterator<E>... iterators) {
        return new LazyGrepIterator<E>(grepper, iterators);
    }




    /** Counts how many items the grepper picks from the input elements.
     *
     *  This is logically equivalent to <BR>
     *  {@link #grep(Grepper, Iterable...)}{@link List#size() .size()},
     *  but doesn't catch the results, only counts them.
     *
     *  <P>Note: It is <STRONG>not</STRONG> recommended to use this solely to find out which capacity a result list
     *  should be allocated for, it's far too expensive for that.<P>
     *
     *  <P>In case you supply a handcrafted array in place of the {@code lists} vararg parameter, be advised that
     *  {@code null} pointers are forbidden.</P>
     *
     *  @param <E>          type of the list elements
     *  @param grepper      The grepper that is asked for each element if the grep condition is met.
     *  @param lists        input list(s) to grep from
     *  @return             how many elements made it through the grepper.
     */
//    @SuppressWarnings("varargs")
    public static <E> int grepCount(Grepper<? super E> grepper, Iterable<E>... lists) {
        int count = 0;

        for (Iterable<E> l: lists)
            for (E e: l)
                if (grepper.grep(e)) count++;

        return count;
    }


    /** Counts how many items the grepper picks from the input elements.
     *
     *  This is logically equivalent to <BR>
     *  {@link #grep(Grepper, Iterator...)}{@link List#size() .size()},
     *  but doesn't catch the results, only counts them.
     *
     *  <P>Note: It is <STRONG>not</STRONG> recommended to use this solely to find out which capacity a result list
     *  should be allocated for, it's far too expensive for that.</P>
     *
     *  <P>In case you supply a handcrafted array in place of the {@code iterators} vararg parameter, be advised that
     *  {@code null} pointers are forbidden.</P>
     *
     *  @param <E>          type of the elements the iterator returns
     *  @param grepper      The grepper that is asked for each element if the grep condition is met.
     *  @param iterators    input iterator(s) to grep from
     *  @return             how many elements made it through the grepper.
     */
//    @SuppressWarnings("varargs")
    public static <E> int grepCount(Grepper<? super E> grepper, Iterator<E>... iterators) {
        int count = 0;

        for (Iterator<E> i: iterators)
            while (i.hasNext()) {
                E e = i.next();
                if (grepper.grep(e)) count++;
            }

        return count;
    }




//    @SuppressWarnings("varargs")
    public static <I, O> List<O> map(Mapper<? super I, ? extends O> mapper, Iterable<I>... lists) {
        List<O> dest = new ArrayList<O>();
        map(dest, mapper, lists);
        return dest;
    }

//    @SuppressWarnings("varargs")
    public static <I, O> List<O> map(Mapper<? super I, ? extends O> mapper, Iterator<I>... iterators) {
        List<O> dest = new ArrayList<O>();
        map(dest, mapper, iterators);
        return dest;
    }




//    @SuppressWarnings("varargs")
    public static <I, O> List<? super O> map(List<? super O> dest, Mapper<? super I, ? extends O> mapper, Iterable<I>... lists) {
        for (Iterable<I> l: lists)
            for (I e: l)
                dest.add(mapper.map(e));

        return dest;
    }

//    @SuppressWarnings("varargs")
    public static <I, O> List<? super O> map(List<? super O> dest, Mapper<? super I, ? extends O> mapper, Iterator<I>... iterators) {
        for (Iterator<I> i: iterators)
            while (i.hasNext()) {
                I e = i.next();
                dest.add(mapper.map(e));
            }

        return dest;
    }




//    @SuppressWarnings("varargs")
    public static <I, O> IterableIterator<O> mapLazy(Mapper<? super I, ? extends O> mapper, Iterable<I>... lists) {
        return new LazyMapIterator<I, O>(mapper, lists);
    }

//    @SuppressWarnings("varargs")
    public static <I, O> IterableIterator<O> mapLazy(Mapper<? super I, ? extends O> mapper, Iterator<I>... iterators) {
        return new LazyMapIterator<I, O>(mapper, iterators);
    }


}

