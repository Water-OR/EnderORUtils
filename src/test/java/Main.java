import java.io.PrintStream;
import java.util.Scanner;

public class Main {
  public Main(String[] args) {
    final Scanner     input  = new Scanner(System.in);
    final PrintStream output = System.out;
    output.print(getAngle(input.nextDouble(), input.nextDouble()));
  }
  
  public static void main(String[] args) { new Main(args); }
  
  public static double getAngle(final double dx, final double dy) {
    final double d     = Math.sqrt(dx * dx + dy * dy);
    final double angle = Math.acos(dx / d) * 180D / Math.PI;
    return dy / d < 0 ? 360D - angle : angle;
  }
}
