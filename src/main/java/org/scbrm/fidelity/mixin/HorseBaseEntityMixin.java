package org.scbrm.fidelity.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.text.TranslatableText;
import org.scbrm.fidelity.bridge.IHorseBaseEntity;
import org.scbrm.fidelity.entity.ai.goal.ObeyMasterGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(HorseBaseEntity.class)
public abstract class HorseBaseEntityMixin extends AnimalEntity implements IHorseBaseEntity {

	private static final TrackedData<Optional<UUID>> MASTER_UUID = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private State state = State.ROAMING_FREE;

	/*@Shadow protected abstract void spawnPlayerReactionParticles(boolean positive);*/

	protected HorseBaseEntityMixin(EntityType<? extends HorseBaseEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(at = @At("HEAD"), method = "initGoals()V")
	private void _initGoals(CallbackInfo info) {
		this.goalSelector.add(0, new ObeyMasterGoal((HorseBaseEntity)(AnimalEntity)this, 1.2D,10.0F, 5.0F));
	}

	@Inject(at = @At("TAIL"), method = "initDataTracker()V")
	protected void _initDataTracker(CallbackInfo info) {
		this.dataTracker.startTracking(MASTER_UUID, Optional.empty());
	}

	@Nullable
	public UUID getMasterUuid() {
		return (this.dataTracker.get(MASTER_UUID)).orElse(null);
	}

	public void setMasterUuid(@Nullable UUID uuid) {
		this.dataTracker.set(MASTER_UUID, Optional.ofNullable(uuid));
	}

	@Nullable
	public LivingEntity getMaster() {
		try {
			UUID uUID = this.getMasterUuid();
			return uUID == null ? null : this.world.getPlayerByUuid(uUID);
		} catch (IllegalArgumentException var2) {
			return null;
		}
	}

	@NotNull
	public State getState() {
		return state;
	}

	public void setState(@NotNull State state) {
		this.state = state;
	}

	@Inject(at = @At("TAIL"), method = "writeCustomDataToTag(Lnet/minecraft/nbt/CompoundTag;)V")
	public void _writeCustomDataToTag(CompoundTag tag, CallbackInfo info) {
		if (this.getMasterUuid() != null) {
			tag.putUuid("Master", this.getMasterUuid());
		}

		tag.putByte("FidelityState", (byte)this.state.ordinal());
	}

	@Inject(at = @At("TAIL"), method = "readCustomDataFromTag(Lnet/minecraft/nbt/CompoundTag;)V")
	public void _readCustomDataFromTag(CompoundTag tag, CallbackInfo info) {
		final UUID uuid = tag.containsUuid("Master") ?
				tag.getUuid("Master") :
				UUID.fromString(ServerConfigHandler.getPlayerUuidByName(this.getServer(), tag.getString("Master")));

		if (uuid != null) {
			try {
				this.setMasterUuid(uuid);
				this.state = State.values()[tag.getByte("FidelityState")];
			} catch (Throwable e) {
				this.state = State.ROAMING_FREE;
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "putPlayerOnBack(Lnet/minecraft/entity/player/PlayerEntity;)V", cancellable = true)
	void preventTheft(PlayerEntity player, CallbackInfo info){
		if(this.state.hasMaster() && !this.isMaster(player)) {
			if(!player.world.isClient)
				player.sendMessage(new TranslatableText("fidelity.text.not_own_animal"));
			info.cancel();
		}
	}

}
