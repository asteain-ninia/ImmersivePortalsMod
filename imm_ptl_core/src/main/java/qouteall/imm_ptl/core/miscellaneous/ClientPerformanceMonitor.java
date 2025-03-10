package qouteall.imm_ptl.core.miscellaneous;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import qouteall.imm_ptl.core.chunk_loading.PerformanceLevel;
import qouteall.imm_ptl.core.commands.PortalDebugCommands;
import qouteall.q_misc_util.api.McRemoteProcedureCall;

import java.util.ArrayDeque;

@Environment(EnvType.CLIENT)
public class ClientPerformanceMonitor {
    
    public static PerformanceLevel currentPerformanceLevel = PerformanceLevel.medium;
    
    public static class Record {
        public final int FPS;
        public final int freeMemoryMB;
        
        public Record(int FPS, int freeMemoryMB) {
            this.FPS = FPS;
            this.freeMemoryMB = freeMemoryMB;
        }
    }
    
    private static final ArrayDeque<Record> records = new ArrayDeque<>();
    private static int averageFps = 60;
    private static int minimumFps = 60;
    private static int averageFreeMemoryMB = 1000;
    private static final int sampleNum = 20;
    
    private static int counter = 0;
    
    public static void updateEverySecond(int newFps) {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        
        long freeMemory = Runtime.getRuntime().freeMemory();
        int freeMemoryMB = (int) PortalDebugCommands.toMiB(freeMemory);
        
        records.addLast(new Record(newFps, freeMemoryMB));
        
        if (records.size() > sampleNum) {
            records.removeFirst();
        }
        
        averageFps = (int) records.stream().mapToInt(r -> r.FPS).average().orElse(60);
        minimumFps = (int) records.stream().mapToInt(r -> r.FPS).min().orElse(60);
        averageFreeMemoryMB = (int) records.stream()
            .mapToInt(r -> r.freeMemoryMB).average().orElse(1000);
        
        counter++;
        if (counter % 5 == 0) {
            sendPerformanceInfo();
        }
    }
    
    public static int getAverageFps() {
        return averageFps;
    }
    
    public static int getMinimumFps() {
        return minimumFps;
    }
    
    public static int getAverageFreeMemoryMB() {
        return averageFreeMemoryMB;
    }
    
    private static void sendPerformanceInfo() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        
        currentPerformanceLevel = PerformanceLevel.getPerformanceLevel(averageFps, averageFreeMemoryMB);
        
        McRemoteProcedureCall.tellServerToInvoke(
            "qouteall.imm_ptl.core.chunk_loading.NewChunkTrackingGraph.RemoteCallables.acceptClientPerformanceInfo",
            currentPerformanceLevel
        );
    }
}
