package org.scbrm.fidelity.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.world.World;
import org.scbrm.fidelity.bridge.IRidableEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StriderEntity.class)
public abstract class StriderEntityMixin extends AnimalEntity implements IRidableEntity {
    protected StriderEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean isTame() {
        return true;
    }
}
