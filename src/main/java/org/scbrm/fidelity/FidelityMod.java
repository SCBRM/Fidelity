package org.scbrm.fidelity;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FidelityMod implements ModInitializer {
	public static final WhipItem WHIP_ITEM = new WhipItem(new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("fidelity", "whip_item"), WHIP_ITEM);
	}
}