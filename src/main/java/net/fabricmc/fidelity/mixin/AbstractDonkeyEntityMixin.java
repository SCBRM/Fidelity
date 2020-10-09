package net.fabricmc.fidelity.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractDonkeyEntity.class)
public abstract class AbstractDonkeyEntityMixin extends HorseBaseEntity {
    protected AbstractDonkeyEntityMixin(EntityType<? extends HorseBaseEntity> entityType, World world) {
        super(entityType, world);
    }


    @Inject(
            method = "interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/AbstractDonkeyEntity;isBreedingItem(Lnet/minecraft/item/ItemStack;)Z"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/AbstractDonkeyEntity;hasChest()Z")
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/passive/AbstractDonkeyEntity;isTame()Z"
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            cancellable = true
    )
    public void interactMobFix(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> ci, ItemStack itemStack) {
        final ActionResult actionResult = itemStack.useOnEntity(player, this, hand);
        if (actionResult.isAccepted())
            ci.setReturnValue(actionResult);
    }
}
