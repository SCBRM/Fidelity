package org.scbrm.fidelity;

import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.scbrm.fidelity.bridge.IRidableEntity;

import java.util.Random;

public class WhipItem extends Item {
    private static final Random random = new Random();

    public WhipItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity playerEntity, Hand hand) {
        playerEntity.playSound(SoundEvents.ENTITY_HORSE_SADDLE.value(), 1.0F, 1.0F);
        return ActionResult.SUCCESS.withNewHandStack(playerEntity.getStackInHand(hand));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof final IRidableEntity ridable) {
            if (entity.isAlive() && ridable.isTame() && !user.getWorld().isClient) {
                if (ridable.getState() == IRidableEntity.State.ROAMING_FREE) {
                    ridable.setMasterReference(new LazyEntityReference<>(user));
                } else if (!ridable.isMaster(user)) {
                    user.sendMessage(Text.translatable("fidelity.text.not_own_animal"), true);
                    return user.getWorld().isClient ? ActionResult.SUCCESS : ActionResult.SUCCESS_SERVER;
                }

                final IRidableEntity.State state = ridable.getState().next();
                ridable.setState(state);
                if (state == IRidableEntity.State.ROAMING_FREE)
                    ridable.setMasterReference(null);
                user.sendMessage(Text.translatable("fidelity.text.setstate." + state), true);
            } else if (entity.isAlive() && !ridable.isTame() && user.getWorld().isClient) {
                spawnParticles(entity, false);
            }
            return user.getWorld().isClient ? ActionResult.SUCCESS : ActionResult.SUCCESS_SERVER;
        }
        return ActionResult.PASS;
    }

    private void spawnParticles(LivingEntity entity, boolean positive) {
        World world = entity.getWorld();
        ParticleEffect particleEffect = positive ? ParticleTypes.HEART : ParticleTypes.SMOKE;

        for (int i = 0; i < 7; ++i) {
            double d = random.nextGaussian() * 0.02D;
            double e = random.nextGaussian() * 0.02D;
            double f = random.nextGaussian() * 0.02D;
            world.addParticleClient(particleEffect, entity.getParticleX(1.0D), entity.getRandomBodyY() + 0.5D,
                    entity.getParticleZ(1.0D), d, e, f);
        }

    }

}
