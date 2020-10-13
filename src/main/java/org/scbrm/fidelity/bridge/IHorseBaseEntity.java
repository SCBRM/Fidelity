package org.scbrm.fidelity.bridge;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IHorseBaseEntity {
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
    UUID getMasterUuid();
    void setMasterUuid(@Nullable UUID uuid);

    @Nullable
    LivingEntity getMaster();
    default void setMaster(@NotNull PlayerEntity player) { this.setMasterUuid(player.getUuid()); }

    default boolean isMaster(LivingEntity entity) {
        return entity == this.getMaster();
    }

    @NotNull
    State getState();
    void setState(@NotNull State state);

    void spawnPlayerReactionParticles(boolean positive);
}
