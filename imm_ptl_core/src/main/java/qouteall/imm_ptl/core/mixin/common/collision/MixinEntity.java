package qouteall.imm_ptl.core.mixin.common.collision;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qouteall.imm_ptl.core.IPGlobal;
import qouteall.imm_ptl.core.IPMcHelper;
import qouteall.imm_ptl.core.ducks.IEEntity;
import qouteall.imm_ptl.core.portal.EndPortalEntity;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.teleportation.CollisionHelper;
import qouteall.q_misc_util.Helper;
import qouteall.q_misc_util.my_util.LimitedLogger;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEEntity {
    
    private Entity collidingPortal;
    private long collidingPortalActiveTickTime;
    
    @Shadow
    public abstract Box getBoundingBox();
    
    @Shadow
    public World world;
    
    @Shadow
    public abstract void setBoundingBox(Box box_1);
    
    @Shadow
    protected abstract Vec3d adjustMovementForCollisions(Vec3d vec3d_1);
    
    @Shadow
    public abstract Text getName();
    
    @Shadow
    public abstract double getX();
    
    @Shadow
    public abstract double getY();
    
    @Shadow
    public abstract double getZ();
    
    @Shadow
    protected abstract BlockPos getLandingPos();
    
    @Shadow
    public boolean inanimate;
    
    @Shadow
    public int age;
    
    @Shadow
    public abstract Vec3d getVelocity();
    
    @Shadow
    protected abstract void unsetRemoved();
    
    //maintain collidingPortal field
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTicking(CallbackInfo ci) {
        tickCollidingPortal(1);
    }
    
    private static final LimitedLogger limitedLogger = new LimitedLogger(20);
    
    @Redirect(
        method = "move",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"
        )
    )
    private Vec3d redirectHandleCollisions(Entity entity, Vec3d attemptedMove) {
        if (!IPGlobal.enableServerCollision) {
            if (!entity.world.isClient()) {
                if (entity instanceof PlayerEntity) {
                    return attemptedMove;
                }
                else {
                    return Vec3d.ZERO;
                }
            }
        }
        
        if (attemptedMove.lengthSquared() > 60 * 60) {
            limitedLogger.invoke(() -> {
                Helper.err("Entity moves too fast " + entity + attemptedMove + entity.world.getTime());
                new Throwable().printStackTrace();
            });

//            if (entity instanceof ServerPlayerEntity) {
//                ServerTeleportationManager.sendPositionConfirmMessage(((ServerPlayerEntity) entity));
//                Helper.log("position confirm message sent " + entity);
//            }
            
            return attemptedMove;
        }
        
        if (getVelocity().lengthSquared() > 2) {
            CollisionHelper.updateCollidingPortalNow(entity);
        }
        
        if (collidingPortal == null ||
//            entity.hasPassengers() ||
//            entity.hasVehicle() ||
            !IPGlobal.crossPortalCollision
        ) {
            return adjustMovementForCollisions(attemptedMove);
        }
        
        Vec3d result = CollisionHelper.handleCollisionHalfwayInPortal(
            (Entity) (Object) this,
            attemptedMove,
            getCollidingPortal(),
            attemptedMove1 -> adjustMovementForCollisions(attemptedMove1)
        );
        return result;
    }
    
    //don't burn when jumping into end portal
    @Inject(
        method = "isFireImmune",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onIsFireImmune(CallbackInfoReturnable<Boolean> cir) {
        if (getCollidingPortal() instanceof EndPortalEntity) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
    
    @Redirect(
        method = "checkBlockCollision",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;getBoundingBox()Lnet/minecraft/util/math/Box;"
        )
    )
    private Box redirectBoundingBoxInCheckingBlockCollision(Entity entity) {
        return CollisionHelper.getActiveCollisionBox(entity);
    }
    
    // avoid suffocation when colliding with a portal on wall
    @Inject(method = "isInsideWall", at = @At("HEAD"), cancellable = true)
    private void onIsInsideWall(CallbackInfoReturnable<Boolean> cir) {
        if (isRecentlyCollidingWithPortal()) {
            cir.setReturnValue(false);
        }
    }
    
    //for teleportation debug
    @Inject(
        method = "setPos",
        at = @At("HEAD")
    )
    private void onSetPos(double nx, double ny, double nz, CallbackInfo ci) {
        if (((Object) this) instanceof ServerPlayerEntity) {
            if (IPGlobal.teleportationDebugEnabled) {
                if (Math.abs(getX() - nx) > 10 ||
                    Math.abs(getY() - ny) > 10 ||
                    Math.abs(getZ() - nz) > 10
                ) {
                    Helper.log(String.format(
                        "%s %s teleported from %s %s %s to %s %s %s",
                        getName().asString(),
                        world.getRegistryKey(),
                        (int) getX(), (int) getY(), (int) getZ(),
                        (int) nx, (int) ny, (int) nz
                    ));
                    new Throwable().printStackTrace();
                }
            }
        }
    }
    
    // Avoid instant crouching when crossing a scaling portal
    @Inject(method = "setPose", at = @At("HEAD"), cancellable = true)
    private void onSetPose(EntityPose pose, CallbackInfo ci) {
        Entity this_ = (Entity) (Object) this;
        
        if (this_ instanceof PlayerEntity) {
            if (this_.getPose() == EntityPose.STANDING) {
                if (pose == EntityPose.SWIMMING) {
                    if (isRecentlyCollidingWithPortal()) {
                        ci.cancel();
                    }
                }
            }
        }
    }
    
    //fix climbing onto ladder cross portal
    @Inject(method = "getBlockStateAtPos", at = @At("HEAD"), cancellable = true)
    private void onGetBlockState(CallbackInfoReturnable<BlockState> cir) {
        Portal collidingPortal = ((IEEntity) this).getCollidingPortal();
        Entity this_ = (Entity) (Object) this;
        if (collidingPortal != null) {
            if (collidingPortal.getNormal().y > 0) {
                BlockPos remoteLandingPos = new BlockPos(
                    collidingPortal.transformPoint(this_.getPos())
                );
                
                World destinationWorld = collidingPortal.getDestinationWorld();
                
                if (destinationWorld.isChunkLoaded(remoteLandingPos)) {
                    BlockState result = destinationWorld.getBlockState(remoteLandingPos);
                    
                    if (!result.isAir()) {
                        cir.setReturnValue(result);
                        cir.cancel();
                    }
                }
            }
        }
    }
    
    @Override
    public Portal getCollidingPortal() {
        return ((Portal) collidingPortal);
    }
    
    @Override
    public void tickCollidingPortal(float tickDelta) {
        Entity this_ = (Entity) (Object) this;
        
        if (collidingPortal != null) {
            if (collidingPortal.world != world) {
                collidingPortal = null;
            }
            else {
                Box stretchedBoundingBox = CollisionHelper.getStretchedBoundingBox(this_);
                if (!stretchedBoundingBox.expand(0.5).intersects(collidingPortal.getBoundingBox())) {
                    collidingPortal = null;
                }
            }
            
            if (Math.abs(age - collidingPortalActiveTickTime) >= 3) {
                collidingPortal = null;
            }
        }
        
        if (world.isClient) {
            IPMcHelper.onClientEntityTick(this_);
        }
    }
    
    @Override
    public void notifyCollidingWithPortal(Entity portal) {
        Entity this_ = (Entity) (Object) this;
        
        collidingPortal = portal;
        collidingPortalActiveTickTime = age;//world time may jump due to time synchroization
        ((Portal) portal).onCollidingWithEntity(this_);
    }
    
    @Override
    public boolean isRecentlyCollidingWithPortal() {
        return (age - collidingPortalActiveTickTime) < 20;
    }
    
    @Override
    public void portal_unsetRemoved() {
        unsetRemoved();
    }
}
