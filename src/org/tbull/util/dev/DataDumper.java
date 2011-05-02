/*
 *
 *
 *
 *
 */


package org.tbull.util.dev;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;




/* @todo Differ between DataDumper and ObjectDumper,
    the former of which is for dumping data structures like collections,
    the latter of which is for dumping extensive info on fields, methods and more of an object.

   @todo build circular reference detection and have an idea how to display such thing

    @todo   build data-dump handler database which lets the user provide custom dump handlers (for classes that do not implement DataDumpable)
            implementation: probably like: interface CustomDataDumper { ILB dumpData(Object) throws WontDumpThatThingException }
                                           DataDumper.register(Class, CustomDataDumper)


public interface DataDumpHandler {
    public IndentableLineBuffer dumpData(Object o);
}
*/




/** Provides routines for dumping objects (for debugging).
 *
 *  <B>Warning:</B> You really should not use this. Not at this time. It's a relict from the very early days of my
 *  Java experience. However, the idea is not so bad, so I keep it here for putting it straight later.
 *
 *
 */

public class DataDumper {

    /*
     *  Why not have one line buffer per dump (as opposed to one per individual sub-dump)?
     *      A convention would have to be established as to the state of the indentation settings of the
     *      buffer (base + default line indent). Well, this would be feasible.
     *      Also, the sub-dump function would have to decide when it begins a new line. This would, for instance,
     *      make it impossible to implement different newline behaviour between Map keys and values, respectively.
     *
     *
     *
     *

     */



    public static String getPrettyClassName(Object o) {
        return getPrettyClassName(o.getClass());
    }

    public static String getPrettyClassName(Class<?> c) {
        Package p = c.getPackage();
        return p != null && p.getName().equals("java.lang") ? c.getSimpleName() : c.getName();
    }






    /** The internal root.
     *
     *  @return     never null
     */

    protected static IndentableLineBuffer dump_Object(Object o) {
        IndentableLineBuffer lb = new IndentableLineBuffer();

//System.err.println("<<dump_Object ...>>");



/*
        if (false) return null;
        else if (o instanceof Number) return dump_Number((Number) o);
        else if (o instanceof ArrayList) return dump_ArrayList((ArrayList) o);
        else if (o instanceof List) return dump_List((List) o);
        else return new IndentableLineBuffer(o.toString());
*/


        if (false) return null;
        else if (o == null) lb.append("null");
        else if (o.getClass().isArray())
        /* The object o cannot be of a primitive type (it wouldn't be an Object then).
            However, an array's component type can be of primitive type. In that case, we cannot cast
            o to Object[] because a primitive type can't be cast to Object. Hence we pass the raw object to
            dump_array() which then can care for the details. */
            lb.addLines(dump_array(o));
        else if (o.getClass().isEnum()) lb.addLines(dump_enum((Enum<?>) o));
        else if (o instanceof DataDumpable) lb.addLines(((DataDumpable) o).dumpData());
        else if (o instanceof Number) lb.addLines(dump_Number((Number) o));
        else if (o instanceof String) lb.addLine("\"" + (String) o + "\"");
        else if (o instanceof Map<?, ?>) lb.addLines(dump_Map((Map<?,?>) o));
        else if (o instanceof Set<?>) lb.addLines(dump_Set((Set<?>) o));
        else if (o instanceof List<?>) lb.addLines(dump_List((List<?>) o));
        else lb.addLine(getPrettyClassName(o)).append(" = ").append(o.toString());

        return lb;
    }


    protected static IndentableLineBuffer dump_enum(Enum<?> e) {
        return new IndentableLineBuffer("enum " + getPrettyClassName(e) + " = " + e.toString());
    }



    protected static IndentableLineBuffer dump_primitive_array(IndentableLineBuffer lb, Object a) {
        Class<?> cc;


        // retrieve the class of the array's elements
        cc = a.getClass().getComponentType();

        lb.addLine(cc.getName()).append("[");

        /* For some reason,
         *  lb.append(String.valueOf(a.getClass().getField("length").getInt(a)));
         *  doesn't work (throws NoSuchFieldException: length), so we have to do it the tedious way:
         */

        /* There are nine predefined Class objects to represent the eight primitive types and void. These are created
         *  by the JVM, and have the same names as the primitive types that they represent, namely boolean, byte, char,
         *  short, int, long, float, and double.
         *  These objects may only be accessed via the following public static final variables, and are the only Class
         *  objects for which this method returns true.
         *  Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE
         */

/* they use the expression  long[].class  in java.util */

        if (cc == Boolean.TYPE) {
            boolean[] za = (boolean[]) a;
            lb.append(String.valueOf(za.length)).append("] = ").append(Arrays.toString(za));
        } else if (cc == Byte.TYPE) {
            byte[] ba = (byte[]) a;
            lb.append(String.valueOf(ba.length)).append("] = ").append(Arrays.toString(ba));
        } else if (cc == Character.TYPE) {
            char[] ca = (char[]) a;
            lb.append(String.valueOf(ca.length)).append("] = ").append(Arrays.toString(ca));
        } else if (cc == Double.TYPE) {
            double[] da = (double[]) a;
            lb.append(String.valueOf(da.length)).append("] = ").append(Arrays.toString(da));
        } else if (cc == Float.TYPE) {
            float[] fa = (float[]) a;
            lb.append(String.valueOf(fa.length)).append("] = ").append(Arrays.toString(fa));
        } else if (cc == Integer.TYPE) {
            int[] ia = (int[]) a;
            lb.append(String.valueOf(ia.length)).append("] = ").append(Arrays.toString(ia));
        } else if (cc == Long.TYPE) {
            long[] la = (long[]) a;
            lb.append(String.valueOf(la.length)).append("] = ").append(Arrays.toString(la));
        } else if (cc == Short.TYPE) {
            short[] sa = (short[]) a;
            lb.append(String.valueOf(sa.length)).append("] = ").append(Arrays.toString(sa));
        } else {
            // glitch in the matrix
            assert(false);
        }

        return lb;
    }




    protected static IndentableLineBuffer dump_array(Object a) {
        IndentableLineBuffer lb = new IndentableLineBuffer();
        Class<?> cc;
        Object[] oa;


        // retrieve the class of the array's components
        cc = a.getClass().getComponentType();

        if (cc.isPrimitive())
            return dump_primitive_array(lb, a);
        else {

            oa = (Object[]) a;
            lb
                .append(getPrettyClassName(cc))
                .append("[").append(String.valueOf(oa.length)).append("]")
                .append(" = [");
            lb.indentCurrentLine(-1);

            for (Object o: oa) {
                lb.addLines(dump_Object(o));
                lb.append(",");
            }
            lb.addLine("]");
            lb.indentCurrentLine(-1);
            lb.indent(1);
        }

        return lb;
    }




    protected static IndentableLineBuffer dump_Number(Number n) {
        IndentableLineBuffer lb = new IndentableLineBuffer();

        lb.append(getPrettyClassName(n)).append(" = ").append(n.toString());

        return lb;
    }




    protected static IndentableLineBuffer dump_Set(Set<?> s) {
        IndentableLineBuffer lb = new IndentableLineBuffer();


        lb
            .append(getPrettyClassName(s))
            .append("(").append(String.valueOf(s.size())).append(")")
            .append(" = (");

        for (Object o: s) {
            lb.addLines(dump_Object(o).indent(1));
            lb.append(",");
        }
        lb.addLine(")");

        return lb;
    }




    protected static IndentableLineBuffer dump_List(List<?> l) {
        IndentableLineBuffer lb = new IndentableLineBuffer();


        lb
            .append(getPrettyClassName(l))
            .append("(").append(String.valueOf(l.size())).append(")")
            .append(" = [");

        for (Object o: l) {
            lb.addLines(dump_Object(o).indent(1));
            lb.append(",");
        }
        lb.addLine("]");

        return lb;
    }



        /** if the key is an enum, only show it's name (and maybe unqualified classname), not the full objectdump  */
        protected static IndentableLineBuffer dump_map_key(Object key) {
            Class<? extends Object> c;
            if ((c = key.getClass()).isEnum()) {
                return new IndentableLineBuffer(c.getSimpleName() + "." + key.toString());
            } else
                return dump_Object(key);        // takes proper care of Strings
        }


    protected static IndentableLineBuffer dump_Map(Map<?,?> m) {
        IndentableLineBuffer lb = new IndentableLineBuffer();

        lb
            .append(getPrettyClassName(m))
            .append("(").append(String.valueOf(m.size())).append(")")
            .append(" = {");

        for (Object key: m.keySet()) {
            lb.addLines(dump_map_key(key).indent(1));
            lb.append(" => ");
            lb.appendLines(dump_Object(m.get(key)).indent(1));
            lb.append(",");
        }

        lb.addLine("}");

        return lb;
    }




    public static String dump(Object... objects) {
        IndentableLineBuffer lb = null;
        /*
        IndentableLineBuffer lb = dump_Object(o);
        System.err.println(dump_Object(lb).toString());
        return lb.toString();
        */

        for (Object o: objects)
            if (lb != null) lb.addLines(dump_Object(o));
            else lb = dump_Object(o);   // never returns null

        // TODO: what will we return if no args supplied?
        return lb == null ? null : lb.toString();
    }





}
