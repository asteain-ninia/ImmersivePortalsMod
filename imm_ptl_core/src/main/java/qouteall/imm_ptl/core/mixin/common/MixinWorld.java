package qouteall.imm_ptl.core.mixin.common;

import net.minecraft.entity.Entity;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLookup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.ducks.IEWorld;

@Mixin(World.class)
public abstract class MixinWorld implements IEWorld {
    
    @Shadow
    @Final
    protected MutableWorldProperties properties;
    
    @Shadow
    public abstract RegistryKey<World> getRegistryKey();
    
    @Shadow
    protected float rainGradient;
    
    @Shadow
    protected float thunderGradient;
    
    @Shadow
    protected float rainGradientPrev;
    
    @Shadow
    protected float thunderGradientPrev;
    
    @Shadow
    protected abstract EntityLookup<Entity> getEntityLookup();
    
    @Shadow
    @Final
    private Thread thread;
    
    // Fix overworld rain cause nether fog change
    @Inject(method = "initWeatherGradients", at = @At("TAIL"))
    private void onInitWeatherGradients(CallbackInfo ci) {
        if (getRegistryKey() == World.NETHER) {
            rainGradient = 0;
            rainGradientPrev = 0;
            thunderGradient = 0;
            thunderGradientPrev = 0;
        }
    }
    
    @Override
    public MutableWorldProperties myGetProperties() {
        return properties;
    }
    
    @Override
    public void portal_setWeather(float rainGradPrev, float rainGrad, float thunderGradPrev, float thunderGrad) {
        rainGradientPrev = rainGradPrev;
        rainGradient = rainGrad;
        thunderGradientPrev = thunderGradPrev;
        thunderGradient = thunderGrad;
    }
    
    @Override
    public EntityLookup<Entity> portal_getEntityLookup() {
        return getEntityLookup();
    }
    
    @Override
    public Thread portal_getThread() {
        return thread;
    }
}
