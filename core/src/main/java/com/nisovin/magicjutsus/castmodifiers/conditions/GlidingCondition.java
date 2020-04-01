package com.nisovin.magicjutsus.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicjutsus.castmodifiers.Condition;

public class GlidingCondition extends Condition {

	@Override
	public boolean setVar(String var) {
		return true;
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return livingEntity.isGliding();
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return target.isGliding();
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

}