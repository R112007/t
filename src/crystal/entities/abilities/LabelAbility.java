package crystal.entities.abilities;

import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

/**
 * LabelAbility
 */
public class LabelAbility extends Ability {
  public String text;

  public LabelAbility(String text) {
    this.text = text;
  }

  @Override
  public void update(Unit unit) {
  }
}
