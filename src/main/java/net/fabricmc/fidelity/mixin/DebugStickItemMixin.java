package net.fabricmc.fidelity.mixin;

import net.fabricmc.fidelity.bridge.IHorseBaseEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DebugStickItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(DebugStickItem.class)
public abstract class DebugStickItemMixin extends Item {
    private static final Random random = new Random();

    public DebugStickItemMixin(Item.Settings item$Settings_1) {
        super(item$Settings_1);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if(entity instanceof HorseBaseEntity) {
            final HorseBaseEntity equine = (HorseBaseEntity)entity;
            if(equine.isAlive() && equine.isTame() && !user.world.isClient) {
                final IHorseBaseEntity iequine = (IHorseBaseEntity)equine;
                if(iequine.getState() == IHorseBaseEntity.State.ROAMING_FREE) {
                    iequine.setMasterUuid(user.getUuid());
                } else if(iequine.getMasterUuid() != user.getUuid()) {
                    user.sendSystemMessage(new LiteralText("You do not own this animal"), Util.NIL_UUID);
                    return ActionResult.FAIL;
                }

                final IHorseBaseEntity.State state = iequine.getState().next();
                iequine.setState(state);
                if(state == IHorseBaseEntity.State.ROAMING_FREE)
                    iequine.setMasterUuid(null);
                user.sendSystemMessage(new LiteralText("Animal set to " + state.toString()), Util.NIL_UUID);
            } else if(equine.isAlive() && !equine.isTame() && user.world.isClient) {
                spawnParticles(entity, false);
            }
            return ActionResult.success(user.world.isClient);
        }
        return ActionResult.PASS;
    }

    private void spawnParticles(LivingEntity entity, boolean positive) {
        ParticleEffect particleEffect = positive ? ParticleTypes.HEART : ParticleTypes.SMOKE;

        for(int i = 0; i < 7; ++i) {
            double d = random.nextGaussian() * 0.02D;
            double e = random.nextGaussian() * 0.02D;
            double f = random.nextGaussian() * 0.02D;
            entity.world.addParticle(particleEffect, entity.getParticleX(1.0D), entity.getRandomBodyY() + 0.5D, entity.getParticleZ(1.0D), d, e, f);
        }

    }
}
