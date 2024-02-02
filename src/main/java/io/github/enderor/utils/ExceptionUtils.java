package io.github.enderor.utils;

import org.jetbrains.annotations.NotNull;

public class ExceptionUtils {
  private static void print(char[] messages) {
    System.err.print(messages);
  }
  
  public static void print(@NotNull Throwable exception) {
    print(exception.fillInStackTrace().toString().toCharArray());
  }
}
