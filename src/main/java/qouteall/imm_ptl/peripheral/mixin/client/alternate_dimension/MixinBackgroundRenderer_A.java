package qouteall.imm_ptl.peripheral.mixin.client.alternate_dimension;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import qouteall.imm_ptl.peripheral.alternate_dimension.AlternateDimensions;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer_A {
    //avoid alternate dimension dark when seeing from overworld
    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/Camera;getPos()Lnet/minecraft/util/math/Vec3d;"
        )
    )
    private static Vec3d redirectCameraGetPos(Camera camera) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null && AlternateDimensions.isAlternateDimension(world)) {
            return new Vec3d(
                camera.getPos().x,
                Math.max(32.0, camera.getPos().y),
                camera.getPos().z
            );
        }
        else {
            return camera.getPos();
        }
    }
}
