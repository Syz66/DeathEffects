package me.zircta.deatheffects.utils;

import me.zircta.deatheffects.DeathEffects;
import me.zircta.deatheffects.config.DeathEffectsConfig;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

public class EffectUtils {
    private final static Minecraft mc = Minecraft.getMinecraft();
    private final static DeathEffectsConfig config = DeathEffects.config;
    private static int entityID = 0;

    public static void spawnBlood(Entity entity, int amount) {
        amount *= 25;
        for (int i = 0; i < amount; i++) {
            mc.theWorld.spawnParticle(EnumParticleTypes.BLOCK_CRACK, entity.posX, entity.posY + entity.height - 0.75, entity.posZ, 0, 0, 0, Block.getStateId(Blocks.redstone_block.getDefaultState()));
        }

        if (config.sound) {
            playSound("dig.stone", entity.posX, entity.posY, entity.posZ);
        }
    }


    public static void spawnCloud(Entity entity, int amount) {
        for (int i = 0; i < amount; i++) {
            mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CLOUD);
        }

        if (config.sound) {
            playSound("fireworks.twinkle", entity.posX, entity.posY, entity.posZ);
        }
    }

    public static void spawnExplosion(Entity entity, int amount) {
        for (int i = 0; i < amount; i++) {
            mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.EXPLOSION_NORMAL);
        }

        if (config.sound) {
            playSound("random.explode", entity.posX, entity.posY, entity.posZ);
        }
    }

    public static void spawnFlame(Entity entity, int amount) {
        for (int i = 0; i < amount; i++) {
            mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.FLAME);
        }

        if (config.sound) {
            playSound("item.fireCharge.use", entity.posX, entity.posY, entity.posZ);
        }
    }

    public static void spawnLightning(Entity entity) {
        final EntityLightningBolt entityLightningBolt = new EntityLightningBolt(mc.theWorld, entity.posX, entity.posY, entity.posZ);
        mc.theWorld.addEntityToWorld(entityID--, entityLightningBolt);

        if (config.sound) {
            playSound("ambient.weather.thunder", entity.posX, entity.posY, entity.posZ);
        }
    }

    public static void playSound(String sound, double x, double y, double z) {
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation(sound), (float) x, (float) y, (float) z));
    }
}
