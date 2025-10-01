package crystal.world.blocks.environment;

import arc.struct.Seq;
import arc.util.Log;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

public class DamageFloor extends Floor {
  public float damage;

  public DamageFloor(String name) {
    super(name);
  }

  public static void update() {
    for (Building b : Groups.build) {
      Seq<Tile> tempTiles = new Seq<>();
      b.tile.getLinkedTilesAs(b.block, tempTiles);
      for (var f : tempTiles) {
        if (f.floor() instanceof DamageFloor) {
          DamageFloor d = (DamageFloor) f.floor();
          f.build.damage(d.damage);
          // f.build.health -= (d.damage);
          Log.info("块" + f.build);
          Log.info("似没似" + f.build.dead);
        }
      }
    }
  }
}
