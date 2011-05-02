package org.tbull.util.dev;


public class TunnelException extends RuntimeException {

    private static final long serialVersionUID = -5984164929554243551L;


/*
 *
 * http://www.mindview.net/Etc/Discussions/CheckedExceptions

import java.io.*;
class ExceptionAdapter extends RuntimeException {
  private final String stackTrace;
  public Exception originalException;
  public ExceptionAdapter(Exception e) {
    super(e.toString());
    originalException = e;
    StringWriter sw = new StringWriter();
    e.printStackTrace(new PrintWriter(sw));
    stackTrace = sw.toString();
  }
  public void printStackTrace() {
    printStackTrace(System.err);
  }
  public void printStackTrace(java.io.PrintStream s) {
    synchronized(s) {
      s.print(getClass().getName() + ": ");
      s.print(stackTrace);
    }
  }
  public void printStackTrace(java.io.PrintWriter s) {
    synchronized(s) {
      s.print(getClass().getName() + ": ");
      s.print(stackTrace);
    }
  }
  public void rethrow() { throw originalException; }
}




 */
}
