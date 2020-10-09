package net.fabricmc.fidelity.entity.ai.goal;

import net.fabricmc.fidelity.bridge.IHorseBaseEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.HorseBaseEntity;

public class ObeyMasterGoal extends Goal {
    private final HorseBaseEntity equine;
    private LivingEntity master;

    public ObeyMasterGoal(HorseBaseEntity equine) {
        this.equine = equine;
    }

    @Override
    public boolean canStart() {
        if(equine.isTame())
            return false;
        if(((IHorseBaseEntity)equine).getMaster() == null)
            return false;
        return isStateRelevant();
    }

    @Override
    public boolean shouldContinue() {
        return isStateRelevant();
    }

    @Override
    public void start() {
        master = ((IHorseBaseEntity)equine).getMaster();
    }

    @Override
    public void tick() {

    }

    private boolean isStateRelevant() {
        final IHorseBaseEntity.State state = ((IHorseBaseEntity)equine).getState();
        return state == IHorseBaseEntity.State.STANDING || state == IHorseBaseEntity.State.FOLLOWING;
    }

    private int getRandomInt(int min, int max) {
        return this.equine.getRandom().nextInt(max - min + 1) + min;
    }
}
