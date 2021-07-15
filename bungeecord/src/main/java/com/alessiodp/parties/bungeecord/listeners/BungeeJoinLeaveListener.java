package com.alessiodp.parties.bungeecord.listeners;

import com.alessiodp.core.bungeecord.user.BungeeUser;
import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.bungeecord.messaging.BungeePartiesMessageDispatcher;
import com.alessiodp.parties.common.PartiesPlugin;
import com.alessiodp.parties.common.listeners.JoinLeaveListener;
import com.alessiodp.parties.common.parties.objects.PartyImpl;
import com.alessiodp.parties.common.players.objects.PartyPlayerImpl;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class BungeeJoinLeaveListener extends JoinLeaveListener implements Listener {
	
	public BungeeJoinLeaveListener(PartiesPlugin instance) {
		super(instance);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PostLoginEvent event) {
		super.onPlayerJoin(new BungeeUser(plugin, event.getPlayer()));
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerDisconnectEvent event) {
		super.onPlayerQuit(new BungeeUser(plugin, event.getPlayer()));
	}
	
	@Override
	protected void onJoinComplete(PartyPlayerImpl partyPlayer) {
		// Ensure player is not in a null party
		antiNullParty:
		{
			PartiesAPI api = Parties.getApi();
			try {
				if (api.isPlayerInParty(partyPlayer.getPlayerUUID())) {
					if (partyPlayer.getPartyId() == null) {
						leaveParty(partyPlayer);
						break antiNullParty;
					}
					if (api.getParty(partyPlayer.getPartyId()) == null) {
						leaveParty(partyPlayer);
						break antiNullParty;
					}

					// Attempt to access smfn that will be null -> error -> party leave

					PartyImpl party = (PartyImpl) api.getParty(partyPlayer.getPartyId());
					String lower = party.getName().toLowerCase();
					String partyID = party.getId().toString();

					if (lower == null || partyID == null) {
						leaveParty(partyPlayer);
						break antiNullParty;
					}
				}
			} catch (Exception e) {
				leaveParty(partyPlayer);
			}
		}

		((BungeePartiesMessageDispatcher) plugin.getMessenger().getMessageDispatcher()).sendLoadPlayer(partyPlayer);
	}
	
	@Override
	protected void onLeaveComplete(PartyPlayerImpl partyPlayer) {
		((BungeePartiesMessageDispatcher) plugin.getMessenger().getMessageDispatcher()).sendUnloadPlayer(partyPlayer);
	}


	private void leaveParty(PartyPlayerImpl partyPlayer) {
		partyPlayer.removeFromParty(true);
	}
}
