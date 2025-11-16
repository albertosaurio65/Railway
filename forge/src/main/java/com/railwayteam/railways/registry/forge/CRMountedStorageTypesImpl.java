package com.railwayteam.railways.registry.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.fuel.tank.FuelTankMountedStorageType;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class CRMountedStorageTypesImpl {
    public static final RegistryEntry<FuelTankMountedStorageType> FUEL_TANK = Railways.registrate()
            .mountedFluidStorage("fuel_tank", FuelTankMountedStorageType::new)
            .register();

    public static void init() {}
}
