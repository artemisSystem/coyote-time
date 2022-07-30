package artemis.coyote_time.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow private int noJumpDelay;

	@Shadow protected abstract void jumpFromGround();

	private int ticksFalling = 0;
	private boolean hasJumped = false;

	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "jumpFromGround", at = @At(value = "HEAD"))
	private void coyote_time_setHasJumped(CallbackInfo ci) {
		hasJumped = true;
	}

	@Inject(method = "aiStep", at = @At(value = "HEAD"))
	private void coyote_time_countTicksFalling(CallbackInfo ci) {
		if (!this.onGround) {
			ticksFalling++;
		} else {
			ticksFalling = 0;
			hasJumped = false;
		}
	}

	@Inject(method = "aiStep", at = @At(target = "Lnet/minecraft/world/entity/LivingEntity;getFluidJumpThreshold()D", value = "INVOKE", shift = At.Shift.AFTER))
	private void coyote_time_coyoteTimeJump(CallbackInfo ci) {
		if (this.ticksFalling <= 10 && !this.hasJumped) {
			this.jumpFromGround();
			this.noJumpDelay = 10;
		}
	}
}