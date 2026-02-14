package crystal.world.blocks.defence.towers;

import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Healthc;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.consumers.ConsumeItemFilter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

public class ItemAttackTower extends Tower {
  public ObjectSet<Item> ammoItems = new ObjectSet<>();
  public int ammoPerShot = 1;
  public int maxAmmo = 30; // 最大弹药容量（总物品数）

  public ItemAttackTower(String name) {
    super(name);
    hasItems = true;
  }

  public void addAmmo(Item... items) {
    ammoItems.addAll(items);
  }

  @Override
  public void init() {
    super.init();
    consume(new ConsumeItemFilter(item -> ammoItems.contains(item)) {
      @Override
      public float efficiency(Building build) {
        ItemAttackTowerBuild tb = (ItemAttackTowerBuild) build;
        // 弹药充足则效率为1，否则为0
        return tb.totalAmmo >= ammoPerShot || tb.cheating() ? 1f : 0f;
      }
    });
  }

  @Override
  public boolean outputsItems() {
    return false;
  }

  @Override
  public void setStats() {
    super.setStats();
    stats.remove(Stat.itemCapacity);
    stats.add(Stat.ammoCapacity, maxAmmo, StatUnit.items);
    stats.add(Stat.ammoUse, ammoPerShot, StatUnit.perShot);
    stats.add(Stat.ammo, table -> {
      for (var i : ammoItems) {
        table.add(StatValues.displayItem(i));
      }
    });
  }

  @Override
  public void setBars() {
    super.setBars();

    addBar("ammo", (ItemAttackTowerBuild entity) -> new Bar(
        "stat.ammo",
        Pal.ammo,
        () -> (float) entity.totalAmmo / maxAmmo));
  }

  public class ItemAttackTowerBuild extends TowerBuild {
    public Seq<AmmoEntry> ammo = new Seq<>(); // 存储不同类型的弹药物品
    public int totalAmmo = 0; // 总弹药数（所有物品数量之和）

    @Override
    public int getMaximumAccepted(Item item) {
      return maxAmmo;
    }

    @Override
    public boolean shouldConsume() {
      // 无弹药则不消耗资源
      return super.shouldConsume() && totalAmmo < ammoPerShot;
    }

    // 弹药条目类（仅物品+数量，无 Bullet）
    public class AmmoEntry {
      public Item item;
      public int amount;

      public AmmoEntry(Item item, int amount) {
        this.item = item;
        this.amount = amount;
      }
    }

    @Override
    public void updateTile() {
      if (!isAttacking) {
        // 冷却阶段：必须弹药充足才启动攻击轮次
        if ((timer += Time.delta * delta()) >= reload && totalAmmo >= ammoPerShot) {
          updateAll();
          if (!all.isEmpty()) {
            all.sort(h -> h.dst2(x, y));
            isAttacking = true;
            attackedCount = 0;
            attackIntervalTimer = 0f;
          } else {
            timer = 0f;
            return;
          }
        }
      } else {
        // 攻击阶段：每次攻击前校验弹药
        attackIntervalTimer += Time.delta * delta();
        if (attackIntervalTimer >= 8f && attackedCount < maxTarget && totalAmmo >= ammoPerShot) {
          if (all.isEmpty())
            updateAll();
          Healthc target = all.get(attackedCount % all.size);
          if (!target.dead()) {
            attack(target); // 原攻击机制（无 Bullet）
          }
          attackedCount++;
          attackIntervalTimer = 0f;
          if (attackedCount >= maxTarget) {
            isAttacking = false;
            timer = 0f; // 重置冷却
            consumeAmmo();
          }
        }
      }
    }

    public void consumeAmmo() {
      if (cheating())
        return; // 作弊模式不消耗
      if (ammo.isEmpty())
        return;

      // 优先消耗最后添加的弹药类型
      AmmoEntry entry = ammo.peek();
      entry.amount -= ammoPerShot;
      totalAmmo -= ammoPerShot;
      totalAmmo = Math.max(totalAmmo, 0);

      // 弹药耗尽则移除该条目
      if (entry.amount <= 0) {
        ammo.pop();
      }
    }

    @Override
    public boolean acceptItem(Building source, Item item) {
      // 仅接收有效弹药，且总弹药不超过上限
      return ammoItems.contains(item) && totalAmmo + 1 <= maxAmmo;
    }

    // 新增：处理物品输入（添加到弹药列表）
    @Override
    public void handleItem(Building source, Item item) {
      if (!ammoItems.contains(item))
        return;

      // 查找已有该物品的弹药条目，叠加数量
      for (int i = 0; i < ammo.size; i++) {
        AmmoEntry entry = ammo.get(i);
        if (entry.item == item) {
          entry.amount++;
          totalAmmo++;
          ammo.swap(i, ammo.size - 1); // 移到末尾（优先消耗）
          return;
        }
      }

      // 新增弹药条目
      ammo.add(new AmmoEntry(item, 1));
      totalAmmo++;
    }

    // 禁止从塔中取出弹药（可选）
    @Override
    public int removeStack(Item item, int amount) {
      return 0;
    }

    // 序列化（保存/读取弹药状态）
    @Override
    public void write(Writes write) {
      super.write(write);
      write.i(ammo.size);
      for (AmmoEntry entry : ammo) {
        write.s(entry.item.id);
        write.i(entry.amount);
      }
      write.i(totalAmmo);
    }

    @Override
    public void read(Reads read, byte revision) {
      super.read(read, revision);
      int ammoSize = read.i();
      ammo.clear();
      for (int i = 0; i < ammoSize; i++) {
        Item item = Vars.content.item(read.s());
        int amount = read.i();
        if (ammoItems.contains(item)) { // 仅恢复有效弹药
          ammo.add(new AmmoEntry(item, amount));
        }
      }
      totalAmmo = read.i();
    }
  }
}
