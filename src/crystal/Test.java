package crystal;

import crystal.world.blocks.liquid.LiquidRangeBridge;
import crystal.world.blocks.payloads.A;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

public class Test {
  public static Block t;
  public static Block a1;
  public static Block a2;
  public static Block a3;

  public static void load() {
    t = new LiquidRangeBridge("t") {
      {
        range = 35;
        liquidCapacity = 30;
        health = 300;
        hasPower = true;
        consumePower(2);
        size = 3;
        buildVisibility = BuildVisibility.shown;
        category = Category.liquid;
        requirements = ItemStack.with(Items.copper, 2);
        alwaysUnlocked = true;
      }
    };
    a1 = new A("a1") {
      {
        u = UnitTypes.dagger;
        buildVisibility = BuildVisibility.shown;
        requirements = ItemStack.with(Items.copper, 2);
        category = Category.liquid;
        alwaysUnlocked = true;
      }
    };
    a2 = new A("a2") {
      {
        u = UnitTypes.mace;
        buildVisibility = BuildVisibility.shown;
        requirements = ItemStack.with(Items.copper, 2);
        category = Category.liquid;
        alwaysUnlocked = true;
      }
    };
    a3 = new A("a3") {
      {
        u = UnitTypes.fortress;
        requirements = ItemStack.with(Items.copper, 2);
        buildVisibility = BuildVisibility.shown;
        alwaysUnlocked = true;
        category = Category.liquid;
      }
    };
  }
}
