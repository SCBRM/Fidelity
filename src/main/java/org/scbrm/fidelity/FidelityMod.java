package org.scbrm.fidelity;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class FidelityMod implements ModInitializer {
	public static final Identifier WHIP_ITEM_ID = Identifier.of("fidelity", "whip_item");
	public static final WhipItem WHIP_ITEM = Registry.register(Registries.ITEM, WHIP_ITEM_ID,
			new WhipItem(
					new Item.Settings().registryKey(
							RegistryKey.of(RegistryKeys.ITEM, WHIP_ITEM_ID)).maxCount(1)));

	@Override
	public void onInitialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
				.register(itemGroup -> itemGroup.add(WHIP_ITEM));
	}
}