package com.railwayteam.railways.registry.forge;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRParticleTypes;
import com.simibubi.create.foundation.particle.ICustomParticleData;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.eventbus.api.IEventBus;
import net.neoforged.registries.DeferredRegister;
import net.neoforged.registries.RegistryObject;

import java.util.function.Supplier;

public class CRParticleTypesParticleEntryImpl {

    private static final DeferredRegister<ParticleType<?>> REGISTER =
            DeferredRegister.create(Railways.MOD_ID, ParticleType.class);

    public static void register(String id, Supplier<ParticleType<?>> supplier) {
        REGISTER.register(id, supplier);
    }

    public static void register(IEventBus modEventBus) {
        CRParticleTypes.init();
        REGISTER.register(modEventBus);
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("deprecation")
    public static <T extends ParticleOptions> void registerFactory(
            ParticleType<T> type,
            ParticleEngine engine,
            ICustomParticleData<T> customParticleData) {
        if (customParticleData instanceof ICustomParticleDataWithSprite<T> withSprite) {
            engine.register(type, withSprite.getMetaFactory());
        } else {
            engine.register(type, customParticleData.getFactory());
        }
    }
}
