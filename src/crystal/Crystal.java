package crystal;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.struct.Seq;
import arc.util.Log;
import crystal.content.CPlanets;
import crystal.core.UnitInfoSystem;
import crystal.game.UnitInfo;
import crystal.ui.TimeControl;
import crystal.world.blocks.environment.DamageFloor;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.EventType.Trigger;
import mindustry.mod.Mod;
import mindustry.type.Sector;
import mindustry.type.SectorPreset;
import mindustry.ui.dialogs.BaseDialog;

public class Crystal extends Mod {
  public static BaseDialog welcomeDialog;
  public static final String scqq = "http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=Rrju8RLWbsJstJ3rcJxWyrtop4u7uRb9&authKey=gdngZkPeYxZPhYTmjQUTjPos%2FJKckD02YSFnYLmdVojPZIzZw1T%2FbtubSoyuw2LA&noverify=0&group_code=756820891";
  public static int timer = 0;
  Seq<SectorPreset> sectorpresets = Vars.content.getBy(ContentType.sector);
  Seq<Sector> sectors = new Seq<>();

  public Crystal() {
    Log.info("Start to Loaded Crystal Mod Constructor.");
    Events.on(ClientLoadEvent.class, e -> {
      constructor();
    });
  }

  @Override
  public void loadContent() {
    Log.info("Start to Load Contents");
    Test.load();
    CPlanets.load();
    TimeControl.load();
    Log.info("hava loaded all");
  }

  public void constructor() {
    /*
     * UnitInfo.loadArray();
     * try {
     * for (var u : UnitInfo.all) {
     * if (u != null)
     * Log.info("id+" + u.id + "  sector " + u.getBoundSector());
     * }
     * } catch (Exception e) {
     * Log.err("loadarray出错", e);
     * }
     */
    // UnitInfoFileStorage.loadAll();
    loadlog();
    UnitInfoSystem.loadUnitInfo();
    showwelcome();
    Log.info("运行checkallsector");
    UnitInfoSystem.checkAllSector();
    UnitInfoSystem.saveUnitInfo();
    Log.info("运行checkallsector结束");
  }

  public void loadlog() {
    for (var preset : UnitInfo.all) {
      if (preset != null)
        sectors.add(preset.getBoundSector());
    }
    Log.info("");
    for (var u : sectors) {
      Log.info("id+" + u.id + "  sector " + u + " " + u.name());
    }
  }

  @Override
  public void init() {
    CVars.cui.init();
    replaceUI();
    Events.run(Trigger.update, () -> {
      update();
    });
    UnitInfoSystem.init();
  }

  public void update() {
    timer++;
    UnitInfoSystem.update();
    DamageFloor.update();
  }

  public void replaceUI() {
    Events.on(ClientLoadEvent.class, (e) -> {
      Events.run(Trigger.update, () -> {
        if (Vars.ui.planet.isShown() || CVars.cui.cplanet.isShown()) {
          if (Vars.ui.planet.state.planet == CPlanets.lx || CVars.cui.cplanet.state.planet == CPlanets.lx) {
            if (Vars.ui.planet.isShown()) {
              Vars.ui.planet.hide();
              if (!CVars.cui.cplanet.isShown())
                CVars.cui.cplanet.show();
            }
          } else {
            if (CVars.cui.cplanet.isShown()) {
              CVars.cui.cplanet.hide();
              if (!Vars.ui.planet.isShown())
                Vars.ui.planet.show();
            }
          }
        }
        if (Vars.ui.research.isShown()) {
          Vars.ui.research.hide();
          if (!CVars.cui.cresearch.isShown()) {
            CVars.cui.cresearch.show();
          }
        }
      });
    });
  }

  public void showwelcome() {
    welcomeDialog = new BaseDialog(Core.bundle.get("sc.welcome"));
    welcomeDialog.cont.image(Core.atlas.find("sc-crystal-core")).size(310f).pad(5.0f).row();
    welcomeDialog.cont.pane(t -> {
      t.add(Core.bundle.get("sc.text1")).row();
    }).row();
    welcomeDialog.addCloseButton();
    welcomeDialog.cont.pane((c) -> {
      c.button(Core.bundle.get("sc.qq"), () -> {
        if (!Core.app.openURI(scqq)) {
          Vars.ui.showErrorMessage("@linkfail");
          Core.app.setClipboardText(scqq);
        }
      }).color(Color.valueOf("#556352")).size(120.0f, 50.0f);
    }).pad(3f).row();
    welcomeDialog.show();
  }

  public void closeMod(String name) {
    Events.on(ClientLoadEvent.class, (e) -> {
      if (Vars.mods.getMod(name) != null)
        Vars.mods.removeMod(Vars.mods.getMod(name));
    });
  }
}
