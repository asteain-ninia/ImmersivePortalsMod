package qouteall.imm_ptl.core.mixin.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.render.context_management.PortalRendering;
import qouteall.imm_ptl.core.render.context_management.RenderStates;

@Mixin(VertexConsumerProvider.Immediate.class)
public class MixinVertexConsumerProviderImmediate {
    @Inject(
        method = "draw(Lnet/minecraft/client/render/RenderLayer;)V",
        at = @At("HEAD")
    )
    private void onBeginDraw(RenderLayer layer, CallbackInfo ci) {
        if (PortalRendering.isRenderingOddNumberOfMirrors()) {
            RenderStates.shouldForceDisableCull = true;
            GlStateManager._disableCull();
        }
    }
    
    @Inject(
        method = "draw(Lnet/minecraft/client/render/RenderLayer;)V",
        at = @At("RETURN")
    )
    private void onEndDraw(RenderLayer layer, CallbackInfo ci) {
        if (RenderStates.shouldForceDisableCull) {
            RenderStates.shouldForceDisableCull = false;
            GlStateManager._enableCull();
        }
    }
}
