package com.example.examplemod;

import com.illusivesoulworks.diet.DietCommonMod;
import com.illusivesoulworks.diet.DietConstants;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public class ExampleMod implements ModInitializer {
    
    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        DietConstants.LOG.info("Hello Fabric world!");
        DietCommonMod.init();
        
        // Some code like events require special initialization from the
        // loader specific code.
        ItemTooltipCallback.EVENT.register(DietCommonMod::onItemTooltip);
    }
}
