package crystal.game;

import crystal.type.UnitStack;

public class CEventType {
  public static class CreateNewUnitInfo {
  }

  public static class LaunchUnitEvent {
    public final UnitStack stack;

    public LaunchUnitEvent(UnitStack stack) {
      this.stack = stack;
    }
  }
}
