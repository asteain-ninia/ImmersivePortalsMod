package qouteall.imm_ptl.peripheral.portal_generation;

import com.mojang.serialization.Codec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import qouteall.imm_ptl.core.IPGlobal;
import qouteall.imm_ptl.core.platform_specific.O_O;
import qouteall.imm_ptl.core.portal.custom_portal_gen.PortalGenInfo;
import qouteall.imm_ptl.core.portal.custom_portal_gen.form.AbstractDiligentForm;
import qouteall.imm_ptl.core.portal.custom_portal_gen.form.PortalGenForm;
import qouteall.imm_ptl.core.portal.nether_portal.BlockPortalShape;
import qouteall.imm_ptl.core.portal.nether_portal.BreakablePortalEntity;
import qouteall.imm_ptl.core.portal.nether_portal.NetherPortalEntity;

import java.util.function.Predicate;

public class DiligentNetherPortalForm extends AbstractDiligentForm {
    public DiligentNetherPortalForm() {
        super(true);
    }
    
    @Override
    public void generateNewFrame(ServerWorld fromWorld, BlockPortalShape fromShape, ServerWorld toWorld, BlockPortalShape toShape) {
        for (BlockPos blockPos : toShape.frameAreaWithCorner) {
            toWorld.setBlockState(blockPos, Blocks.OBSIDIAN.getDefaultState());
        }
    }
    
    @Override
    public BreakablePortalEntity[] generatePortalEntitiesAndPlaceholder(PortalGenInfo info) {
        info.generatePlaceholderBlocks();
        BreakablePortalEntity[] portals = info.generateBiWayBiFacedPortal(NetherPortalEntity.entityType);
     
        if (IPGlobal.netherPortalOverlay) {
            IntrinsicNetherPortalForm.initializeOverlay(portals[0], info.fromShape);
            IntrinsicNetherPortalForm.initializeOverlay(portals[1], info.fromShape);
            IntrinsicNetherPortalForm.initializeOverlay(portals[2], info.toShape);
            IntrinsicNetherPortalForm.initializeOverlay(portals[3], info.toShape);
        }
        
        return portals;
    }
    
    @Override
    public Predicate<BlockState> getOtherSideFramePredicate() {
        return O_O::isObsidian;
    }
    
    @Override
    public Predicate<BlockState> getThisSideFramePredicate() {
        return O_O::isObsidian;
    }
    
    @Override
    public Predicate<BlockState> getAreaPredicate() {
        return AbstractBlock.AbstractBlockState::isAir;
    }
    
    @Override
    public Codec<? extends PortalGenForm> getCodec() {
        throw new RuntimeException();
    }
    
    @Override
    public PortalGenForm getReverse() {
        return this;
    }
}
