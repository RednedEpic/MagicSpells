package com.nisovin.magicjutsus.jutsus.passive;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;

import com.nisovin.magicjutsus.MagicJutsus;
import com.nisovin.magicjutsus.Jutsubook;
import com.nisovin.magicjutsus.jutsus.PassiveJutsu;
import com.nisovin.magicjutsus.util.OverridePriority;

// No trigger variable is used here
public class ShootListener extends PassiveListener {

	List<PassiveJutsu> jutsus = new ArrayList<>();

	@Override
	public void registerJutsu(PassiveJutsu jutsu, PassiveTrigger trigger, String var) {
		jutsus.add(jutsu);
	}
	
	@OverridePriority
	@EventHandler
	public void onShoot(final EntityShootBowEvent event) {
		if (jutsus.isEmpty()) return;
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player)event.getEntity();
		Jutsubook jutsubook = MagicJutsus.getJutsubook(player);
		for (PassiveJutsu jutsu : jutsus) {
			if (!isCancelStateOk(jutsu, event.isCancelled())) continue;
			if (!jutsubook.hasJutsu(jutsu)) continue;
			boolean casted = jutsu.activate(player, event.getForce());
			if (!PassiveListener.cancelDefaultAction(jutsu, casted)) continue;
			event.setCancelled(true);
			event.getProjectile().remove();
		}
	}
	
}