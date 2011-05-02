



import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tbull.util.dev.DataDumpable;
import org.tbull.util.dev.DataDumper;
import org.tbull.util.dev.IndentableLineBuffer;



public class t_DD_0 {

    static class SelfDumper implements DataDumpable {
        int x, y, z;

        public SelfDumper(int x, int y, int z) {
            this.x = x; this.y = y; this.z = z;
        }

        public IndentableLineBuffer dumpData() {
            return new IndentableLineBuffer(
                this.getClass().toString() + ": (x/y/z) = (" + String.valueOf(x) + "/" + y + "/" + z + ")");
        }
    }


    enum Foonum { ALPHA, BETA, GAMMA }


    public static void main(String[] argv) {
        int[] ia;
        double[] da;
        java.awt.Choice[] jaca;
        Enum<?> e;
        Map<Object, Object> m;
        EnumMap<Foonum, Object> em;


        List<Object> l = new ArrayList<Object>();

        l.add(new Object());
        l.add(ia = new int[4]);
            for (int i=1; i<=ia.length; i++) ia[i-1] = (int) Math.pow(1024, i);
        l.add(da = new double[5]);
            for (int i=1; i<=da.length; i++) da[i-1] = Math.pow(1024, i);

        l.add(new SelfDumper(3, 5, 9));
        l.add(new Integer(-13));

        jaca = new java.awt.Choice[3];
        for (int i=0; i<jaca.length; i++) jaca[i] = new java.awt.Choice();
        l.add(jaca);

        l.add("Just a string");
        l.add(new StringBuilder("A string builder"));
        l.add(Foonum.BETA);
        e = Foonum.GAMMA; l.add(e);

        m = new HashMap<Object, Object>();
        m.put("Huhu", Collections.EMPTY_SET);
        m.put(e, Collections.EMPTY_LIST);
        m.put(new Object(), Collections.EMPTY_MAP);
        l.add(m);

        em = new EnumMap<Foonum, Object>(Foonum.class);
        em.put(Foonum.ALPHA, "String");
        em.put(Foonum.BETA, m);
        em.put(Foonum.GAMMA, jaca);
        l.add(em);

        System.out.println(DataDumper.dump(l));

    }

}
