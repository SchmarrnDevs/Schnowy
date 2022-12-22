package dev.schmarrn.schnowy.common;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;

public class SchnowyGameRules {
	public static final GameRules.Key<GameRules.BooleanValue> RULE_BEE = registerBoolean("schnowyBee", GameRules.Category.MOBS, true);

	private static GameRules.Key<GameRules.BooleanValue> registerBoolean(String name, GameRules.Category category, boolean value) {
		return GameRuleRegistry.register(name, category, GameRuleFactory.createBooleanRule(value));
	}

	public static final void init() {

	}
}
