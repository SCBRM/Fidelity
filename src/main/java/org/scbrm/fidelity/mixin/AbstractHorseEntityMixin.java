package org.scbrm.fidelity.mixin;

import org.scbrm.fidelity.bridge.IRidableEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;

@Mixin(AbstractHorseEntity.class)
public abstract class AbstractHorseEntityMixin extends AnimalEntity implements IRidableEntity {
    protected AbstractHorseEntityMixin(EntityType<? extends AbstractHorseEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "putPlayerOnBack(Lnet/minecraft/entity/player/PlayerEntity;)V", cancellable = true)
    void preventTheft(PlayerEntity player, CallbackInfo info){
        if(this.getState().hasMaster() && !this.isMaster(player)) {
            if(!player.getWorld().isClient)
                player.sendMessage(Text.translatable("fidelity.text.not_own_animal"), true);
            info.cancel();
        }
    }

	@Inject(at = @At("TAIL"), method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V")
	public void _writeCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
		this.writeExtraCustomDataToNbt(nbt);
	}

	@Inject(at = @At("TAIL"), method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V")
	public void _readCustomDataFromNbt(NbtCompound nbt, CallbackInfo info) {
		this.readExtraCustomDataFromNbt(nbt);
	}
}
