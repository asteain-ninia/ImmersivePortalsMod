package qouteall.imm_ptl.core.portal.global_portals;

import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalExtension;
import qouteall.q_misc_util.Helper;
import qouteall.q_misc_util.MiscHelper;
import qouteall.q_misc_util.my_util.DQuaternion;

import java.util.function.Predicate;

public class VerticalConnectingPortal extends GlobalTrackedPortal {
    public static EntityType<VerticalConnectingPortal> entityType;
    
    public static enum ConnectorType {
        ceil, floor
    }
    
    private static Predicate<Portal> getPredicate(ConnectorType connectorType) {
        switch (connectorType) {
            case floor:
                return portal -> portal instanceof VerticalConnectingPortal && portal.getNormal().y > 0;
            default:
            case ceil:
                return portal -> portal instanceof VerticalConnectingPortal && portal.getNormal().y < 0;
        }
    }
    
    public VerticalConnectingPortal(
        EntityType<?> entityType_1,
        World world_1
    ) {
        super(entityType_1, world_1);
    }
    
    public static void connect(
        RegistryKey<World> from,
        ConnectorType connectorType,
        RegistryKey<World> to
    ) {
        connect(from, connectorType, to, false);
    }
    
    public static void connect(
        RegistryKey<World> from,
        ConnectorType connectorType,
        RegistryKey<World> to,
        boolean respectSpaceRatio
    ) {
        removeConnectingPortal(connectorType, from);
        
        ServerWorld fromWorld = MiscHelper.getServer().getWorld(from);
        ServerWorld toWorld = MiscHelper.getServer().getWorld(to);
        
        VerticalConnectingPortal connectingPortal = createConnectingPortal(
            fromWorld,
            connectorType,
            toWorld,
            respectSpaceRatio ?
                fromWorld.getDimension().getCoordinateScale() /
                    toWorld.getDimension().getCoordinateScale()
                : 1.0,
            false,
            0,
            McHelper.getMinY(fromWorld), McHelper.getMaxContentYExclusive(fromWorld),
            McHelper.getMinY(toWorld),
            McHelper.getMaxContentYExclusive(toWorld)
        );
        
        GlobalPortalStorage storage = GlobalPortalStorage.get(fromWorld);
        
        storage.addPortal(connectingPortal);
    }
    
    public static void connectMutually(
        RegistryKey<World> up,
        RegistryKey<World> down,
        boolean respectSpaceRatio
    ) {
        
        connect(up, ConnectorType.floor, down, respectSpaceRatio);
        connect(down, ConnectorType.ceil, up, respectSpaceRatio);
    }
    
    public static VerticalConnectingPortal createConnectingPortal(
        ServerWorld fromWorld,
        ConnectorType connectorType,
        ServerWorld toWorld,
        double scaling,
        boolean inverted,
        double rotationAlongYDegrees,
        int fromWorldMinY, int fromWorldMaxY, int toWorldMinY, int toWorldMaxY
    ) {
        VerticalConnectingPortal verticalConnectingPortal = new VerticalConnectingPortal(
            entityType, fromWorld
        );
        
        verticalConnectingPortal.dimensionTo = toWorld.getRegistryKey();
        verticalConnectingPortal.width = 23333333333.0d;
        verticalConnectingPortal.height = 23333333333.0d;
        
        switch (connectorType) {
            case floor:
                verticalConnectingPortal.setPosition(0, fromWorldMinY, 0);
                verticalConnectingPortal.axisW = new Vec3d(0, 0, 1);
                verticalConnectingPortal.axisH = new Vec3d(1, 0, 0);
                break;
            case ceil:
                verticalConnectingPortal.setPosition(0, fromWorldMaxY, 0);
                verticalConnectingPortal.axisW = new Vec3d(1, 0, 0);
                verticalConnectingPortal.axisH = new Vec3d(0, 0, 1);
                break;
        }
    
        
        if (!inverted) {
            switch (connectorType) {
                case floor:
                    verticalConnectingPortal.setDestination(new Vec3d(0, toWorldMaxY, 0));
                    break;
                case ceil:
                    verticalConnectingPortal.setDestination(new Vec3d(0, toWorldMinY, 0));
                    break;
            }
        }
        else {
            switch (connectorType) {
                case floor:
                    verticalConnectingPortal.setDestination(new Vec3d(0, toWorldMinY, 0));
                    break;
                case ceil:
                    verticalConnectingPortal.setDestination(new Vec3d(0, toWorldMaxY, 0));
                    break;
            }
        }
        
        DQuaternion inversionRotation = inverted ?
            DQuaternion.rotationByDegrees(new Vec3d(1, 0, 0), 180) : null;
        DQuaternion additionalRotation = rotationAlongYDegrees != 0 ?
            DQuaternion.rotationByDegrees(new Vec3d(0, 1, 0), rotationAlongYDegrees) : null;
        DQuaternion rotation = Helper.combineNullable(
            inversionRotation, additionalRotation, DQuaternion::hamiltonProduct
        );
        verticalConnectingPortal.rotation = rotation != null ? rotation.toMcQuaternion() : null;
        
        if (scaling != 1.0) {
            verticalConnectingPortal.scaling = scaling;
            verticalConnectingPortal.teleportChangesScale = false;
            PortalExtension.get(verticalConnectingPortal).adjustPositionAfterTeleport = false;
        }
        
        return verticalConnectingPortal;
    }
    
    public static void removeConnectingPortal(
        ConnectorType connectorType,
        RegistryKey<World> dimension
    ) {
        removeConnectingPortal(getPredicate(connectorType), dimension);
    }
    
    private static void removeConnectingPortal(
        Predicate<Portal> predicate, RegistryKey<World> dimension
    ) {
        ServerWorld endWorld = MiscHelper.getServer().getWorld(dimension);
        GlobalPortalStorage storage = GlobalPortalStorage.get(endWorld);
        
        storage.removePortals(
            portal -> portal instanceof VerticalConnectingPortal && predicate.test(portal)
        );
    }
    
    public static VerticalConnectingPortal getConnectingPortal(
        World world, ConnectorType type
    ) {
        return (VerticalConnectingPortal) GlobalPortalStorage.getGlobalPortals(world).stream()
            .filter(getPredicate(type))
            .findFirst().orElse(null);
    }
    
}
