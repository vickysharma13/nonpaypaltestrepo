package com.paypal.sea.s2dbservices;

import java.io.*;

public final class StackTraceUtil {

      public static String getStackTrace(Exception ex) {
        final Writer errors = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(errors);
        ex.printStackTrace(printWriter);
        return errors.toString();
      }
}
