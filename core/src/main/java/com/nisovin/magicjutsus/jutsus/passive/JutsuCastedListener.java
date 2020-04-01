package com.nisovin.magicjutsus.jutsus.passive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.nisovin.magicjutsus.MagicJutsus;
import com.nisovin.magicjutsus.Jutsu;
import com.nisovin.magicjutsus.Jutsu.PostCastAction;
import com.nisovin.magicjutsus.Jutsu.JutsuCastState;
import com.nisovin.magicjutsus.Jutsubook;
import com.nisovin.magicjutsus.events.JutsuCastedEvent;
import com.nisovin.magicjutsus.jutsus.PassiveJutsu;
import com.nisovin.magicjutsus.util.OverridePriority;

// Optional trigger variable of comma separated list of internal jutsu names to accept
public class JutsuCastedListener extends PassiveListener {

	Map<Jutsu, List<PassiveJutsu>> jutsus = new HashMap<>();
	List<PassiveJutsu> anyJutsu = new ArrayList<>();
			
	@Override
	public void registerJutsu(PassiveJutsu jutsu, PassiveTrigger trigger, String var) {
		if (var == null || var.isEmpty()) {
			anyJutsu.add(jutsu);
		} else {
			String[] split = var.split(",");
			for (String s : split) {
				Jutsu sp = MagicJutsus.getJutsuByInternalName(s.trim());
				if (sp == null) continue;
				List<PassiveJutsu> passives = jutsus.computeIfAbsent(sp, p -> new ArrayList<>());
				passives.add(jutsu);
			}
		}
	}
	
	@OverridePriority
	@EventHandler
	public void onJutsuCast(JutsuCastedEvent event) {
		LivingEntity caster = event.getCaster();
		if (!(caster instanceof Player)) return;
		if (event.getJutsuCastState() == JutsuCastState.NORMAL && event.getPostCastAction() != PostCastAction.ALREADY_HANDLED && event.getCaster() != null) {
			Jutsubook jutsubook = MagicJutsus.getJutsubook((Player) caster);
			for (PassiveJutsu jutsu : anyJutsu) {
				if (jutsu.equals(event.getJutsu())) continue;
				if (!jutsubook.hasJutsu(jutsu, false)) continue;
				jutsu.activate((Player) caster);
			}
			List<PassiveJutsu> list = jutsus.get(event.getJutsu());
			if (list != null) {
				for (PassiveJutsu jutsu : list) {
					if (jutsu.equals(event.getJutsu())) continue;
					if (!jutsubook.hasJutsu(jutsu, false)) continue;
					jutsu.activate((Player) caster);
				}
			}
		}
	}

}