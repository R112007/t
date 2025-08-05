package crystal.world.blocks.payloads;

import arc.scene.ui.layout.Table;
import crystal.game.UnitInfo;
import crystal.game.UnitInfo.ExportStat;
import mindustry.content.SectorPresets;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.Block;

public class A extends Block {
  public static UnitType u;
  public static ExportStat e;

  public A(String name) {
    super(name);
    e = new ExportStat();
  }

  public class ABuild extends Building {
    int i = 0;

    @Override
    public void buildConfiguration(Table table) {
      table.button(Icon.upOpen, Styles.clearNoneTogglei, () -> {
        i++;
        e.amount = i;
        UnitInfo.get(SectorPresets.groundZero.sector).possessed.put(u, e);
        UnitInfo.get(SectorPresets.groundZero.sector).possessed.put(u, e);
      });
    }
  }
}
