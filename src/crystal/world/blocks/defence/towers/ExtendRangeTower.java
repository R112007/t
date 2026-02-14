package crystal.world.blocks.defence.towers;

import arc.Events;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectSet;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.io.Reads;
import crystal.world.blocks.defence.towers.Tower.TowerBuild;
import crystal.world.blocks.defence.towers.Tower.TowerBuildEvent;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.logic.Ranged;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import static mindustry.Vars.*;

public class ExtendRangeTower extends Block {
  public float range = 100f;

  public ExtendRangeTower(String name) {
    super(name);
    update = solid = true;
  }

  @Override
  public void drawPlace(int x, int y, int rotation, boolean valid) {
    super.drawPlace(x, y, rotation, valid);
    Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, player.team().color);
  }

  @Override
  public void setStats() {
    super.setStats();
    stats.add(Stat.range, range / tilesize, StatUnit.blocks);
  }

  public class ExtendRangeTowerBuild extends Building implements Ranged {
    public ObjectSet<TowerBuild> towers = new ObjectSet<>();
    public ObjectSet<TowerBuild> attackTowers = new ObjectSet<>();
    public ObjectSet<ExtendRangeTowerBuild> extendTowers = new ObjectSet<>();
    public boolean first = true;

    public void updateNearTowers() {
    }

    public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
      Events.on(TowerBuildEvent.class, e -> {
        if (this.dst2(e.x, e.y) <= range() * range()) {
          updateNearExtendTowers();
          for (var b : extendTowers) {
            b.updateNearExtendTowers();
          }
          for (var b : extendTowers) {
            b.updateTowerOwnership();
          }
          for (var b : extendTowers) {
            b.syncAttackTowers();
          }
        }
      });
      return super.init(tile, team, shouldAdd, rotation);
    }

    public void updateNearExtendTowers() {
      ObjectSet<ExtendRangeTowerBuild> connectedSet = new ObjectSet<>();
      Queue<ExtendRangeTowerBuild> queue = new Queue<>();

      // 初始化：添加自身到连通集合和遍历队列
      connectedSet.add(this);
      queue.add(this);

      // BFS遍历所有连通实例（避免递归循环，效率更高）
      while (!queue.isEmpty()) {
        ExtendRangeTowerBuild current = queue.removeFirst();
        // 利用indexer高效查询范围内同类型实例（已索引，范围查询O(1)级）
        indexer.eachBlock(null, current.x, current.y, current.range(),
            build -> build.team == current.team && !build.dead && build instanceof ExtendRangeTowerBuild,
            build -> {
              ExtendRangeTowerBuild tower = (ExtendRangeTowerBuild) build;
              // 未加入连通集合则添加，避免重复遍历
              if (!connectedSet.contains(tower)) {
                connectedSet.add(tower);
                queue.add(tower);
              }
            });
      }

      // 同步所有连通实例的集合（每个实例持有独立副本，避免并发修改问题）
      for (ExtendRangeTowerBuild tower : connectedSet) {
        tower.extendTowers.clear();
        tower.extendTowers.addAll(connectedSet);
      }
      updateTowerOwnership();
      syncAttackTowers();
    }

    @Override
    public void placed() {
      super.placed();
      updateNearExtendTowers();
          for (var b : extendTowers) {
            b.updateTowerOwnership();
          }
          for (var b : extendTowers) {
            b.syncAttackTowers();
          }
    }

    @Override
    public void updateTile() {
      if (first) {
        updateNearExtendTowers();
        first = false;
      }
    }

    /** 扫描当前实例范围内的所有TowerBuild */
    private Seq<TowerBuild> scanRangeTowers() {
      Seq<TowerBuild> result = new Seq<>();
      indexer.eachBlock(null, this.x, this.y, this.range(),
          build -> build.team == this.team && !build.dead && build instanceof TowerBlock,
          build -> result.add((TowerBuild) build));
      return result;
    }

    private void updateTowerOwnership() {
      Seq<ExtendRangeTowerBuild> connected = new Seq<>(this.extendTowers.toSeq());
      if (connected.isEmpty())
        return;

      // 收集所有连通实例范围内的TowerBuild（去重）
      Seq<TowerBuild> allTowers = new Seq<>();
      for (ExtendRangeTowerBuild tower : connected) {
        allTowers.addAll(tower.scanRangeTowers());
      }
      allTowers.distinct();

      // 重置所有连通实例的towers集合
      for (ExtendRangeTowerBuild tower : connected) {
        tower.towers.clear();
      }

      // 为每个TowerBuild分配最近的归属
      for (TowerBuild towerBuild : allTowers) {
        ExtendRangeTowerBuild closest = null;
        float minDist = Float.MAX_VALUE;

        // 计算到每个连通实例的平方距离（避免开根号，提升效率）
        for (ExtendRangeTowerBuild extendTower : connected) {
          float dist = towerBuild.dst2(extendTower.x, extendTower.y);
          if (dist < minDist) {
            minDist = dist;
            closest = extendTower;
          }
        }

        // 添加到最近实例的towers集合
        if (closest != null) {
          closest.towers.add(towerBuild);
        }
      }
    }

    private void syncAttackTowers() {
      this.attackTowers.clear();
      for (ExtendRangeTowerBuild tower : this.extendTowers) {
        this.attackTowers.addAll(tower.towers);
      }
    }

    @Override
    public void display(Table table) {
      super.display(table);
      if (Vars.player.team() == this.team) {
        table.row();
        table.label(() -> {
          return "链接数量" + this.extendTowers.size;
        }).pad(4).wrap().width(200f).left();
        table.row();
        table.label(() -> "自身归属塔数量: " + this.towers.size).pad(4).wrap().width(200f).left();
        table.row();
        table.label(() -> "同步塔总数: " + this.attackTowers.size).pad(4).wrap().width(200f).left();
      }
    }

    @Override
    public float range() {
      return range;
    }

    @Override
    public void drawSelect() {
      Drawf.dashCircle(x, y, range(), team.color);
    }

    @Override
    public void remove() {
      // 1. 保存当前连通集合（避免删除过程中集合被修改）
      ObjectSet<ExtendRangeTowerBuild> affectedTowers = extendTowers.copy();
      // 2. 从所有受影响实例的集合中移除自身
      for (ExtendRangeTowerBuild tower : affectedTowers) {
        if (tower.isValid()) { // 过滤已失效实例
          tower.extendTowers.remove(this);
        }
      }
      // 3. 所有受影响实例重新构建连通集合，实现全局刷新
      for (ExtendRangeTowerBuild tower : affectedTowers) {
        if (tower.isValid()) {
          tower.updateNearExtendTowers();
        }
      }
      super.remove();
    }

    @Override
    public void read(Reads read, byte revision) {
      super.read(read, revision);
    }
  }
}
