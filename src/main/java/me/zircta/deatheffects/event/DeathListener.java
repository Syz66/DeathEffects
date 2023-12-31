package me.zircta.deatheffects.event;

import me.zircta.deatheffects.DeathEffects;
import me.zircta.deatheffects.config.DeathEffectsConfig;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.weavemc.loader.api.event.SubscribeEvent;

public class DeathListener {
    private final DeathEffectsConfig config = DeathEffects.config;

    @SubscribeEvent
    public void onTick(DeathEvent event) {
        if (!config.enabled) return;

        if (Minecraft.getMinecraft().theWorld != null) {
            switch (config.effect) {
                case 0 -> this.spawnFlame(event.getEntity(), (int)config.amount);
                case 1 -> this.spawnCloud(event.getEntity(), (int)config.amount);
                case 2 -> this.spawnBlood(event.getEntity(), (int)config.amount);
            }
        }
    }

    public void spawnFlame(Entity entity, int amount) {
        for (int i = 0; i < amount; i++) {
            Minecraft.getMinecraft().effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.FLAME);
        }

        if (config.sound) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("item.fireCharge.use"), ((float) entity.posX), ((float) entity.posY), ((float) entity.posZ)));
        }
    }

    public void spawnCloud(Entity entity, int amount) {
        for (int i = 0; i < amount; i++) {
            Minecraft.getMinecraft().effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CLOUD);
        }

        if (config.sound) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("fireworks.twinkle"), ((float) entity.posX), ((float) entity.posY), ((float) entity.posZ)));
        }
    }

    public void spawnBlood(Entity entity, int amount) {
        amount *= 50;
        for (int i = 0; i < amount; i++) {
            Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.BLOCK_CRACK, entity.posX, entity.posY + entity.height - 0.75, entity.posZ, 0, 0, 0, Block.getStateId(Blocks.redstone_block.getDefaultState()));
        }

        if (config.sound) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("dig.stone"), ((float) entity.posX), ((float) entity.posY), ((float) entity.posZ)));
        }
    }
}
