package me.zircta.deatheffects;

import com.gitlab.candicey.zenithloader.ZenithLoader;
import com.gitlab.candicey.zenithloader.dependency.Dependencies;
import me.zircta.deatheffects.config.ModConfig;
import me.zircta.deatheffects.event.DeathListener;
import net.weavemc.loader.api.ModInitializer;
import net.weavemc.loader.api.event.EventBus;
import net.weavemc.loader.api.event.StartGameEvent;

public class DeathEffects implements ModInitializer {
    public static ModConfig config;

    @Override
    public void preInit() {
        ZenithLoader.INSTANCE.loadDependencies(
                Dependencies.INSTANCE.getConcentra().invoke(
                        // deatheffects.versions.json
                        "deatheffects"
                )
        );

        EventBus.subscribe(StartGameEvent.Post.class, (event) -> config = new ModConfig());
        EventBus.subscribe(StartGameEvent.Post.class, (event) -> EventBus.subscribe(new DeathListener()));
    }
}
