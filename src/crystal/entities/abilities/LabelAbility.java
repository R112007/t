package crystal.entities.abilities;

import arc.math.Rand;
import crystal.game.WaitTime;
import mindustry.Vars;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

public class LabelAbility extends Ability {
  public String text;
  public static Rand rand = new Rand();

  public LabelAbility(String text) {
    this.text = text;
  }

  @Override
  public void update(Unit unit) {
    if (WaitTime.waittime(90f)) {
      Vars.ui.showLabel(text, 1.5f, unit.x + rand.random(-10, 10), unit.y + rand.random(-10, 10));
    }
  }
}
