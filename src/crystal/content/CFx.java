package crystal.content;

import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Rand;
import crystal.graphics.CPal;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.world.Block;

import static arc.graphics.g2d.Draw.*;

public class CFx {
  public static Block block = new Block("") {
    {
      size = 1;
    }
  };
  public static final Rand rand = new Rand();
  public static Effect spawn1 = new Effect(120f, e -> {
    color(CPal.blue1);
    Drawf.flame(e.x, e.y, 5, 360, 50, 50, 5);
    // Lines.arc(e.x, e.y, 50f, 10f);
    // Fill.circle(e.x, e.y, 80f);
    color(CPal.dark_blue2);
    // Fill.square(e.x, e.y, 60f);
    // Lines.line(e.x + block.offset, e.y + block.offset, 100, 100);
    color(CPal.dark_sharedyellow);
    // Fill.arc(e.x, e.y, 50f, 10f, 90f, 6);
  });
}
