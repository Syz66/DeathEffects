package me.zircta.deatheffects.event;

import net.minecraft.entity.EntityLivingBase;
import net.weavemc.loader.api.event.Event;

public class DeathEvent extends Event {
    public EntityLivingBase entity;

    public DeathEvent(EntityLivingBase entity) {
        this.entity = entity;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }
}
