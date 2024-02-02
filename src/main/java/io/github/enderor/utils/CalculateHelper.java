package io.github.enderor.utils;

public class CalculateHelper {
  public static double getAngle(final double dx, final double dy) {
    final double d = Math.sqrt(dx * dx + dy * dy);
    final double angle = Math.acos(dx / d) * 180D / Math.PI;
    return dy / d > 0 ? angle : 360D - angle;
  }
}
