package me.zircta.deatheffects.event;

import me.zircta.deatheffects.DeathEffects;
import me.zircta.deatheffects.config.DeathEffectsConfig;
import me.zircta.deatheffects.utils.EffectUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.weavemc.loader.api.event.SubscribeEvent;
import net.weavemc.loader.api.event.TickEvent;

public class DeathListener {
    private final DeathEffectsConfig config = DeathEffects.config;

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (!config.enabled) return;

        if (Minecraft.getMinecraft().theWorld != null) {
            for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
                if (entity instanceof EntityPlayer) {
                    EntityLivingBase livingEntity = (EntityLivingBase) entity;
                    if (livingEntity.deathTime == 1) {
                        switch (config.effect) {
                            case 0 -> EffectUtils.spawnFlame(livingEntity, (int) config.amount);
                            case 1 -> EffectUtils.spawnCloud(livingEntity, (int) config.amount);
                            case 2 -> EffectUtils.spawnBlood(livingEntity, (int) config.amount);
                        }
                    }
                }
            }
        }
    }
}
