You have to use HTML for the overview file. You cannot use javadoc comment style like in package-info.java.

You even MUST use explicit BODY tags. Yet you can't actually make use of HTML. For example, there's no way to set the title from within this file. Anything before the body start tag is ignored.

This is so braindead...


<BODY>

    Utilities for the little tedious everyday tasks.

    <P>For updates, bug reports or whatever, see <A HREF="http://tbull.org/projects/util4j/">the util4j home</A>.</P>

    <P STYLE="font-variant:small-caps">Ceterum censeo HTML in Javadoc is the dumbest idea ever.</P>

    @author     Roland Bluethgen a.k.a. T-Bull
    @version    0.0.1   <!-- TODO: automate this -->

</BODY>

