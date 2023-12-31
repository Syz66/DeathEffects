package me.zircta.deatheffects.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Checkbox;
import cc.polyfrost.oneconfig.config.annotations.Dropdown;
import cc.polyfrost.oneconfig.config.annotations.Slider;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;

public class DeathEffectsConfig extends Config {
    @Switch(
            name = "Enabled",
            description = "Set enabled status of the mod."
    )
    public boolean enabled;

    @Dropdown(
            name = "Effect",
            options = {"Blood", "Cloud", "Explosion", "Flames", "Lightning"},
            description = "Allows you to choose the death effect.",
            subcategory = "Effect options"
    )
    public int effect = 1;

    @Checkbox(
            name = "Sound",
            description = "Allows you to toggle sound effects.",
            subcategory = "Effect options"
    )
    public boolean sound = false;

    @Slider(
            name = "Amount",
            min = 1.0f,
            max = 5.0f,
            step = 1,
            description = "Allows you to change the amount of effects.",
            subcategory = "Effect options"
    )
    public float amount = 2.0f;

    public DeathEffectsConfig() {
        super(new Mod("Death Effects", ModType.PVP), "death-effects.json");
        this.initialize();
    }
}
