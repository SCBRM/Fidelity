package net.fabricmc.fidelity.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.HorseBaseEntity;

public class FollowMasterGoal extends Goal {
    private final HorseBaseEntity equine;
    private LivingEntity master;

    public FollowMasterGoal(HorseBaseEntity equine) {
        this.equine = equine;
    }

    @Override
    public boolean canStart() {
        return false;
    }
}
