package org.scbrm.fidelity.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.scbrm.fidelity.bridge.IRidableEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.util.Util.NIL_UUID;

@Mixin(HorseBaseEntity.class)
public abstract class HorseBaseEntityMixin extends AnimalEntity implements IRidableEntity {
    protected HorseBaseEntityMixin(EntityType<? extends HorseBaseEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "putPlayerOnBack(Lnet/minecraft/entity/player/PlayerEntity;)V", cancellable = true)
    void preventTheft(PlayerEntity player, CallbackInfo info){
        if(this.getState().hasMaster() && !this.isMaster(player)) {
            if(!player.world.isClient)
                player.sendSystemMessage(new TranslatableText("fidelity.text.not_own_animal"), NIL_UUID);
            info.cancel();
        }
    }
}
