package crystal.content;

import crystal.content.blocks.*;
import mindustry.content.TechTree;
import mindustry.content.TechTree.TechNode;
import mindustry.ctype.UnlockableContent;
import static mindustry.content.TechTree.*;

public class CTechTree {
  public static TechNode context = null;
  public static Runnable lx = () -> {
    node(CTurrets.qianfeng, () -> {
    });
  };

  public static void load() {
    CPlanets.lx.techTree = TechTree.nodeRoot("lx", CStroage.core1, lx);
  }

  public static void addNode(UnlockableContent content, Runnable run) {
    context = TechTree.all.find(t -> t.content == content);
    run.run();
  }
}
