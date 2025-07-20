package sc;

import mindustry.content.Blocks;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;
import sc.world.blocks.payloads.UnitLanuchPad;

/**
 * Test
 */
public class Test {
  public static Block a;

  public static void load() {
    Blocks.itemSource.alwaysUnlocked = true;
    Blocks.itemSource.buildVisibility = BuildVisibility.shown;
    Blocks.payloadSource.alwaysUnlocked = true;
    Blocks.payloadSource.buildVisibility = BuildVisibility.shown;

    a = new UnitLanuchPad("a") {
      {
        size = 3;
        this.requirements(Category.units, ItemStack.with(new Object[] {}));
        this.alwaysUnlocked = true;
      }
    };
  }
}
