package org.tbull.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;




/** Shuffles the bits of an integer number.
 *
 *  This is useful to obfuscate numbers without loss of information, so that the number can later be regained
 *  from the obfuscated version.
 *
 *  <P>Shuffles the bits of an integer number according to a defined shuffle sequence supplied by the user.
 *  In the shuffle process, each bit of the number is moved to a new position, resulting in a new number.
 *  Bits at a position larger than the shuffle sequence remain untouched.
 *  An unshuffle sequence is generated which inverts the effect of the shuffle process, so that the original
 *  number can be computed from the shuffled version.</P>
 *
 *  This is a quick-hack implementation.
 *
 *  @version    2008-09-09
 *
 *
 *  TODO:       This is largely untested.
 */

public class BitShuffler {

    protected int[] shuffle_sequence;
    protected int[] unshuffle_sequence;



    /** Constructs a {@code BitShuffler} with a 64 bit identity transformation. */
    public BitShuffler() {
        reset(64);
    }



    /** Constructs a {@code BitShuffler} with a given shuffle sequence.
     *
     *  @param  seq     See {@link #setShuffleSequence} for details.
     */
    public BitShuffler(int[] seq) {
        setShuffleSequence(seq);
    }




    protected boolean verify_sequence(int[] seq) {
        int i, len = seq.length;
        boolean[] present = new boolean[len];   // records which target bit numbers are present in the mapping

        for (i = 0; i < len; i++) present[i] = false;

        for (i = 0; i < len; i++) {
            if (seq[i] >= len) return false;    // target bit number must not exceed sequence length
            present[seq[i]] = true;             // mark bit number as present
        }

        for (i = 0; i < len; i++)
            if (!present[i]) return false;

        return true;
    }




    /** Sets the shuffle sequence.
     *
     *  <P>Sets the sequence used for shuffling the bits of a number. The elements of {@code seq}
     *  give the target positions of each of the respective bits of the shuffled number,
     *  while the element indices give the source positions. So bit {@code n} of a number to be shuffled
     *  will be placed at position {@code seq[i]} in the shuffled version.</P>
     *
     *  <P>Note that the binary representation of a number is usually written down from right to left
     *  (LSB is the rightmost, MSB the leftmost digit), while the array contents is written from
     *  left to right. Be aware of confusion!</P>
     *
     *  <P>The sequence must adhere to certain constraints, otherwise {@code IllegalArgumentException} is thrown.
     *
     *  </P>
     *
     *  @param  seq     The shuffle sequence specification.
     *  @throws IllegalArgumentException
     */

    public void setShuffleSequence(int[] seq) throws IllegalArgumentException {
        shuffle_sequence = new int[seq.length];

        if (!verify_sequence(seq)) throw new IllegalArgumentException();

        System.arraycopy(seq, 0, shuffle_sequence, 0, seq.length);
        // shuffle_sequence = java.util.Arrays.copyOf(seq, seq.length);
        create_unshuffle_sequence();
//        System.out.printf("shuffle_sequence: %s\n", java.util.Arrays.toString(shuffle_sequence));
    }


    /** Resets the shuffle sequence to an an identity transformation.
     *
     *  @param  length  Length of the new shuffle sequence.
     */

    public void reset(int length) {
        shuffle_sequence = new int[length];
        for (int i = 0; i < length; i++)
            shuffle_sequence[i] = i;

        create_unshuffle_sequence();
    }



    /** Generates a random sequence suitable for use with a {@code BitShuffler}. */
    public static int[] randomSequence(int length) {
        List<Integer> remaining = new LinkedList<Integer>();
        Random rnd = new Random();
        int seq[] = new int[length];
        int i;

        for (i = 0; i < length; i++) remaining.add(Integer.valueOf(i));

        for (i = 0; i < length; i++)
            seq[i] = remaining.remove(rnd.nextInt(remaining.size())).intValue();

        return seq;
    }



    /**
     *  Creates the unshuffle sequence from the shuffle sequence
     *  so that unshuffling is the exact inverse function of shuffling.
     */

    protected void create_unshuffle_sequence() {
        unshuffle_sequence = new int[shuffle_sequence.length];
        for (int i = 0; i < shuffle_sequence.length; i++) {
            unshuffle_sequence[shuffle_sequence[i]] = i;
        }
    }




    /**
     *  Returns the number of bits this {@code BitShuffler} works on.
     */
    public int length() {
        //return (byte) var_shuffle_sequence.size();
        return (byte) shuffle_sequence.length;
    }



/*  @TODO
 *
 *      - there will arise a problem if the seq is longer than the type to be shuffled,
 *          for instance if unshuffleInt is used with a 40-element sequence, some bits might be shifted outside the int range
 *  if conditions not met, throw IllegalArgumentException
 */

    /**
     *  Returns the shuffled version of number {@code int n}.
     *
     *  Shuffles that many bits (of the lower bits) of @c n as the size of the
     *  shuffle sequence is prepared for. Higher bits are considered insignificant and,
     *  thus, set to {@code 0} in the return value.
     *
     *  @param  n   The number to be shuffled.
     *  @return     The shuffled version of @c n.
     *
     *  TODO:   define [Verhalten] if shuffle sequence has more than 32 bits
     */

    public int shuffleInt(int n) {
        int result, bit;

        result = 0;
        for (int i = 0; i < shuffle_sequence.length && i < 32; i++) {
            /* take bit i and move it to its position according to shuffle_sequence */
            bit = ((n & (1 << i)) >>> i);
            result |= (bit << shuffle_sequence[i]);

            /* alternative:
                if ((n & (1 << i)) != 0)
                    result |= (1 << shuffle_sequence[i]);
            */
        }

        return result;
    }


    /**
     *  Returns the shuffled version of number {@code long n}.
     *
     *  Shuffles that many bits (of the lower bits) of @c n as the size of the
     *  shuffle sequence is prepared for. Higher bits are considered insignificant and,
     *  thus, set to {@code 0} in the return value.
     *
     *  @param  n   The number to be shuffled.
     *  @return     The shuffled version of @c n.
     *
     *  TODO:   define [Verhalten] if shuffle sequence has more than 32 bits
     */

    public long shuffleLong(long n) {
        long result, bit;

        result = 0;
        for (int i = 0; i < shuffle_sequence.length && i < 64; i++) {
            /* take bit i and move it to its position according to shuffle_sequence */
            bit = ((n & (1 << i)) >>> i);
            result |= (bit << shuffle_sequence[i]);
        }

        return result;
    }



    /** TODO
    public java.util.BitSet shuffleBitSet(BitSet bs) {
    }

    rename shuffle*() to shuffle(), the overloading mechanism will still tell them apart
    */


    /**
     *  Returns the unshuffled version of {@code int n}.
     *
     *  Unshuffles that many bits (of the lower bits) of @c n as the size of the
     *  shuffle sequence is prepared for. Higher bits are considered insignificant and,
     *  thus, set to {@code 0} in the return value.
     *
     *  @param  n   The number to be unshuffled.
     *  @return     The unshuffled version of @c n.
     */

    public int unshuffleInt(int n) {
        int result, bit;

        result = 0;
        for (int i = 0; i < unshuffle_sequence.length && i < 32; i++) {
            /* take bit i and move it to its position according to unshuffle_sequence */
            bit = ((n & (1 << i)) >>> i);
            result |= (bit << unshuffle_sequence[i]);
        }

        return result;
    }


    /**
     *  Returns the unshuffled version of {@code long n}.
     *
     *  Unshuffles that many bits (of the lower bits) of @c n as the size of the
     *  shuffle sequence is prepared for. Higher bits are considered insignificant and,
     *  thus, set to {@code 0} in the return value.
     *
     *  @param  n   The number to be unshuffled.
     *  @return     The unshuffled version of @c n.
     */

    public long unshuffleLong(long n) {
        long result, bit;

        result = 0;
        for (int i = 0; i < unshuffle_sequence.length && i < 64; i++) {
            /* take bit i and move it to its position according to unshuffle_sequence */
            bit = ((n & (1 << i)) >>> i);
            result |= (bit << unshuffle_sequence[i]);
        }

        return result;
    }





    public byte[] shuffle(byte[] n) {
        int len = n.length < shuffle_sequence.length ? shuffle_sequence.length : n.length;
        byte[] result = new byte[len];

        java.util.Arrays.fill(result, (byte) 0);

        // if len > shuffle_sequence.length, copy remaining bits

        return result;
    }



}

