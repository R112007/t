package crystal.type.weapons;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

/**
 * StageWeapon
 */
public class StageWeapon extends Weapon {
  public TextureRegion topIcon = null;
  public boolean active = false;
  public static final String topTex = "-tex";
  public int weappnStage;

  public StageWeapon(String name, boolean load) {
    this.name = name;
    if (load)
      loadTopTex();
  }

  public StageWeapon(boolean load) {
    this("", load);
  }

  public StageWeapon() {
    this(false);
  }

  public void loadTopTex() {
    topIcon = Core.atlas.find("crystal-" + name + topTex);
  }

  public void drawTopTex(Unit unit) {
    if (topIcon == null && active)
      return;
    Draw.rect(topIcon, x, y, unit.rotation);
  }

  public StageWeapon copy() {
    try {
      return (StageWeapon) clone();
    } catch (CloneNotSupportedException suck) {
      throw new RuntimeException("very good language design", suck);
    }
  }

  @Override
  public void addStats(UnitType u, Table t) {
    super.addStats(u, t);
  }
}
