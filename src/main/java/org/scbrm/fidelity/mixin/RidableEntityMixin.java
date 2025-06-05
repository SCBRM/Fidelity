package org.scbrm.fidelity.mixin;

import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.scbrm.fidelity.bridge.IRidableEntity;
import org.scbrm.fidelity.entity.ai.goal.ObeyMasterGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
// import net.minecraft.entity.data.DataTracker;
// import net.minecraft.entity.data.TrackedData;
// import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// import java.util.Optional;
// import java.util.UUID;

@Mixin(value = { AbstractHorseEntity.class, StriderEntity.class })
public abstract class RidableEntityMixin extends AnimalEntity implements IRidableEntity {

	// private static final TrackedData<Optional<UUID>> MASTER_UUID = DataTracker.registerData(AbstractHorseEntity.class,
	// 		TrackedDataHandlerRegistry.OPTIONAL_UUID);
	@Nullable
	private LazyEntityReference<LivingEntity> master;
	private State state = State.ROAMING_FREE;

	protected RidableEntityMixin(EntityType<? extends AbstractHorseEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(at = @At("HEAD"), method = "initGoals()V")
	private void _initGoals(CallbackInfo info) {
		this.goalSelector.add(0, new ObeyMasterGoal(this, 1.2D, 10.0F, 5.0F));
	}

	// @Inject(at = @At("TAIL"), method = "initDataTracker(Lnet/minecraft/entity/data/DataTracker$Builder;)V")
	// protected void _initDataTracker(CallbackInfo info, DataTracker.Builder builder) {
	// 	builder.add(MASTER_UUID, Optional.empty());
	// }

	@Override
	@Nullable
	public LazyEntityReference<LivingEntity> getMasterReference() {
		return this.master;
	}

	@Override
	public void setMasterReference(@Nullable LazyEntityReference<LivingEntity> masterReference) {
		this.master = masterReference;
	}

	@Override
	@NotNull
	public State getState() {
		return state;
	}

	@Override
	public void setState(@NotNull State state) {
		this.state = state;
	}

	@Override
	public void writeExtraCustomDataToNbt(NbtCompound nbt) {
		if (this.master != null) {
			this.master.writeNbt(nbt, "Master");
		}

		nbt.putByte("FidelityState", (byte) this.state.ordinal());
	}

	@Override
	public void readExtraCustomDataFromNbt(NbtCompound nbt) {
		this.master = LazyEntityReference.fromNbtOrPlayerName(nbt, "Master", this.getWorld());

		if (this.master != null) {
			try {
				this.state = State.values()[nbt.getByte("FidelityState").get()];
			} catch (Throwable e) {
				this.state = State.ROAMING_FREE;
			}
		}
	}
}
