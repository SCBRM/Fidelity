package org.scbrm.fidelity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.scbrm.fidelity.bridge.IHorseBaseEntity;

import java.util.Random;

public class WhipItem extends Item {
    private static final Random random = new Random();

    public WhipItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        playerEntity.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 1.0F, 1.0F);
        return new TypedActionResult<>(ActionResult.SUCCESS, playerEntity.getStackInHand(hand));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if(entity instanceof HorseBaseEntity) {
            final HorseBaseEntity equine = (HorseBaseEntity)entity;
            if(equine.isAlive() && equine.isTame() && !user.world.isClient) {
                final IHorseBaseEntity iequine = (IHorseBaseEntity)equine;
                if(iequine.getState() == IHorseBaseEntity.State.ROAMING_FREE) {
                    iequine.setMaster(user);
                } else if(iequine.getMaster() != user) {
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
