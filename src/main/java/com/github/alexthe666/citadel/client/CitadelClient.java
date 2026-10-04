package com.github.alexthe666.citadel.client;

import com.github.alexthe666.citadel.Citadel;

import net.fabricmc.api.ClientModInitializer;

public class CitadelClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Citadel.PROXY.onClientInit();
        new ClientEvents();
    }
}
