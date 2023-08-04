package wraith.fwaystones.registry;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroup;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import wraith.fwaystones.FabricWaystones;
import wraith.fwaystones.item.*;
import wraith.fwaystones.util.Utils;

import java.util.HashMap;

public final class ItemRegistry {

    private static final HashMap<String, Item> ITEMS = new HashMap<>();
    //public static final ItemGroup WAYSTONE_GROUP = PolymerItemGroupUtils.builder(Utils.ID(FabricWaystones.MOD_ID)).icon(() -> new ItemStack(BlockRegistry.WAYSTONE)).entries((enabledFeatures, entries) -> ITEMS.values().stream().map(ItemStack::new).forEach(entries::add)).build();
    // Not sure how to fix this. Documentation is not current. Added all items to the Functional tab



    private ItemRegistry() {}

    private static void registerItem(String id, Item item, RegistryKey<ItemGroup> tab) {
        ITEMS.put(id, Registry.register(Registries.ITEM, Utils.ID(id), item));
    }

    public static void init() {
        if (!ITEMS.isEmpty()) {
            return;
        }
        registerItem("waystone", new WaystoneItem(BlockRegistry.WAYSTONE, new FabricItemSettings()), ItemGroups.FUNCTIONAL);
        registerItem("desert_waystone", new WaystoneItem(BlockRegistry.DESERT_WAYSTONE, new FabricItemSettings()), ItemGroups.FUNCTIONAL);
        registerItem("red_desert_waystone", new WaystoneItem(BlockRegistry.RED_DESERT_WAYSTONE, new FabricItemSettings()), ItemGroups.FUNCTIONAL);
        registerItem("stone_brick_waystone", new WaystoneItem(BlockRegistry.STONE_BRICK_WAYSTONE, new FabricItemSettings()), ItemGroups.FUNCTIONAL);
        registerItem("nether_brick_waystone", new WaystoneItem(BlockRegistry.NETHER_BRICK_WAYSTONE, new FabricItemSettings()), ItemGroups.FUNCTIONAL);
        registerItem("red_nether_brick_waystone", new WaystoneItem(BlockRegistry.RED_NETHER_BRICK_WAYSTONE, new FabricItemSettings()), ItemGroups.FUNCTIONAL);
        registerItem("end_stone_brick_waystone", new WaystoneItem(BlockRegistry.ENDSTONE_BRICK_WAYSTONE, new FabricItemSettings()), ItemGroups.FUNCTIONAL);
        registerItem("deepslate_brick_waystone", new WaystoneItem(BlockRegistry.DEEPSLATE_BRICK_WAYSTONE, new FabricItemSettings()), ItemGroups.FUNCTIONAL);
        registerItem("blackstone_brick_waystone", new WaystoneItem(BlockRegistry.BLACKSTONE_BRICK_WAYSTONE, new FabricItemSettings()), ItemGroups.FUNCTIONAL);
        registerItem("pocket_wormhole", new PocketWormholeItem(new FabricItemSettings().maxCount(1).fireproof()), ItemGroups.FUNCTIONAL);
        registerItem("abyss_watcher", new AbyssWatcherItem(new FabricItemSettings().maxCount(4).fireproof()), ItemGroups.FUNCTIONAL);
        registerItem("waystone_scroll", new WaystoneScrollItem(new FabricItemSettings().maxCount(1)), ItemGroups.FUNCTIONAL);
        registerItem("local_void", new LocalVoidItem(new FabricItemSettings().maxCount(1)), ItemGroups.FUNCTIONAL);
        registerItem("void_totem", new VoidTotem(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON)), ItemGroups.FUNCTIONAL);
        registerItem("scroll_of_infinite_knowledge", new ScrollOfInfiniteKnowledgeItem(new FabricItemSettings().maxCount(1).fireproof()), ItemGroups.FUNCTIONAL);
        registerItem("waystone_debugger", new WaystoneDebuggerItem(new FabricItemSettings().maxCount(1).fireproof().rarity(Rarity.EPIC)), ItemGroups.FUNCTIONAL);
    }

    public static Item get(String id) {
        return ITEMS.getOrDefault(id, Items.AIR);
    }

}
