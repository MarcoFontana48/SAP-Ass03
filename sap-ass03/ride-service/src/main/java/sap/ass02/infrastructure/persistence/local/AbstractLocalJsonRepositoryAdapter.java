package sap.ass02.infrastructure.persistence.local;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.*;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.infrastructure.persistence.utils.DateFormatUtils;
import sap.ass02.application.Repository;

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

/**
 * Abstract class for local JSON repository adapters.
 */
public abstract class AbstractLocalJsonRepositoryAdapter extends AbstractVerticle implements Repository {
    private static final Logger LOGGER = LogManager.getLogger(AbstractLocalJsonRepositoryAdapter.class);
    private final String RIDE_PATH = "rides";
    private final String USER_PATH = "users";
    private final String EBIKE_PATH = "ebikes";
    private final String databaseFolder;
    
    /**
     * Constructor for the local JSON repository adapter.
     */
    public AbstractLocalJsonRepositoryAdapter() {
        this.databaseFolder = "./database";
        LOGGER.trace("LocalJsonRepositoryAdapter instantiated (remember to initialize it before using it!)");
    }
    
    /**
     * Initializes the local JSON repository adapter.
     */
    @Override
    public void init() {
        this.makeDir(this.databaseFolder);
        this.makeDir(this.databaseFolder + File.separator + this.RIDE_PATH);
        this.makeDir(this.databaseFolder + File.separator + this.USER_PATH);
        this.makeDir(this.databaseFolder + File.separator + this.EBIKE_PATH);
        LOGGER.trace("LocalJsonRepositoryAdapter initialized");
    }
    
    /**
     * Retrieves an eBike by its ID.
     * @param ebikeId the eBike's ID
     * @return the eBike
     */
    @Override
    public Optional<EBikeDTO> getEBikeById(final String ebikeId) {
        LOGGER.trace("Retrieving eBike with ID: {}", ebikeId);
        File ebikeFile = new File(this.databaseFolder + File.separator + this.EBIKE_PATH + File.separator + ebikeId + ".json");
        if (ebikeFile.exists()) {
            LOGGER.trace("EBikeDTO file found: {}", ebikeFile.getAbsolutePath());
            try {
                JsonObject jsonEBike = new JsonObject(new String(Files.readAllBytes(ebikeFile.toPath())));
                return Optional.of(EBikeDTO.fromJson(jsonEBike));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            LOGGER.trace("EBikeDTO file '{}' not found, returning Optional.empty()", ebikeFile.getAbsolutePath());
            return Optional.empty();
        }
    }
    
    /**
     * Retrieves a user by its ID.
     * @param userId the user's ID
     * @return the user
     */
    @Override
    public Optional<UserDTO> getUserById(String userId) {
        LOGGER.trace("Retrieving user with ID: {}", userId);
        File userFile = new File(this.databaseFolder + File.separator + this.USER_PATH + File.separator + userId + ".json");
        if (userFile.exists()) {
            LOGGER.trace("UserDTO file found: {}", userFile.getAbsolutePath());
            try {
                JsonObject jsonUser = new JsonObject(new String(Files.readAllBytes(userFile.toPath())));
                return Optional.of(UserDTO.fromJson(jsonUser));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            LOGGER.trace("UserDTO file '{}' not found, returning Optional.empty()", userFile.getAbsolutePath());
            return Optional.empty();
        }
    }
    
    /**
     * Retrieves a ride by the user's ID and the eBike's ID.
     * @param userId the user's ID
     * @param ebikeId the eBike's ID
     * @return the ride
     */
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
    
    /**
     * Inserts a ride into the database.
     * @param ride the ride to insert
     */
    @Override
    public void insertRide(RideDTO ride) {
        try {
            this.saveObj(this.RIDE_PATH, String.valueOf(ride.id()), ride.toJsonObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves a ride DTO.
     * @param userId the user's ID
     * @param ebikeId the eBike's ID
     * @param file the file
     * @return the ride DTO
     */
    private Optional<RideDTO> getRideDTO(String userId, String ebikeId, File file) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            JsonObject obj = new JsonObject(content);
            if (userId.equals(obj.getString(JsonFieldKey.RIDE_USER_ID_KEY)) && ebikeId.equals(obj.getString(JsonFieldKey.RIDE_EBIKE_ID_KEY))) {
                UserDTO user = this.getUserById(userId).orElseThrow(() -> new RuntimeException("UserDTO not found"));
                EBikeDTO ebike = this.getEBikeById(ebikeId).orElseThrow(() -> new RuntimeException("EBikeDTO not found"));
                
                Date startDate = DateFormatUtils.toSqlDate(obj.getString(JsonFieldKey.RIDE_START_DATE_KEY));
                Optional<Date> endDate;
                if (obj.getString(JsonFieldKey.RIDE_END_DATE_KEY) == null || obj.getString(JsonFieldKey.RIDE_END_DATE_KEY).isBlank()) {
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
    
    /**
     * Retrieves all rides.
     * @return the rides
     */
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
    
    /**
     * Retrieves an ongoing ride by the user's ID and the eBike's ID.
     * @param userId the user's ID
     * @param ebikeId the eBike's ID
     * @return the ongoing ride
     */
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
    
    /**
     * Retrieves all rides.
     * @return the rides
     */
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
    
    /**
     * Creates a directory.
     * @param name the directory's name
     */
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
    
    /**
     * Saves an object.
     * @param db the database
     * @param id the object's ID
     * @param obj the object
     * @throws FileAlreadyExistsException if the file already exists
     */
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
    
    /**
     * Updates an eBike.
     * @param dto the eBike DTO
     */
    public void updateEBike(EBikeDTO dto) {
        try {
            this.saveObj(this.EBIKE_PATH, dto.id(), dto.toJsonObject());
        } catch (FileAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
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
    
    /**
     * Updates a user.
     * @param dto the user DTO
     */
    public void updateUser(UserDTO dto) {
        try {
            this.saveObj(this.USER_PATH, dto.id(), dto.toJsonObject());
        } catch (FileAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
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
    
    /**
     * Inserts a user.
     * @param user the user
     */
    public void insertUser(UserDTO user) {
        try {
            this.saveObj(this.USER_PATH, user.id(), user.toJsonObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Inserts an eBike.
     * @param ebike the eBike
     */
    public void insertEbike(EBikeDTO ebike) {
        try {
            this.saveObj(this.EBIKE_PATH, ebike.id(), ebike.toJsonObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
