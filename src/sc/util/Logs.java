package sc.util;

import arc.struct.Seq;
import arc.util.Log;

/**
 * Log
 */
public class Logs {
  public static void printSeq(Seq seq) {
    for (var value : seq) {
      Log.info(value);
    }
  }
}
