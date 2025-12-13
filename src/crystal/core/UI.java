package crystal.core;

import arc.Core;
import arc.scene.ui.Button;
import crystal.CVars;
import crystal.content.WorldStuffs;
import crystal.ui.dialogs.CPlanetDialog;
import crystal.ui.dialogs.CResearchDialog;
import crystal.ui.dialogs.WorldStuffDialog;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.Styles;

public class UI {
  public CResearchDialog cresearch;
  public CPlanetDialog cplanet;
  public WorldStuffDialog stuff;

  public void init() {
    WorldStuffs.load();
    cresearch = new CResearchDialog();
    cplanet = new CPlanetDialog();
    stuff = new WorldStuffDialog();
    if (CVars.debug)
      addButton();
  }

  public void addButton() {
    Button targetButton = new Button(Styles.black5);
    targetButton.button("按钮", Icon.addSmall, () -> stuff.show()).size(200f, 54f).pad(2f).bottom(); // 与原有按钮尺寸一致（返回/科技树按钮尺寸）
    Core.app.post(() -> {
      cplanet.shown(() -> {
        cplanet.buttons.removeChild(targetButton);
        cplanet.buttons.add(targetButton).pad(2f).bottom();
      });
    });
  }
}
