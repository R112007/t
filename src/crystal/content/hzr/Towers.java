package crystal.content.hzr;

import crystal.world.blocks.defence.towers.PowerAttackTower;
import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class Towers {
  public static Block powerTower1;

  public static void load() {
    powerTower1 = new PowerAttackTower("powerTower1") {
      {

        size = 2;
        requirements(Category.effect, ItemStack.with(new Object[] { Items.copper, 1 }));
        alwaysUnlocked = true;
        range = 80;
        damage = 40;
        consumePower(2f);
        coolant = consumeCoolant(0.1f);
      }
    };
  }
}
