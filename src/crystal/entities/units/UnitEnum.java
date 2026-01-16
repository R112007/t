package crystal.entities.units;

public class UnitEnum {
  public enum Stage {
    stage1, stage2, stage3, stage4;

    public static final Stage[] all = values();

    public boolean lowerThan(Stage target) {
      return this.ordinal() < target.ordinal();
    }
  }

  public enum Mode {
    easy, normal, hard, extreme
  }
}
