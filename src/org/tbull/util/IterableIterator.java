package org.tbull.util;

import java.util.Iterator;


/** An {@link Iterator}, which can itself be the subject of an enhanced {@code for} statement.
 *
 *  <P>This comes in handy if you want to supply more than one iterator for a class. Since only one
 *  {@code Iterator} can be retrieved by the {@link Iterable#iterator() iterator()} method, other methods have to
 *  be provided for the other iterators. But then, those iterators cannot be used with an enhanced {@code for}
 *  statement, because that statement relies on invocation of the {@code iterator()} method. Therefore, those
 *  iterators have to be {@link Iterable} themselves.</P>
 *
 *  <P>This interface does not introduce new methods. It just unifies {@link Iterator} and {@link Iterable}.<P>
 *
 *  <P>Normally, your custom {@code IterableIterator} would just return itself in its additional {@code iterator()}
 *  method, like this:</P>
 *
 *  <PRE>
 *      class CustomList&lt;E&gt; implements List&lt;E&gt; {
 *          private List&lt;E&gt; backend = new ArrayList&lt;E&gt;();
 *
 *          private class CustomIterator&lt;E&gt; <B>implements IterableIterator&lt;E&gt;</B> {
 *              /*
 *               *  implement hasNext(), next() and remove() in your custom fashion
 *               *&#47;
 *
 *              <B>/* make this Iterator iterable *&#47;
 *              public Iterator&lt;E&gt; iterator() {
 *                  return this;            // just return ourselves
 *              }</B>
 *          }
 *
 *          ...
 *
 *          /* this returns the normal iterator *&#47;
 *          public Iterator&lt;E&gt; iterator() {
 *              return backend.iterator();  // use ArrayList's iterator
 *          }
 *
 *          /* this returns our custom iterator *&#47;
 *          public <B>IterableIterator&lt;E&gt;</B> custom() {
 *              return new CustomIterator&lt;E&gt;();
 *          }
 *      }
 *
 *
 *      CustomList&lt;Foo&gt; foos = new CustomList&lt;Foo&gt;();
 *      ... add elements ...
 *
 *      // iterate over all elements:
 *      for (Foo foo: foos) { ... }
 *      // iterate over custom elements:
 *      for (Foo foo: foos.custom()) { ... }
 *  </PRE>
 *
 *  <P>Ceterum censeo HTML in Javadoc is the dumbest idea ever.</P>
 */

public interface IterableIterator<E> extends Iterator<E>, Iterable<E> {

}
