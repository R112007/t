package crystal;

import arc.graphics.Color;
import crystal.entities.abilities.AddWeaponAbility;
import crystal.entities.bullet.GravityBullet;
import crystal.world.blocks.crystal.CrystalDrill;
import crystal.world.blocks.crystal.CrystalSource;
import crystal.world.blocks.defence.LinkWall;
import crystal.world.blocks.environment.DamageFloor;
import crystal.world.blocks.liquid.LiquidRangeBridge;
import crystal.world.blocks.payloads.A;
import crystal.world.blocks.payloads.UnitLanuchPad;
import crystal.world.blocks.payloads.UnitReceivePad;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.gen.Sounds;
import mindustry.gen.UnitEntity;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.meta.BuildVisibility;

public class Test {
  public static Block t;
  public static Block t1;
  public static Block t2;
  public static Block a1;
  public static Block a2;
  public static Block a3;
  public static Block a4;
  public static Block a5;
  public static Block c1;
  public static Block d1;
  public static Block f1;
  public static Block tu1;
  public static UnitType a6;

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
        size = 3;
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
        size = 3;
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
        size = 3;
      }
    };
    a4 = new UnitLanuchPad("a4") {
      {
        size = 3;
        this.requirements(Category.units, ItemStack.with(new Object[] { Items.copper, 1 }));
        this.alwaysUnlocked = true;
      }
    };
    a5 = new UnitReceivePad("a5") {
      {
        size = 3;
        this.requirements(Category.units, ItemStack.with(new Object[] { Items.copper, 1 }));
        this.alwaysUnlocked = true;
      }
    };
    a6 = new UnitType("a6") {
      {
        speed = 0.5f;
        hitSize = 8f;
        health = 150;
        constructor = UnitEntity::create;
        this.abilities.add(new AddWeaponAbility(UnitTypes.dagger.weapons.get(0), 300, 80, 500, false));
      }
    };
    t1 = new LinkWall("t1") {
      {
        size = 1;
        this.requirements(Category.units, ItemStack.with(new Object[] { Items.copper, 1 }));
        this.alwaysUnlocked = true;
      }
    };
    t2 = new LinkWall("t2") {
      {
        size = 2;
        this.requirements(Category.units, ItemStack.with(new Object[] { Items.copper, 1 }));
        this.alwaysUnlocked = true;
      }
    };
    c1 = new CrystalSource("c1") {
      {
        size = 1;
        this.requirements(Category.production, ItemStack.with(new Object[] { Items.copper, 1 }));
        this.alwaysUnlocked = true;
      }
    };
    d1 = new CrystalDrill("d1") {
      {
        this.consumeCrystal = 1f;
        this.crystalCapacity = 60f;
        size = 2;
        this.requirements(Category.production, ItemStack.with(new Object[] { Items.copper, 1 }));
        this.alwaysUnlocked = true;
      }
    };
    f1 = new DamageFloor("f1") {
      {
        id = 0;
        albedo = 0.9f;
        damage = 0.2f;
      }
    };
    tu1 = new PowerTurret("tu1") {
      {
        requirements(Category.turret, ItemStack.with(Items.copper, 60));
        range = 165f;

        shoot.firstShotDelay = 40f;

        recoil = 2f;
        reload = 70f;
        shake = 2f;
        shootEffect = Fx.lancerLaserShoot;
        smokeEffect = Fx.none;
        heatColor = Color.red;
        size = 2;
        scaledHealth = 280;
        moveWhileCharging = false;
        accurateDelay = false;
        shootSound = Sounds.laser;
        coolant = consumeCoolant(0.2f);

        consumePower(6f);
        shootType = new GravityBullet(3, 5) {
          {
            pierce = true;
            foeceRange = 300;
            foece = 5;
            lifetime = 600;
          }
        };
      }
    };
  }
}
