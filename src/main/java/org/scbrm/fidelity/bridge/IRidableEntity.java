package org.scbrm.fidelity.bridge;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

public interface IRidableEntity {
    enum State {
        ROAMING_FREE,
        ROAMING,
        FOLLOWING,
        STANDING;

        public State next() {
            int ret = ordinal() + 1;
            if(ret == State.values().length)
                ret = 0;
            return State.values()[ret];
        }

        public boolean isRoaming()
        {
            return this == State.ROAMING_FREE || this == State.ROAMING;
        }
        public boolean hasMaster() {
            return this != State.ROAMING_FREE;
        }
    }

    @Nullable
    LazyEntityReference<LivingEntity> getMasterReference();
    void setMasterReference(@Nullable LazyEntityReference<LivingEntity> masterReference);

    default boolean isMaster(LivingEntity entity) {
        return entity == this.getMasterReference().resolve(entity.getWorld(), LivingEntity.class);
    }

    @NotNull
    State getState();
    void setState(@NotNull State state);

    void readExtraCustomDataFromNbt(NbtCompound nbt);
    void writeExtraCustomDataToNbt(NbtCompound nbt);

    boolean isTame();
}
