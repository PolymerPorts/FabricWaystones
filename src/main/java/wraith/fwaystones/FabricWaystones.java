package wraith.fwaystones;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wraith.fwaystones.registry.*;
import wraith.fwaystones.util.Config;
import wraith.fwaystones.util.WaystoneStorage;
import wraith.fwaystones.util.WaystonesEventManager;

public class FabricWaystones implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("Fabric-Waystones");
    public static final String MOD_ID = "fwaystones";
    public static WaystoneStorage WAYSTONE_STORAGE;

    @Override
    public void onInitialize() {

        LOGGER.info("Is initializing.");
        Config.getInstance().loadConfig();
        BlockRegistry.registerBlocks();
        BlockEntityRegistry.registerBlockEntities();
        ItemRegistry.init();
        CompatRegistry.init();
        WaystonesEventManager.registerEvents();

        LOGGER.info("Has successfully been initialized.");
        LOGGER.info("If you have any issues or questions, feel free to join our Discord: https://discord.gg/vMjzgS4.");
    }

}
