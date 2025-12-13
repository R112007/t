package crystal.content;

import crystal.content.blocks.*;
import mindustry.content.TechTree;
import mindustry.content.TechTree.TechNode;
import mindustry.ctype.UnlockableContent;

import static crystal.content.Tree.*;

public class CTechTree {
  public static TechNode context = null;
  public static Runnable lx = () -> {
    node(CTurrets.qianfeng, () -> {
    });
  };

  public static void load() {
    CPlanets.lx.techTree = nodeRoot("lx", CStroage.core1, lx);
  }

  public static void addNode(UnlockableContent content, UnlockableContent child) {
    context = TechTree.all.find(t -> t.content == content);
    TechNode node = new TechNode(null, child, child.researchRequirements());
    if (!context.children.contains(node)) {
      context.children.add(node);
    }
    node.parent = context;
    node.planet = context.planet;
  }
}
