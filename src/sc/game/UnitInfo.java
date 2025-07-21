package sc.game;

import arc.math.WindowedMean;
import arc.struct.ObjectMap;
import mindustry.type.Sector;
import mindustry.type.UnitType;

public class UnitInfo {
  private static final int valueWindow = 60;
  public final Sector sector;
  public final int id;
  public static final UnitInfo[] all = new UnitInfo[10000];
  public ObjectMap<UnitType, ExportStat> export = new ObjectMap<>();
  public ObjectMap<UnitType, ExportStat> total = new ObjectMap<>();

  public UnitInfo(Sector sector) {
    this.sector = sector;
    this.id = sector.id;
    all[id] = this;
  }

  public static UnitInfo get(int id) {
    return all[((byte) id) & 0xff];
  }

  public static class ExportStat {
    public transient float counter;
    public transient WindowedMean means = new WindowedMean(valueWindow);
    public transient boolean loaded;

    public float mean;

    public String toString() {
      return mean + "";
    }
  }
}
