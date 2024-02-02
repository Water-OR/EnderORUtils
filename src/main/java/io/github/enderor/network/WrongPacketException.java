package io.github.enderor.network;

import java.io.IOException;

public class WrongPacketException extends IOException {
  public WrongPacketException(Throwable cause, String message, Object... objects) {
    super(String.format(message, objects), cause);
  }
  
  public WrongPacketException(String message, Object... objects) {
    super(String.format(message, objects));
  }
}
