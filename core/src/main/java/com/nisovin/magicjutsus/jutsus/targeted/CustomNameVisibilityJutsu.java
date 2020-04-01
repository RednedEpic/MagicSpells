package com.nisovin.magicjutsus.jutsus.targeted;

import org.bukkit.entity.LivingEntity;

import com.nisovin.magicjutsus.util.TargetInfo;
import com.nisovin.magicjutsus.util.MagicConfig;
import com.nisovin.magicjutsus.jutsus.TargetedJutsu;
import com.nisovin.magicjutsus.util.TargetBooleanState;
import com.nisovin.magicjutsus.jutsus.TargetedEntityJutsu;

public class CustomNameVisibilityJutsu extends TargetedJutsu implements TargetedEntityJutsu {
	
	private TargetBooleanState targetBooleanState;
	
	public CustomNameVisibilityJutsu(MagicConfig config, String jutsuName) {
		super(config, jutsuName);
		
		targetBooleanState = TargetBooleanState.getFromName(getConfigString("target-state", "toggle"));
	}
	
	@Override
	public PostCastAction castJutsu(LivingEntity livingEntity, JutsuCastState state, float power, String[] args) {
		if (state == JutsuCastState.NORMAL) {
			TargetInfo<LivingEntity> targetInfo = getTargetedEntity(livingEntity, power);
			if (targetInfo == null) return noTarget(livingEntity);
			LivingEntity target = targetInfo.getTarget();
			if (target == null) return noTarget(livingEntity);

			target.setCustomNameVisible(targetBooleanState.getBooleanState(target.isCustomNameVisible()));
		}
		return PostCastAction.HANDLE_NORMALLY;
	}
	
	@Override
	public boolean castAtEntity(LivingEntity caster, LivingEntity target, float power) {
		target.setCustomNameVisible(targetBooleanState.getBooleanState(target.isCustomNameVisible()));
		return true;
	}
	
	@Override
	public boolean castAtEntity(LivingEntity target, float power) {
		target.setCustomNameVisible(targetBooleanState.getBooleanState(target.isCustomNameVisible()));
		return true;
	}
	
}