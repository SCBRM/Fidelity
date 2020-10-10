package org.scbrm.fidelity.entity.ai.goal;

import org.scbrm.fidelity.bridge.IHorseBaseEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.Optional;

public class ObeyMasterGoal extends Goal {
    private final HorseBaseEntity equine;
    private LivingEntity master;
    private final WorldView world;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;

    public ObeyMasterGoal(HorseBaseEntity equine, double speed, float minDistance, float maxDistance) {
        this.equine = equine;
        this.world = equine.world;
        this.speed = speed;
        this.navigation = equine.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean canStart() {
        if(!equine.isTame())
            return false;
        final LivingEntity master = ((IHorseBaseEntity)equine).getMaster();
        if(master == null)
            return false;
        return isStateRelevant().orElse(this.equine.squaredDistanceTo(master) >= (double)(minDistance * minDistance));
    }

    @Override
    public boolean shouldContinue() {
        return isStateRelevant().orElse(this.equine.squaredDistanceTo(this.master) > (double)(maxDistance * maxDistance));
    }

    @Override
    public void start() {
        this.master = ((IHorseBaseEntity)equine).getMaster();
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.equine.getPathfindingPenalty(PathNodeType.WATER);
        this.equine.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    public void stop() {
        this.master = null;
        this.navigation.stop();
        this.equine.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    }

    @Override
    public void tick() {
        if(((IHorseBaseEntity)equine).getState() == IHorseBaseEntity.State.STANDING) {
            this.navigation.stop();
            return;
        }
        this.equine.getLookControl().lookAt(this.master, 10.0F, (float)this.equine.getLookPitchSpeed());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 10;
            if (!this.equine.isLeashed() && !this.equine.hasVehicle()) {
                if (this.equine.squaredDistanceTo(this.master) >= 144.0D) {
                    this.tryTeleport();
                } else {
                    this.navigation.startMovingTo(this.master, this.speed);
                }

            }
        }
    }

    private void tryTeleport() {
        BlockPos blockPos = new BlockPos(this.master);

        for(int i = 0; i < 10; ++i) {
            int j = this.getRandomInt(-3, 3);
            int k = this.getRandomInt(-1, 1);
            int l = this.getRandomInt(-3, 3);
            boolean bl = this.tryTeleportTo(blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
            if (bl) {
                return;
            }
        }

    }

    private boolean tryTeleportTo(int x, int y, int z) {
        if (Math.abs((double)x - this.master.getX()) < 2.0D && Math.abs((double)z - this.master.getZ()) < 2.0D) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.equine.refreshPositionAndAngles((double)((float)x + 0.5F), (double)y, (double)((float)z + 0.5F), this.equine.yaw, this.equine.pitch);
            this.navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        PathNodeType pathNodeType = LandPathNodeMaker.getPathNodeType(this.world, pos.getX(), pos.getY(), pos.getZ());
        if (pathNodeType != PathNodeType.WALKABLE) {
            return false;
        } else {
            final BlockPos blockPos = pos.subtract(new BlockPos(this.equine));
            return this.world.doesNotCollide(this.equine, this.equine.getBoundingBox().offset(blockPos));
        }
    }

    private Optional<Boolean> isStateRelevant() {
        final IHorseBaseEntity.State state = ((IHorseBaseEntity)equine).getState();
        if(state == IHorseBaseEntity.State.STANDING)
            return Optional.of(true);
        if(state != IHorseBaseEntity.State.FOLLOWING)
            return Optional.of(false);
        return Optional.empty();
    }

    private int getRandomInt(int min, int max) {
        return this.equine.getRandom().nextInt(max - min + 1) + min;
    }
}
