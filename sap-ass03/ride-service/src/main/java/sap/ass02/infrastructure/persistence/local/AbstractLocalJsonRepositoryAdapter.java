package sap.ass02.infrastructure.persistence.local;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.*;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.infrastructure.persistence.AbstractVerticleRepository;
import sap.ass02.infrastructure.persistence.utils.DateFormatUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Optional;

public abstract class AbstractLocalJsonRepositoryAdapter extends AbstractVerticleRepository {
    private static final Logger LOGGER = LogManager.getLogger(AbstractLocalJsonRepositoryAdapter.class);
    private final String RIDE_PATH = "rides";
    private final String databaseFolder;
    
    public AbstractLocalJsonRepositoryAdapter() {
        this.databaseFolder = "./database";
        LOGGER.trace("LocalJsonRepositoryAdapter instantiated (remember to initialize it before using it!)");
    }
    
    @Override
    public void init() {
        this.makeDir(this.databaseFolder);
        this.makeDir(this.databaseFolder + File.separator + this.RIDE_PATH);
        LOGGER.trace("LocalJsonRepositoryAdapter initialized");
    }
    
    @Override
    public Optional<EBikeDTO> getEBikeById(final String ebikeId) {
        LOGGER.trace("Retrieving eBike with ID: {}", ebikeId);
        for (RideDTO ride : this.getAllRides()) {
            if (ride.ebike().id().equals(ebikeId)) {
                return Optional.of(ride.ebike());
            }
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<UserDTO> getUserById(String userId) {
        LOGGER.trace("Retrieving user with ID: {}", userId);
        for (RideDTO ride : this.getAllRides()) {
            if (ride.user().id().equals(userId)) {
                return Optional.of(ride.user());
            }
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<RideDTO> getRideById(String userId, String ebikeId) {
        LOGGER.trace("Retrieving ride with user ID: {} and eBike ID: {}", userId, ebikeId);
        File rideDir = new File(this.databaseFolder + File.separator + this.RIDE_PATH);
        File[] files = rideDir.listFiles((dir, name) -> name.endsWith(".json"));
        
        if (files != null) {
            for (File file : files) {
                Optional<RideDTO> ride = this.getRideDTO(userId, ebikeId, file);
                if (ride != null) return ride;
            }
        }
        return Optional.empty();
    }
    
    @Override
    public void insertRide(RideDTO ride) {
        try {
            this.saveObj(this.RIDE_PATH, String.valueOf(ride.id()), ride.toJsonObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void updateRideEnd(RideDTO ride) {
        LOGGER.trace("Updating ride end: {}", ride.toString());
        File rideFile = new File(this.databaseFolder + File.separator + this.RIDE_PATH + File.separator + ride.id() + ".json");
        if (rideFile.exists()) {
            LOGGER.trace("RideDTO file found: {}", rideFile.getAbsolutePath());
            try {
                String content = new String(Files.readAllBytes(rideFile.toPath()));
                LOGGER.trace("RideDTO file content: {}", content);
                JsonObject obj = new JsonObject(content);
                ride.endDate().ifPresent(date -> obj.put(JsonFieldKey.RIDE_END_DATE_KEY, date.toString()));
                obj.put(JsonFieldKey.RIDE_ONGONING_KEY, ride.ongoing());
                LOGGER.trace("RideDTO file content updated: {}", obj.encodePrettily());
                this.saveObj(this.RIDE_PATH, String.valueOf(ride.id()), obj);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("RideDTO with ID " + ride.id() + " does not exist.");
        }
    }
    
    private Optional<RideDTO> getRideDTO(String userId, String ebikeId, File file) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            JsonObject obj = new JsonObject(content);
            if (userId.equals(obj.getString(JsonFieldKey.RIDE_USER_ID_KEY)) && ebikeId.equals(obj.getString(JsonFieldKey.RIDE_EBIKE_ID_KEY))) {
                UserDTO user = this.getUserById(userId).orElseThrow(() -> new RuntimeException("UserDTO not found"));
                EBikeDTO ebike = this.getEBikeById(ebikeId).orElseThrow(() -> new RuntimeException("EBikeDTO not found"));
                
                Date startDate = DateFormatUtils.toSqlDate(obj.getString(JsonFieldKey.RIDE_START_DATE_KEY));
                Optional<Date> endDate;
                if (obj.getString(JsonFieldKey.RIDE_END_DATE_KEY).isBlank()) {
                    endDate = Optional.empty();
                } else {
                    endDate = Optional.of(DateFormatUtils.toSqlDate(obj.getString(JsonFieldKey.RIDE_END_DATE_KEY)));
                }
                RideDTO ride = new RideDTO(
                        startDate,
                        endDate,
                        user,
                        ebike,
                        obj.getBoolean(JsonFieldKey.RIDE_ONGONING_KEY),
                        obj.getString(JsonFieldKey.RIDE_ID_KEY));
                
                return Optional.of(ride);
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    
    @Override
    public Optional<RideDTO> getRideById(String rideId) {
        LOGGER.trace("Retrieving ride with ID: {}", rideId);
        File rideFile = new File(this.databaseFolder + File.separator + this.RIDE_PATH + File.separator + rideId + ".json");
        if (rideFile.exists()) {
            LOGGER.trace("RideDTO file found: {}", rideFile.getAbsolutePath());
            try {
                JsonObject jsonRide = new JsonObject(new String(Files.readAllBytes(rideFile.toPath())));
                return Optional.of(RideDTO.fromJson(jsonRide));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            LOGGER.trace("RideDTO file '{}' not found, returning Optional.empty()", rideFile.getAbsolutePath());
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<RideDTO> getOngoingRideById(String userId, String ebikeId) {
        LOGGER.trace("Retrieving ongoing ride with user ID: {} and eBike ID: {}", userId, ebikeId);
        File rideDir = new File(this.databaseFolder + File.separator + this.RIDE_PATH);
        File[] files = rideDir.listFiles((dir, name) -> name.endsWith(".json"));
        
        if (files != null) {
            for (File file : files) {
                Optional<RideDTO> ride = this.getRideDTO(userId, ebikeId, file);
                if (ride != null) return ride;
            }
        }
        return Optional.empty();
    }
    
    @Override
    public Iterable<RideDTO> getAllRides() {
        ArrayList<RideDTO> rides = new ArrayList<>();
        File rideDir = new File(this.databaseFolder + File.separator + this.RIDE_PATH);
        File[] files = rideDir.listFiles((dir, name) -> name.endsWith(".json"));
        
        if (files != null) {
            for (File file : files) {
                try {
                    String content = new String(Files.readAllBytes(file.toPath()));
                    JsonObject obj = new JsonObject(content);
                    UserDTO user = this.getUserById(obj.getString(JsonFieldKey.RIDE_USER_ID_KEY)).get();
                    EBikeDTO ebike = this.getEBikeById(obj.getString(JsonFieldKey.RIDE_EBIKE_ID_KEY)).get();
                    RideDTO ride = this.getRideById(user.id(), ebike.id()).get();
                    rides.add(ride);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return rides;
    }
    
    private void makeDir(final String name) {
        LOGGER.trace("Creating directory: {}", name);
        try {
            File dir = new File(name);
            LOGGER.trace("About to check if directory '{}' exists", dir.getAbsolutePath());
            if (!dir.exists()) {
                LOGGER.trace("Directory '{}' does not exist. Creating it...", dir.getAbsolutePath());
                dir.mkdir();
                LOGGER.trace("Directory '{}' created", dir.getAbsolutePath());
            } else {
                LOGGER.trace("Directory '{}' already exists, skipping creation", dir.getAbsolutePath());
            }
        } catch (Exception ex) {
            LOGGER.error("Error creating directory '{}'", name, ex);
        }
    }
    
    private void saveObj(final String db, final String id, final JsonObject obj) throws FileAlreadyExistsException {
        File file = new File(this.databaseFolder + File.separator + db + File.separator + id + ".json");
//        if (file.exists()) {
//            throw new FileAlreadyExistsException("Data already present into database: " + file.getAbsolutePath());
//        }
        
        try (FileWriter fw = new FileWriter(file, false); BufferedWriter wr = new BufferedWriter(fw)) {
            wr.write(obj.encodePrettily());
            wr.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    
    public void updateEBike(EBikeDTO dto) {
        this.getAllRides().forEach(ride -> {
            if (ride.ebike().id().equals(dto.id())) {
                try {
                    JsonObject rideWithEbikeUpdated = ride.toJsonObject().put(JsonFieldKey.RIDE_EBIKE_KEY, dto.toJsonObject());
                    this.saveObj(this.RIDE_PATH, ride.id(), rideWithEbikeUpdated);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void updateUser(UserDTO dto) {
        this.getAllRides().forEach(ride -> {
            if (ride.user().id().equals(dto.id())) {
                try {
                    JsonObject rideWithUserUpdated = ride.toJsonObject().put(JsonFieldKey.RIDE_USER_KEY, dto.toJsonObject());
                    this.saveObj(this.RIDE_PATH, ride.id(), rideWithUserUpdated);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
