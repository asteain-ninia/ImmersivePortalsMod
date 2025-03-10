package qouteall.imm_ptl.core.render.context_management;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.world.World;
import qouteall.imm_ptl.core.ducks.IEGameRenderer;

public class DimensionRenderHelper {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public World world;
    
    public final LightmapTextureManager lightmapTexture;
    
    public DimensionRenderHelper(World world) {
        this.world = world;
    
        if (client.world == world) {
            IEGameRenderer gameRenderer = (IEGameRenderer) client.gameRenderer;
        
            lightmapTexture = client.gameRenderer.getLightmapTextureManager();
        }
        else {
            lightmapTexture = new LightmapTextureManager(client.gameRenderer, client);
        }
    }
    
    public void tick() {
        lightmapTexture.tick();
    }
    
    public void cleanUp() {
        if (lightmapTexture != client.gameRenderer.getLightmapTextureManager()) {
            lightmapTexture.close();
        }
    }
    
}
