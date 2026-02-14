package crystal.entities.units;
import mindustry.game.Team;
import arc.graphics.Color;
import crystal.type.BuildShieldUnitType;
import static mindustry.Vars.*;
import mindustry.gen.Groups;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.graphics.Layer;
import crystal.game.WaitTime;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Bullet;
import arc.graphics.g2d.Draw;
import arc.func.Cons;
import mindustry.gen.Unitc;
import arc.math.geom.Vec2;
import arc.graphics.g2d.Lines;
import mindustry.type.UnitType;
import arc.math.Angles;
import mindustry.content.Fx;
import arc.util.Time;

import arc.func.Cons;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Builderc;
import mindustry.gen.Bullet;
import mindustry.gen.Drawc;
import mindustry.gen.Entityc;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Itemsc;
import mindustry.gen.Minerc;
import mindustry.gen.Physicsc;
import mindustry.gen.Posc;
import mindustry.gen.Rotc;
import mindustry.gen.Shieldc;
import mindustry.gen.Statusc;
import mindustry.gen.Syncc;
import mindustry.gen.Teamc;
import mindustry.gen.Unitc;
import mindustry.gen.Velc;
import mindustry.gen.Weaponsc;

@SuppressWarnings({"all", "unchecked", "deprecation"})
public abstract interface ShieldBuilderc extends Builderc, Drawc, Entityc, Healthc, Hitboxc, Itemsc, Minerc, Physicsc, Posc, Rotc, Shieldc, Statusc, Syncc, Teamc, Unitc, Velc, Weaponsc {
    Cons<Bullet> bulletc();

    boolean drawArc();

    boolean open();

    float alpha();

    float angle();

    float cooldown();

    float getRange(BuildPlan currentPlan);

    float getWidth(BuildPlan currentPlan);

    float regenRate();

    float shieldHealth();

    float shieldMaxHealth();

    float temp();

    float widthScale();

    BuildPlan currentPlan();

    BuildPlan last();

    void alpha(float alpha);

    void angle(float angle);

    void bulletc(Cons<Bullet> bulletc);

    void cooldown(float cooldown);

    void currentPlan(BuildPlan currentPlan);

    void drawArc(boolean drawArc);

    void last(BuildPlan last);

    void open(boolean open);

    void regenRate(float regenRate);

    void shieldHealth(float shieldHealth);

    void shieldMaxHealth(float shieldMaxHealth);

    void temp(float temp);

    void widthScale(float widthScale);
}
