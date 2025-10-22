package com.sitael;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sitael.config.DatabaseConfig;
import com.sitael.db.DatabaseManager;
import com.sitael.db.MessageEntity;
import com.sitael.db.MessageRepository;
import com.sitael.networking.SendProtobuffDataC2SPayload;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
	public static final String MOD_ID = "dbbutton";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static DatabaseConfig databaseConfig;
    private static DatabaseManager databaseManager;

	@Override
	public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            try {

                databaseConfig = new DatabaseConfig();
                databaseManager = new DatabaseManager(databaseConfig);
                databaseManager.initialize();

                MessageRepository.initialize(databaseManager);
                LOGGER.info("Database connection initialized");
            } catch (Exception e) {
                LOGGER.error("Failed to initialize database", e);
            }
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            MessageRepository.shutdown();
            LOGGER.info("Database connection closed");
        });

        PayloadTypeRegistry.playC2S().register(SendProtobuffDataC2SPayload.ID, SendProtobuffDataC2SPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SendProtobuffDataC2SPayload.ID, (payload, context) -> {
            try {
                Proto.Message message = Proto.Message.parseFrom(payload.data());
                LOGGER.info(message.getText());
                MessageEntity entity = new MessageEntity(context.player().getUuid(), message.getText());
                MessageRepository.saveMessage(entity);

            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
            }
        });

		LOGGER.info("Hello Fabric world!");
	}
}