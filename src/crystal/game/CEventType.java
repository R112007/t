package crystal.game;

import crystal.type.UnitStack;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.maps.Map;
import mindustry.type.Sector;
import mindustry.type.UnitType;

public class CEventType {
  public static class CreateNewUnitInfo {
  }

  public static class LaunchUnitEvent {
    public final UnitStack stack;

    public LaunchUnitEvent(UnitStack stack) {
      this.stack = stack;
    }
  }

  public static class SummonUnitEvent {
    public UnitType type;
    public float x, y, delay;
    public Effect effect;
    public Team team;

    public SummonUnitEvent(UnitType type, float x, float y, Effect effect, Team team, float delay) {
      this.type = type;
      this.x = x;
      this.y = y;
      this.effect = effect;
      this.team = team;
      this.delay = delay;
    }
  }

  public static class SectorChangeEvent {
    public Sector sector;

    public SectorChangeEvent(Sector sector) {
      this.sector = sector;
    }
  }

  public static class MapChangeEvent {
    public Map map;

    public MapChangeEvent(Map map) {
      this.map = map;
    }
  }
}
