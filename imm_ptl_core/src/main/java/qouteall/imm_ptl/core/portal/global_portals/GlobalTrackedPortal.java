package qouteall.imm_ptl.core.portal.global_portals;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import qouteall.imm_ptl.core.portal.Portal;

public class GlobalTrackedPortal extends Portal {
    public static EntityType<GlobalTrackedPortal> entityType;
    
    public GlobalTrackedPortal(
        EntityType<?> entityType_1,
        World world_1
    ) {
        super(entityType_1, world_1);
    }
    
}
