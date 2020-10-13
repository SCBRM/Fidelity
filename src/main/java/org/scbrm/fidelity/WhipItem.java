package org.scbrm.fidelity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.scbrm.fidelity.bridge.IHorseBaseEntity;

import java.util.Random;

public class WhipItem extends Item {
    public WhipItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        playerEntity.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 1.0F, 1.0F);
        return new TypedActionResult<>(ActionResult.SUCCESS, playerEntity.getStackInHand(hand));
    }

    @Override
    public boolean useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if(entity instanceof HorseBaseEntity) {
            final HorseBaseEntity equine = (HorseBaseEntity)entity;
            if(equine.isAlive() && equine.isTame() && !user.world.isClient) {
                final IHorseBaseEntity iequine = (IHorseBaseEntity)equine;
                if(iequine.getState() == IHorseBaseEntity.State.ROAMING_FREE) {
                    iequine.setMaster(user);
                } else if(iequine.getMaster() != user) {
                    user.sendMessage(new TranslatableText("fidelity.text.not_own_animal"));
                    return true;
                }

                final IHorseBaseEntity.State state = iequine.getState().next();
                iequine.setState(state);
                if(state == IHorseBaseEntity.State.ROAMING_FREE)
                    iequine.setMasterUuid(null);
                user.sendMessage(new TranslatableText("fidelity.text.setstate." + state.toString()));
            } else if(equine.isAlive() && !equine.isTame() && user.world.isClient) {
                ((IHorseBaseEntity)equine).spawnPlayerReactionParticles( false);
            }
        }
        return true;
    }

}
