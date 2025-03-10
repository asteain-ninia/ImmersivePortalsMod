package qouteall.imm_ptl.core.platform_specific.mixin.common;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qouteall.imm_ptl.core.IPGlobal;
import qouteall.imm_ptl.core.portal.custom_portal_gen.CustomPortalGenManagement;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity_MA {
    @Inject(method = "moveToWorld", at = @At("HEAD"))
    private void onChangeDimensionByVanilla(
        ServerWorld serverWorld,
        CallbackInfoReturnable<Entity> cir
    ) {
        ServerPlayerEntity this_ = (ServerPlayerEntity) (Object) this;
        onBeforeDimensionTravel(this_);
    }
    
    // update chunk visibility data
    @Inject(method = "teleport", at = @At("HEAD"))
    private void onTeleported(
        ServerWorld targetWorld,
        double x,
        double y,
        double z,
        float yaw,
        float pitch,
        CallbackInfo ci
    ) {
        ServerPlayerEntity this_ = (ServerPlayerEntity) (Object) this;
        
        if (this_.world != targetWorld) {
            onBeforeDimensionTravel(this_);
        }
    }
    
    private static void onBeforeDimensionTravel(ServerPlayerEntity player) {
        CustomPortalGenManagement.onBeforeConventionalDimensionChange(player);
        IPGlobal.chunkDataSyncManager.onPlayerRespawn(player);
        
        IPGlobal.serverTaskList.addTask(() -> {
            CustomPortalGenManagement.onAfterConventionalDimensionChange(player);
            return true;
        });
    }
}
