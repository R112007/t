package crystal.world.blocks.crystal;

import arc.struct.Seq;
import arc.util.Log;
import crystal.world.blocks.crystal.CrystalDrill.CrystalDrillBuild;
import mindustry.gen.Building;
import mindustry.world.Block;

public class CrystalSource extends Block {
  public CrystalSource(String name) {
    super(name);
    update = true;
    solid = true;
  }

  public class CrystalSourceBuild extends Building {
    public Seq<Building> crys = new Seq<>();
    public boolean shouldUpdate = true;

    @Override
    public void updateTile() {
      if (shouldUpdate) {
        updateProximity();
        Log.info("临近建筑");
        Log.info(proximity);
        for (Building build : this.proximity) {
          if (CrystalProducer.clazzes.contains(build.getClass())) {
            crys.add(build);
          }
          Log.info(build + " " + build.pos() + " " + build.getClass());
          Log.info("clazzes" + CrystalProducer.clazzes);
        }
        Log.info("筛选建筑");
        Log.info(crys);
        for (var entity : crys) {
          if (entity instanceof CrystalDrillBuild drill) {
            ((CrystalDrill) drill.block).crycons.tick = 100000f;
          }
        }
        shouldUpdate = false;
      }
    }

    @Override
    public void onProximityUpdate() {
      super.onProximityUpdate();
      shouldUpdate = true;
    }
  }
}
