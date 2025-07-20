package sc.world.blocks.denfence;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import arc.math.Mathf;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectSet;
import arc.util.Log;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;
import sc.util.build.BlockList;
import sc.util.build.BlockList.Data;

public class LinkWall extends Wall {
  public BlockList blockList = new BlockList();
  public String gname;

  public LinkWall(String name) {
    super(name);
    this.solid = true;
    this.update = true;
    this.gname = "A";
    this.configurable = true;
  }

  static class WallData extends Data {
    public WallData(Building entity) {
      super(entity);
    }
  }

  public class LinkWallBuild extends Building {
    public ObjectSet<LinkWallBuild> linked = new ObjectSet<>();
    public WallData wallData = null;
    public boolean needUpdate = false;
    public String gname1 = gname;
    private final AtomicInteger linkCount = new AtomicInteger(1);

    @Override
    public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
      super.init(tile, team, shouldAdd, rotation);
      blockList.push(this);
      this.wallData = new WallData(this);
      this.linked.add(this);
      return this;
    }

    @Override
    public void updateTile() {
      super.updateTile();
      if (needUpdate) {
        for (int i = 0; i < 4; i++) {
          Tile next = tile.nearby(i);
          if (next == null || !(next.build instanceof LinkWallBuild))
            continue;
          LinkWallBuild other = (LinkWallBuild) next.build;
          if (other == null || other.gname1 != this.gname1)
            continue;
          if (!other.linked.contains(this)) {
            merge(other);
            updateLinkCount();
          }
        }
        needUpdate = false;
      }
      for (LinkWallBuild b : this.linked) {
        if (b.linked.size != this.linked.size) {
          b.linked = this.linked;
          b.wallData = this.wallData;
          Log.info(linkCount.get());
        }
      }
    }

    public int getLinkCount() {
      return linkCount.get();
    }

    private void updateLinkCount() {
      int newCount = linked.size;
      linkCount.set(newCount);
    }

    public void merge(LinkWallBuild other) {
      if (other.linked.equals(this.linked))
        return;
      if (this.linked.size < other.linked.size) {
        ObjectSet<LinkWallBuild> temp = this.linked;
        this.linked = other.linked;
        other.linked = temp;
      }
      for (LinkWallBuild wall : other.linked) {
        if (wall != this && !this.linked.contains(wall)) {
          this.linked.add(wall);
          blockList.push(wall);
          wall.linked = this.linked;
        }
      }

    }

    @Override
    public void damage(float damage) {
      damage = damage / getLinkCount();
      for (LinkWallBuild b : this.linked) {
        b.damage(damage);
      }
    }

    @Override
    public void heal(float amount) {
      amount /= getLinkCount();
      for (LinkWallBuild b : this.linked) {
        b.heal(amount);
      }

    }

    @Override
    public void remove() {
      super.remove();
      blockList.remove(this);
      for (LinkWallBuild b : this.linked) {
        b.linked.clear();
        b.wallData = new WallData(b);
        b.linked.add(this);
        b.needUpdate = true;
      }
    }

    @Override
    public void displayBars(Table table) {
      super.displayBars(table);
      WallData data = this.wallData;
      table.add(new Bar(Mathf.round(this.health) + "/" + Mathf.round(data.maxHealth), Pal.health,
          () -> this.health / data.maxHealth));
    }

    @Override
    public void display(Table table) {
      super.display(table);
      if (Vars.player.team() == this.team) {
        table.row();
        table.table(c -> {
          c.defaults().growX().height(18).pad(4);
          Label label = new Label("连接数量∶0");
          label.update(() -> {
            label.setText("连接数量∶" + this.getLinkCount());
            c.add(label).growX();
          });
        }).growX();
      }
    }

    @Override
    public void onProximityAdded() {
      super.onProximityAdded();
      for (LinkWallBuild b : linked) {
        b.needUpdate = true;
      }
    }

    @Override
    public void write(Writes write) {
      super.write(write);
      write.i(getLinkCount());
      for (LinkWallBuild b : linked) {
        write.i(b.pos());
      }
    }

    @Override
    public void read(Reads read, byte revision) {
      super.read(read, revision);
      linkCount.set(read.i());
      for (int a = 0; a < getLinkCount(); a++) {
        linked.get((LinkWallBuild) blockList.listMap.get(a));
      }
      updateLinkCount();
    }
  }
}
