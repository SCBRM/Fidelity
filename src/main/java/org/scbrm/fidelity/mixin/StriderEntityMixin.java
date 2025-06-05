package org.scbrm.fidelity.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.scbrm.fidelity.bridge.IRidableEntity;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(StriderEntity.class)
public abstract class StriderEntityMixin extends AnimalEntity implements IRidableEntity {
    protected StriderEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean isTame() {
        return true;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        ((RidableEntityMixin) (AnimalEntity) this).writeExtraCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        ((RidableEntityMixin) (AnimalEntity) this).readExtraCustomDataFromNbt(nbt);
    }

    @Inject(
        method = "interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/passive/StriderEntity;hasSaddleEquipped()Z"
        ),
        cancellable = true
    )
    public void interactMobFix(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> ci) {
        final ItemStack itemStack = player.getStackInHand(hand);
        final ActionResult actionResult = itemStack.useOnEntity(player, this, hand);
        if (actionResult.isAccepted())
            ci.setReturnValue(actionResult);
    }
}
