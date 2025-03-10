package qouteall.imm_ptl.core.platform_specific;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import qouteall.imm_ptl.core.IPGlobal;

@Environment(EnvType.CLIENT)
public class IPConfigGUI {
    public static Screen createClothConfigScreen(Screen parent) {
        IPConfig currConfig = IPConfig.readConfig();
        
        ConfigBuilder builder = ConfigBuilder.create();
        ConfigCategory serverSide = builder.getOrCreateCategory(
            new TranslatableText("imm_ptl.server_side_config")
        );
        ConfigCategory clientSide = builder.getOrCreateCategory(
            new TranslatableText("imm_ptl.client_side_config")
        );
        
        IntegerSliderEntry entryMaxPortalLayer = builder.entryBuilder().startIntSlider(
            new TranslatableText("imm_ptl.max_portal_layer"),
            currConfig.maxPortalLayer,
            0, 15
        ).setDefaultValue(5).build();
        BooleanListEntry entryLagAttackProof = builder.entryBuilder().startBooleanToggle(
            new TranslatableText("imm_ptl.lag_attack_proof"),
            currConfig.lagAttackProof
        ).setDefaultValue(true).build();
        IntegerSliderEntry entryPortalRenderLimit = builder.entryBuilder().startIntSlider(
            new TranslatableText("imm_ptl.portal_render_limit"),
            currConfig.portalRenderLimit,
            0, 1000
        ).setDefaultValue(200).build();
        IntegerSliderEntry entryIndirectLoadingRadiusCap = builder.entryBuilder().startIntSlider(
            new TranslatableText("imm_ptl.indirect_loading_radius_cap"),
            currConfig.indirectLoadingRadiusCap,
            1, 32
        ).setDefaultValue(8).build();
        BooleanListEntry entryEnableWarning = builder.entryBuilder().startBooleanToggle(
            new TranslatableText("imm_ptl.enable_warning"),
            currConfig.enableWarning
        ).setDefaultValue(true).build();
        BooleanListEntry entryCompatibilityRenderMode = builder.entryBuilder().startBooleanToggle(
            new TranslatableText("imm_ptl.compatibility_render_mode"),
            currConfig.compatibilityRenderMode
        ).setDefaultValue(false).build();
        BooleanListEntry entryCheckGlError = builder.entryBuilder().startBooleanToggle(
            new TranslatableText("imm_ptl.check_gl_error"),
            currConfig.doCheckGlError
        ).setDefaultValue(false).build();
        IntegerSliderEntry entryPortalSearchingRange = builder.entryBuilder().startIntSlider(
            new TranslatableText("imm_ptl.portal_searching_range"),
            currConfig.portalSearchingRange,
            32, 1000
        ).setDefaultValue(128).build();
        BooleanListEntry entryRenderYourselfInPortal = builder.entryBuilder().startBooleanToggle(
            new TranslatableText("imm_ptl.render_yourself_in_portal"),
            currConfig.renderYourselfInPortal
        ).setDefaultValue(true).build();
        BooleanListEntry entryTeleportDebug = builder.entryBuilder().startBooleanToggle(
            new TranslatableText("imm_ptl.teleportation_debug"),
            currConfig.teleportationDebug
        ).setDefaultValue(false).build();
        BooleanListEntry entryCorrectCrossPortalEntityRendering = builder.entryBuilder().startBooleanToggle(
            new TranslatableText("imm_ptl.correct_cross_portal_entity_rendering"),
            currConfig.correctCrossPortalEntityRendering
        ).setDefaultValue(true).build();
        BooleanListEntry entryPureMirror = builder.entryBuilder().startBooleanToggle(
            new TranslatableText("imm_ptl.pure_mirror"),
            currConfig.pureMirror
        ).setDefaultValue(false).build();
        BooleanListEntry entryEnableAlternateDimensions = builder.entryBuilder().startBooleanToggle(
            new TranslatableText("imm_ptl.enable_alternate_dimensions"),
            currConfig.enableAlternateDimensions
        ).setDefaultValue(true).build();
        BooleanListEntry entryReducedPortalRendering = builder.entryBuilder().startBooleanToggle(
            new TranslatableText("imm_ptl.reduced_portal_rendering"),
            currConfig.reducedPortalRendering
        ).setDefaultValue(false).build();
        BooleanListEntry entryLooseMovementCheck = builder.entryBuilder().startBooleanToggle(
            new TranslatableText("imm_ptl.loose_movement_check"),
            currConfig.looseMovementCheck
        ).setDefaultValue(false).build();
        BooleanListEntry entryVisibilityPrediction = builder.entryBuilder().startBooleanToggle(
            new TranslatableText("imm_ptl.visibility_prediction"),
            currConfig.visibilityPrediction
        ).setDefaultValue(true).build();
        BooleanListEntry entryNetherPortalOverlay = builder.entryBuilder().startBooleanToggle(
            new TranslatableText("imm_ptl.enable_nether_portal_overlay"),
            currConfig.netherPortalOverlay
        ).setDefaultValue(false).build();
        BooleanListEntry entryLightVanillaNetherPortalWhenCrouching = builder.entryBuilder().startBooleanToggle(
            new TranslatableText("imm_ptl.light_vanilla_nether_portal_when_crouching"),
            currConfig.lightVanillaNetherPortalWhenCrouching
        ).setDefaultValue(false).build();
        EnumListEntry<IPGlobal.NetherPortalMode> entryNetherPortalMode = builder.entryBuilder()
            .startEnumSelector(
                new TranslatableText("imm_ptl.nether_portal_mode"),
                IPGlobal.NetherPortalMode.class,
                currConfig.netherPortalMode
            )
            .setDefaultValue(IPGlobal.NetherPortalMode.normal)
            .build();
        EnumListEntry<IPGlobal.EndPortalMode> entryEndPortalMode = builder.entryBuilder()
            .startEnumSelector(
                new TranslatableText("imm_ptl.end_portal_mode"),
                IPGlobal.EndPortalMode.class,
                currConfig.endPortalMode
            )
            .setDefaultValue(IPGlobal.EndPortalMode.normal)
            .build();
        clientSide.addEntry(entryMaxPortalLayer);
        clientSide.addEntry(entryLagAttackProof);
        clientSide.addEntry(entryReducedPortalRendering);
        clientSide.addEntry(entryPortalRenderLimit);
        clientSide.addEntry(entryCompatibilityRenderMode);
        clientSide.addEntry(entryVisibilityPrediction);
        clientSide.addEntry(entryCheckGlError);
        clientSide.addEntry(entryPureMirror);
        clientSide.addEntry(entryRenderYourselfInPortal);
        clientSide.addEntry(entryCorrectCrossPortalEntityRendering);
        
        serverSide.addEntry(entryEnableWarning);
        serverSide.addEntry(entryIndirectLoadingRadiusCap);
        serverSide.addEntry(entryNetherPortalMode);
        serverSide.addEntry(entryEndPortalMode);
        serverSide.addEntry(entryEnableAlternateDimensions);
        serverSide.addEntry(entryNetherPortalOverlay);
        serverSide.addEntry(entryLightVanillaNetherPortalWhenCrouching);
        serverSide.addEntry(entryPortalSearchingRange);
        serverSide.addEntry(entryTeleportDebug);
        serverSide.addEntry(entryLooseMovementCheck);
        
        return builder
            .setParentScreen(parent)
            .setSavingRunnable(() -> {
                IPConfig newConfig = new IPConfig();
                newConfig.maxPortalLayer = entryMaxPortalLayer.getValue();
                newConfig.lagAttackProof = entryLagAttackProof.getValue();
                newConfig.portalRenderLimit = entryPortalRenderLimit.getValue();
                newConfig.compatibilityRenderMode = entryCompatibilityRenderMode.getValue();
                newConfig.doCheckGlError = entryCheckGlError.getValue();
                newConfig.portalSearchingRange = entryPortalSearchingRange.getValue();
                newConfig.renderYourselfInPortal = entryRenderYourselfInPortal.getValue();
                newConfig.teleportationDebug = entryTeleportDebug.getValue();
                newConfig.correctCrossPortalEntityRendering = entryCorrectCrossPortalEntityRendering.getValue();
                newConfig.pureMirror = entryPureMirror.getValue();
                newConfig.enableAlternateDimensions = entryEnableAlternateDimensions.getValue();
                newConfig.reducedPortalRendering = entryReducedPortalRendering.getValue();
                newConfig.indirectLoadingRadiusCap = entryIndirectLoadingRadiusCap.getValue();
                newConfig.netherPortalMode = entryNetherPortalMode.getValue();
                newConfig.endPortalMode = entryEndPortalMode.getValue();
                newConfig.looseMovementCheck = entryLooseMovementCheck.getValue();
                newConfig.visibilityPrediction = entryVisibilityPrediction.getValue();
                newConfig.netherPortalOverlay = entryNetherPortalOverlay.getValue();
                newConfig.lightVanillaNetherPortalWhenCrouching = entryLightVanillaNetherPortalWhenCrouching.getValue();
                
                newConfig.saveConfigFile();
                newConfig.onConfigChanged();
            })
            .build();
    }
}
