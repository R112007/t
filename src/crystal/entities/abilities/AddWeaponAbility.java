package crystal.entities.abilities;

import arc.Core;
import arc.Events;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import mindustry.audio.SoundLoop;
import mindustry.content.UnitTypes;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.WeaponMount;
import mindustry.game.EventType.Trigger;
import mindustry.game.EventType.UnitDestroyEvent;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import mindustry.type.UnitType;

public class AddWeaponAbility extends Ability {
  public float range, reload, duration;
  public Weapon weapon;
  public int amount;// 一次施加的单位数量
  public boolean limited, // true的话应用amount
      permanent = false;// 是否永远添加武器
  public float timer;
  public UnitType unitType;
  private static boolean isDeathListenerRegistered = false;
  public static ObjectMap<String, WeaponEntry> map = new ObjectMap<>();
  public static ObjectMap<Integer, String> keyMap = new ObjectMap<>();
  static {
    if (!isDeathListenerRegistered) {
      Events.on(UnitDestroyEvent.class, event -> {
        if (keyMap.containsKey(event.unit.id)) {
          map.remove(keyMap.get(event.unit.id));
        }
      });
    }
  }

  public AddWeaponAbility(Weapon weapon, float duration, float range, float reload, int amount, boolean permanent) {
    this.weapon = weapon;
    this.duration = duration;
    this.range = range;
    this.reload = reload;
    this.amount = amount;
    this.permanent = permanent;
    this.limited = true;
    this.unitType = new UnitType("weapon.name") {
      {
        this.constructor = UnitTypes.alpha.constructor;
        this.weapons.add(weapon);
      }
    };
  }

  public AddWeaponAbility(Weapon weapon, float duration, float range, float reload, boolean permanent) {
    this.weapon = weapon;
    this.duration = duration;
    this.range = range;
    this.reload = reload;
    this.permanent = permanent;
    this.limited = false;
    this.unitType = new UnitType("weapon.name") {
      {
        this.constructor = UnitTypes.alpha.constructor;
        this.weapons.add(weapon);
      }
    };
  }

  @Override
  public String localized() {
    return Core.bundle.get("ability.addweaponability");
  }

  @Override
  public void addStats(Table t) {
    super.addStats(t);
    weapon.addStats(unitType, t);
  }

  @Override
  public void update(Unit unit) {
    timer += Time.delta;
    if (timer >= reload) {
      timer = 0;
      float effectiveRange = this.range;
      Units.nearby(unit.team, unit.x, unit.y, effectiveRange, targetUnit -> {
        if (targetUnit == null || targetUnit.dead || !targetUnit.isAdded())
          return;
        if (this.weapon == null || this.weapon.name.isEmpty()) {
          Log.err("AddWeaponAbility.update: Weapon未初始化或名称为空");
          return;
        }
        if (permanent) {
          if (!haveWeapon(targetUnit, this.weapon))
            addWeapon(targetUnit);
        } else {
          if (!haveWeapon(targetUnit, this.weapon))
            new WeaponEntry(weapon, duration, targetUnit.id);
        }
      });
    }
  }

  public boolean haveWeapon(Unit unit, Weapon weapon) {
    if (weapon == null || unit == null || unit.dead() || unit.isAdded()) {
      return false;
    }
    if (map.containsKey(weaponKey(weapon, unit.id))) {
      return true;
    } else {
      return false;
    }
  }

  public static String weaponKey(Weapon weapon, int id) {
    try {
      return weapon.name + weapon.x + weapon.y + getUnitByID(id).id;
    } catch (Exception e) {
      Log.err("id为" + id + "的单位为null", e);
      throw new NullPointerException();
    }
  }

  public static Unit getUnitByID(int id) {
    return Groups.unit.getByID(id);
  }

  @Override
  public void death(Unit unit) {
    super.death(unit);
  }

  public void addWeapon(Unit unit) {
    if (unit == null || unit.dead || !unit.isAdded()) {
      Log.err("AddWeaponAbility.addWeapon: 无效Unit[ID:" + (unit != null ? unit.id() : "null") + "]");
      return;
    }
    if (weapon == null || weapon.name.isEmpty()) {
      Log.err("AddWeaponAbility.addWeapon: Weapon未初始化或名称为空");
      return;
    }
    WeaponMount newWeaponMount = weapon.mountType.get(weapon);
    newWeaponMount.reload = 5f;
    if (weapon.recoils > 0) {
      newWeaponMount.recoils = new float[weapon.recoils];
    }
    newWeaponMount.weapon.init();
    Seq<WeaponMount> newMountsSeq = new Seq<>(unit.mounts());
    int originalSize = newMountsSeq.size;
    newMountsSeq.add(newWeaponMount);

    if (weapon.mirror) {
      Weapon mirroredWeapon = weapon.copy();
      mirroredWeapon.flip();
      mirroredWeapon.init();
      WeaponMount mirroredMount = mirroredWeapon.mountType.get(mirroredWeapon);
      mirroredMount.reload = 5f;
      if (mirroredWeapon.recoils > 0) {
        mirroredMount.recoils = new float[mirroredWeapon.recoils];
      }
      weapon.otherSide = originalSize + 1;
      mirroredWeapon.otherSide = originalSize;
      newMountsSeq.add(mirroredMount);
    }
    WeaponMount[] finalMounts = newMountsSeq.toArray(WeaponMount.class);
    Time.runTask(1f, () -> {
      if (unit != null && !unit.dead && unit.isAdded()) {
        unit.mounts(finalMounts);
      }
    });
  }

  public class WeaponEntry {
    public float duration;
    public Weapon weapon;
    public int id;
    public SoundLoop shootSoundLoop;
    public ObjectMap<String, WeaponMount> weaponmap = new ObjectMap<>();

    public WeaponEntry(Weapon weapon, float duration, int id) {
      this.weapon = weapon;
      this.duration = duration;
      this.id = id;
      map.put(weaponKey(this.weapon, this.id), this);
      keyMap.put(this.id, weaponKey(this.weapon, this.id));
      for (var w : getUnitByID(id).mounts) {
        weaponmap.put(weaponKey(w.weapon, this.id), w);
      }
      addWeapon();
      Events.run(Trigger.update, () -> {
        this.duration -= Time.delta;
        if (this.duration <= 0f) {
          if (shootSoundLoop != null) {
            shootSoundLoop.stop();
            shootSoundLoop = null;
          }
          try {
            removeWeapon();
            map.remove(weaponKey(this.weapon, this.id));
            keyMap.remove(this.id);
          } catch (Exception e) {
            Log.err("干啥东西失败了", e);
          }
        }
      });
    }

    public void addWeapon() {
      Unit unit = getUnitByID(this.id);
      if (unit == null || unit.dead || !unit.isAdded()) {
        Log.err("AddWeaponAbility.addWeapon: 无效Unit[ID:" + (unit != null ? unit.id() : "null") + "]");
        return;
      }
      if (weapon == null || weapon.name.isEmpty()) {
        Log.err("AddWeaponAbility.addWeapon: Weapon未初始化或名称为空");
        return;
      }
      WeaponMount newWeaponMount = weapon.mountType.get(weapon);
      weaponmap.put(weaponKey(newWeaponMount.weapon, id), newWeaponMount);
      newWeaponMount.reload = 5f;
      if (weapon.recoils > 0) {
        newWeaponMount.recoils = new float[weapon.recoils];
      }
      newWeaponMount.weapon.init();
      Seq<WeaponMount> newMountsSeq = new Seq<>(unit.mounts());
      int originalSize = newMountsSeq.size;
      newMountsSeq.add(newWeaponMount);

      if (weapon.mirror) {
        Weapon mirroredWeapon = weapon.copy();
        mirroredWeapon.flip();
        mirroredWeapon.init();
        WeaponMount mirroredMount = mirroredWeapon.mountType.get(mirroredWeapon);
        mirroredMount.reload = 5f;
        if (mirroredWeapon.recoils > 0) {
          mirroredMount.recoils = new float[mirroredWeapon.recoils];
        }
        weapon.otherSide = originalSize + 1;
        mirroredWeapon.otherSide = originalSize;
        newMountsSeq.add(mirroredMount);
        weaponmap.put(weaponKey(newWeaponMount.weapon, id) + "mirror", mirroredMount);
      }
      WeaponMount[] finalMounts = newMountsSeq.toArray(WeaponMount.class);
      Time.runTask(1f, () -> {
        if (unit != null && !unit.dead && unit.isAdded()) {
          unit.mounts(finalMounts);
        }
      });
    }

    public void removeWeapon() {
      Unit unit = getUnitByID(id);
      if (unit == null || unit.dead || !unit.isAdded())
        return;
      Seq<WeaponMount> orgin = new Seq<>(unit.mounts);
      if (weaponmap.get(weaponKey(weapon, id)).weapon.mirror == true)
        orgin.remove(weaponmap.get(weaponKey(weapon, id) + "mirror"));
      orgin.remove(weaponmap.get(weaponKey(weapon, id)));
      unit.mounts(orgin.toArray(WeaponMount.class));
    }

    public void set(float duration) {
      this.duration = Math.max(this.duration, duration);
    }
  }
}
