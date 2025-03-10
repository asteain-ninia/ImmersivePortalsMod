package qouteall.imm_ptl.core.mixin.common.portal_generation;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.portal.custom_portal_gen.CustomPortalGenManagement;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity_P {
    @Shadow
    public abstract ItemStack getStack();
    
    @Inject(
        method = "tick",
        at = @At("TAIL")
    )
    private void onItemTickEnded(CallbackInfo ci) {
        ItemEntity this_ = (ItemEntity) (Object) this;
        if (this_.isRemoved()) {
            return;
        }
        
        if (this_.world.isClient()) {
            return;
        }
        
        this_.world.getProfiler().push("imm_ptl_item_tick");
        CustomPortalGenManagement.onItemTick(this_);
        this_.world.getProfiler().pop();
    }
}
