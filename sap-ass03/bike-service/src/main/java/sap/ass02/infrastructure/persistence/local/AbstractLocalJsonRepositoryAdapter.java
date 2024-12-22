package sap.ass02.infrastructure.persistence.local;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.P2dDTO;
import sap.ass02.domain.dto.V2dDTO;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.infrastructure.persistence.AbstractVerticleRepository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Optional;

public abstract class AbstractLocalJsonRepositoryAdapter extends AbstractVerticleRepository {
    private static final Logger LOGGER = LogManager.getLogger(AbstractLocalJsonRepositoryAdapter.class);
    private final String ebikeFolder = "ebike";
    private final String databaseFolder = "./database";

    public AbstractLocalJsonRepositoryAdapter() {
        LOGGER.trace("AbstractLocalJsonRepositoryAdapter instantiated (remember to initialize it before using it!)");
    }

    @Override
    public void init() {
        this.makeDir(this.databaseFolder);
        this.makeDir(this.databaseFolder + File.separator + this.ebikeFolder);
        LOGGER.trace("AbstractLocalJsonRepositoryAdapter initialized");
    }

    @Override
    public boolean insertEbike(EBikeDTO eBike) {
        LOGGER.trace("Inserting eBike: {}", eBike.toString());
        JsonObject obj = new JsonObject();
        obj.put(JsonFieldKey.EBIKE_ID_KEY, eBike.id());
        obj.put(JsonFieldKey.EBIKE_SPEED_KEY, eBike.speed());
        obj.put(JsonFieldKey.EBIKE_BATTERY_KEY, eBike.batteryLevel());
        obj.put(JsonFieldKey.EBIKE_STATE_KEY, eBike.state());
        obj.put(JsonFieldKey.EBIKE_X_DIRECTION_KEY, eBike.direction().x());
        obj.put(JsonFieldKey.EBIKE_Y_DIRECTION_KEY, eBike.direction().y());
        obj.put(JsonFieldKey.EBIKE_X_LOCATION_KEY, eBike.location().x());
        obj.put(JsonFieldKey.EBIKE_Y_LOCATION_KEY, eBike.location().y());
        try {
            this.saveObj(this.ebikeFolder, eBike.id(), obj);
        } catch (RuntimeException | FileAlreadyExistsException e) {
            LOGGER.warn(e.getMessage());
            return false;
        }
        return true;
    }
    
    @Override
    public Optional<EBikeDTO> getEbikeById(String ebikeId) {
        LOGGER.trace("Retrieving ebike with ID: {}", ebikeId);
        File ebikeFile = new File(this.databaseFolder + File.separator + this.ebikeFolder + File.separator + ebikeId + ".json");
        if (ebikeFile.exists()) {
            LOGGER.trace("EbikeDTO file found: {}", ebikeFile.getAbsolutePath());
            try {
                String content = new String(Files.readAllBytes(ebikeFile.toPath()));
                LOGGER.trace("EbikeDTO file content: {}", content);
                JsonObject obj = new JsonObject(content);
                LOGGER.trace("EbikeDTO file content parsed: {}", obj.encodePrettily());
                EBikeDTO retrievedEBike = new EBikeDTO(
                        obj.getString(JsonFieldKey.EBIKE_ID_KEY),
                        EBikeDTO.EBikeStateDTO.valueOf(obj.getString(JsonFieldKey.EBIKE_STATE_KEY)),
                        new P2dDTO(obj.getDouble(JsonFieldKey.EBIKE_X_LOCATION_KEY), obj.getDouble(JsonFieldKey.EBIKE_Y_LOCATION_KEY)),
                        new V2dDTO(obj.getDouble(JsonFieldKey.EBIKE_X_DIRECTION_KEY), obj.getDouble(JsonFieldKey.EBIKE_Y_DIRECTION_KEY)),
                        obj.getDouble(JsonFieldKey.EBIKE_SPEED_KEY),
                        obj.getInteger(JsonFieldKey.EBIKE_BATTERY_KEY)
                );
                LOGGER.trace("EbikeDTO retrieved: {}", retrievedEBike.toString());
                return Optional.of(retrievedEBike);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            LOGGER.trace("EbikeDTO file not found, returning '{}'", Optional.empty());
        }
        return Optional.empty();
    }
    
    @Override
    public boolean updateEBike(EBikeDTO eBike) {
        LOGGER.trace("Updating eBike: {}", eBike.toString());
        File ebikeFile = new File(this.databaseFolder + File.separator + this.ebikeFolder + File.separator + eBike.id() + ".json");
        if (ebikeFile.exists()) {
            LOGGER.trace("EBikeDTO file found: {}", ebikeFile.getAbsolutePath());
            try {
                String content = new String(Files.readAllBytes(ebikeFile.toPath()));
                LOGGER.trace("EBikeDTO file content: {}", content);
                JsonObject obj = new JsonObject(content);
                obj.put(JsonFieldKey.EBIKE_STATE_KEY, eBike.state().toString());
                obj.put(JsonFieldKey.EBIKE_X_LOCATION_KEY, eBike.location().x());
                obj.put(JsonFieldKey.EBIKE_Y_LOCATION_KEY, eBike.location().y());
                obj.put(JsonFieldKey.EBIKE_X_DIRECTION_KEY, eBike.direction().x());
                obj.put(JsonFieldKey.EBIKE_Y_DIRECTION_KEY, eBike.direction().y());
                obj.put(JsonFieldKey.EBIKE_SPEED_KEY, eBike.speed());
                obj.put(JsonFieldKey.EBIKE_BATTERY_KEY, eBike.batteryLevel());
                LOGGER.trace("EBikeDTO file content updated: {}", obj.encodePrettily());
                this.saveObj(this.ebikeFolder, eBike.id(), obj);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("EBikeDTO with ID " + eBike.id() + " does not exist.");
        }
        return true;
    }
    
    @Override
    public Iterable<EBikeDTO> getAllEBikes() {
        ArrayList<EBikeDTO> ebikes = new ArrayList<>();
        File ebikeDir = new File(this.databaseFolder + File.separator + this.ebikeFolder);
        File[] files = ebikeDir.listFiles((dir, name) -> name.endsWith(".json"));
        
        if (files != null) {
            for (File file : files) {
                String ebikeId = file.getName().replace(".json", "");
                this.getEbikeById(ebikeId).ifPresent(ebikes::add);
            }
        }
        
        return ebikes;
    }

    private void makeDir(final String name) {
        LOGGER.trace("Creating directory: {}", name);
        File dir = new File(name);

        if (dir.mkdir()) {
            LOGGER.trace("Directory '{}' created", dir.getAbsolutePath());
        } else {
            LOGGER.warn("Unable to create directory '{}', something went wrong", dir.getAbsolutePath());
        }
    }

    private void saveObj(final String db, final String id, final JsonObject obj) throws FileAlreadyExistsException {
        File file = new File(this.databaseFolder + File.separator + db + File.separator + id + ".json");

        try (FileWriter fw = new FileWriter(file, false); BufferedWriter wr = new BufferedWriter(fw)) {
            wr.write(obj.encodePrettily());
            wr.flush();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
